package com.kma.taskmanagement.ui.main.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.biometric.BiometricCallback;
import com.kma.taskmanagement.biometric.BiometricManager;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.listener.HandleClickListener;
import com.kma.taskmanagement.ui.adapter.MyTaskAdapter;
import com.kma.taskmanagement.ui.common.CustomSpinner;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.kma.taskmanagement.ui.main.TaskViewModelFactory;
import com.kma.taskmanagement.ui.user.LoginActivity;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTaskFragment extends Fragment implements BiometricCallback {

    private LinearLayout llAnimation;
    private RecyclerView myTaskRecycler;
    private MyTaskAdapter myTaskAdapter;
    private TaskViewModel taskViewModel;
    private ProgressDialog progressDialog;
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    private String token = "";
    private BiometricManager mBiometricManager;
    private int position = 0;
    Dialog filterDialog;
    ImageView ivFilter;
    TextView okay_text, cancel_text;
    CustomSpinner spinnerPrio, spinnerStatus;
    List<Task> taskList = new ArrayList<>();
    String[] prios = new String[]{"", "LOWEST", "MEDIUM", "HIGH"};
    String[] statuss = new String[]{"", "TODO", "DOING", "COMPLETED"};

    public MyTaskFragment() {
        // Required empty public constructor
    }


    public static MyTaskFragment newInstance() {
        MyTaskFragment fragment = new MyTaskFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);
        taskViewModel.getAllTasks(token);
        return inflater.inflate(R.layout.fragment_my_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        setOnClick();
        setAdapter();
        enableSwipeToDelete();

        taskViewModel.getResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("Hoàn thành")) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                        }
                    });
                }
                progressDialog.setMessage(s);
                progressDialog.show();
            }
        });
        taskViewModel.getResult().observe(getActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks.size() != 0) {
                    llAnimation.setVisibility(View.GONE);
                    myTaskRecycler.setVisibility(View.VISIBLE);
                    taskList = tasks;
                    myTaskAdapter.setList(taskList);
                } else {
                    llAnimation.setVisibility(View.VISIBLE);
                    myTaskRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initView(View view) {
        llAnimation = view.findViewById(R.id.llAnimationMyTask);
        myTaskRecycler = view.findViewById(R.id.myTaskTaskRecycler);
        ivFilter = view.findViewById(R.id.ivMyTaskFilter);
        filterDialog = new Dialog(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
    }

    private void setOnClick() {
        ivFilter.setOnClickListener(view -> {
            openFilterDialog();
        });
    }

    private void setAdapter() {
        myTaskAdapter = new MyTaskAdapter(getActivity(), taskList, new HandleClickListener() {
            @Override
            public void onLongClick(View view) {

            }

            @Override
            public void onTaskClick(Task task, String status) {
                task.setStatus(status);
                taskViewModel.update(token, task.getId(), task);
            }

            @Override
            public void onGroupClick(Group group) {

            }
        });
        myTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        myTaskRecycler.setAdapter(myTaskAdapter);
    }

    private void openFilterDialog() {
        filterDialog.setContentView(R.layout.layout_filter_dialog);
        filterDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        filterDialog.setCancelable(false);

        okay_text = filterDialog.findViewById(R.id.okay_text);
        cancel_text = filterDialog.findViewById(R.id.cancel_text);
        spinnerPrio = filterDialog.findViewById(R.id.filterPrio);
        spinnerStatus = filterDialog.findViewById(R.id.filterStatus);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, statuss);
        spinnerStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> prioAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, prios);
        spinnerPrio.setAdapter(prioAdapter);

        okay_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = spinnerStatus.getSelectedItem().toString();
                String prio = spinnerPrio.getSelectedItem().toString();
                if(!status.equals("") && prio.equals("")) {
                    taskViewModel.getAllTasksByStatus(token, status);
                } else if(status.equals("") && !prio.equals("")) {
                    taskViewModel.getAllTasksByPrio(token, prio);
                } else if(!status.equals("") && !prio.equals("")) {
                    taskViewModel.getAllTasksByStatusAndPrio(token, prio, status);
                }
                filterDialog.dismiss();
            }
        });

        cancel_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.dismiss();
            }
        });

        filterDialog.show();
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity().getApplicationContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int typeSecure = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getIntFromSharedPreferences(Constants.SECURE + GlobalInfor.username);
                position = viewHolder.getAbsoluteAdapterPosition();
                if (typeSecure == 1) {
                    mBiometricManager = new BiometricManager.BiometricBuilder(requireActivity())
                            .setTitle(getString(R.string.biometric_title))
                            .setSubtitle(getString(R.string.biometric_subtitle))
                            .setDescription(getString(R.string.biometric_description))
                            .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                            .build();
                    mBiometricManager.authenticate(MyTaskFragment.this);
                } else {
                    final Task task = myTaskAdapter.taskList.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage("Xóa việc?")
                            .setTitle("Xác nhận");

                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            taskViewModel.deleteTask(token, task.getId());

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    taskViewModel.getAllTasks(token);
                                }
                            },1000);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            taskViewModel.getAllTasks(token);
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(myTaskRecycler);

    }

    @Override
    public void onSdkVersionNotSupported() {
        Toast.makeText(requireActivity(), getString(R.string.biometric_error_sdk_not_supported), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationNotSupported() {
        Toast.makeText(requireActivity(), getString(R.string.biometric_error_hardware_not_supported), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {
        Toast.makeText(requireActivity(), getString(R.string.biometric_error_fingerprint_not_available), Toast.LENGTH_LONG).show();

        AlertDialog.Builder dialog = new AlertDialog.Builder(requireActivity());
        dialog.setCancelable(false);
        dialog.setTitle(getResources().getString(R.string.prompt_title));
        dialog.setMessage(getResources().getString(R.string.prompt_message));
        dialog.setPositiveButton(getResources().getString(R.string.enable), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Intent intent = new Intent(Settings.ACTION_FINGERPRINT_ENROLL);
                    startActivityForResult(intent, Constants.REQUESTCODE_FINGERPRINT_ENROLLMENT);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    startActivityForResult(intent, Constants.REQUESTCODE_SECURITY_SETTINGS);
                }
            }
        })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(requireActivity(), getString(R.string.biometric_error_permission_not_granted), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {
        Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(requireActivity(), getString(R.string.biometric_failure), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationCancelled() {
        Toast.makeText(requireActivity(), getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show();
        mBiometricManager.cancelAuthentication();
    }

    @Override
    public void onAuthenticationSuccessful(BiometricPrompt.AuthenticationResult result) {
        final Task task = myTaskAdapter.taskList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Xóa việc?")
                .setTitle("Xác nhận");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                taskViewModel.deleteTask(token, task.getId());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        taskViewModel.getAllTasks(token);
                    }
                },1000);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                taskViewModel.getAllTasks(token);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(requireActivity(), helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Toast.makeText(requireActivity(), errString, Toast.LENGTH_LONG).show();
    }
}