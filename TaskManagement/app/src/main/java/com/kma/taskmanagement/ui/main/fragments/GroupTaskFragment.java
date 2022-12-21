package com.kma.taskmanagement.ui.main.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.biometric.BiometricCallback;
import com.kma.taskmanagement.biometric.BiometricManager;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.listener.HandleClickListener;
import com.kma.taskmanagement.ui.adapter.FragmentGroupTaskAdapter;
import com.kma.taskmanagement.ui.adapter.GroupAdapter;
import com.kma.taskmanagement.ui.dialog.AddGroupDialog;
import com.kma.taskmanagement.ui.main.GroupViewModel;
import com.kma.taskmanagement.ui.main.GroupViewModelFactory;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.kma.taskmanagement.ui.main.TaskViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.SwipeToDeleteCallback;

import java.util.List;

public class GroupTaskFragment extends Fragment implements BiometricCallback {

    private LinearLayout llAnimation;
    private RecyclerView groupTaskRecycler;
    private TextView tvAddGroup, tvGroupTitle;
    private GroupAdapter groupAdapter;
    private GroupViewModel groupViewModel;
    private GroupRepository groupRepository = new GroupRepositoryImpl();
    private TaskViewModel taskViewModel;
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    private BiometricManager mBiometricManager;
    private int position = 0;
    private static FragmentGroupTaskAdapter.FirstPageFragmentListener mFirstPageFragmentListener;
    private String token = "";

    public GroupTaskFragment() {
        // Required empty public constructor
    }

    public static GroupTaskFragment newInstance() {
        GroupTaskFragment fragment = new GroupTaskFragment();
        //mFirstPageFragmentListener = firstPageFragmentListener;
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
        return inflater.inflate(R.layout.fragment_group_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupViewModel =  new ViewModelProvider(this, new GroupViewModelFactory(groupRepository)).get(GroupViewModel.class);
        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);
        initView(view);
        setOnClick();
        setAdapter();
        enableSwipeToDelete();

        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        groupViewModel.getGroups(token);

        groupViewModel.getGroupResponse().observe(getActivity(), new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groups) {
                if(groups.size() != 0) {
                    GlobalInfor.groups = groups;
                    llAnimation.setVisibility(View.GONE);
                    groupTaskRecycler.setVisibility(View.VISIBLE);
                    groupAdapter.submitList(groups);
                } else {
                    llAnimation.setVisibility(View.VISIBLE);
                    groupTaskRecycler.setVisibility(View.GONE);
                }
                groupAdapter.submitList(groups);
            }
        });

        taskViewModel.getAssignResult().observe(getActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks.size() != 0) {
                    FragmentTransaction transaction = getChildFragmentManager()
                            .beginTransaction();

                    transaction.replace(R.id.root_frame, GroupAssignFragment.newInstance(tasks));
                    transaction.addToBackStack("assign");

                    transaction.commit();
                } else {
                    Toast.makeText(getContext(), "Nhóm không có công việc được giao", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView(View view) {
        llAnimation = view.findViewById(R.id.llAnimation);
        groupTaskRecycler = view.findViewById(R.id.groupTaskRecycler);
        tvAddGroup = view.findViewById(R.id.addGroupTask);
        tvGroupTitle = view.findViewById(R.id.tvGroupTitle);
    }

    private void setOnClick() {
        tvAddGroup.setOnClickListener(view -> {
           AddGroupDialog addGroupDialog = new AddGroupDialog();
           addGroupDialog.show(getChildFragmentManager(), "add group dialog");
        });

//        llAnimation.setOnClickListener(view -> {
//            mFirstPageFragmentListener.onSwitchToNextFragment();
//        });
    }

    private void setAdapter() {
        groupAdapter = new GroupAdapter(Group.itemCallback, getActivity(), new HandleClickListener() {
            @Override
            public void onLongClick(View view) {

            }

            @Override
            public void onTaskClick(Task task, String status) {
            }

            @Override
            public void onGroupClick(Group group) {
                taskViewModel.getAllTasksByGroupId(token, (int) group.getId());
            }
        });
        groupTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        groupTaskRecycler.setAdapter(groupAdapter);
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity().getApplicationContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int typeSecure = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getIntFromSharedPreferences(Constants.SECURE + GlobalInfor.username);
                position = viewHolder.getAbsoluteAdapterPosition();
                if(typeSecure == 1) {
                    mBiometricManager = new BiometricManager.BiometricBuilder(requireActivity())
                            .setTitle(getString(R.string.biometric_title))
                            .setSubtitle(getString(R.string.biometric_subtitle))
                            .setDescription(getString(R.string.biometric_description))
                            .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                            .build();
                    mBiometricManager.authenticate(GroupTaskFragment.this);
                } else {
                    final Group group = groupAdapter.getCurrentList().get(position);
                    if(!group.getLeader_name().equals(GlobalInfor.username)) {
                        Toast.makeText(getActivity(), "Bạn không phải leader", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    builder.setMessage("Xóa nhóm?")
                            .setTitle("Xác nhận");

                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            groupViewModel.delete(token, group.getId());

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    groupViewModel.getGroups(token);
                                }
                            },1000);
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            groupViewModel.getGroups(token);
                        }
                    });


                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(groupTaskRecycler);

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
        final Group group = groupAdapter.getCurrentList().get(position);
        if(!group.getLeader_name().equals(GlobalInfor.username)) {
            Toast.makeText(getActivity(), "Bạn không phải leader", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Xóa nhóm?")
                .setTitle("Xác nhận");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                groupViewModel.delete(token, group.getId());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        groupViewModel.getGroups(token);
                    }
                },1000);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                groupViewModel.getGroups(token);
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