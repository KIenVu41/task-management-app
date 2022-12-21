package com.kma.taskmanagement.ui.main.fragments;

import static android.content.Context.ALARM_SERVICE;
import static org.webrtc.ContextUtils.getApplicationContext;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.biometric.BiometricCallback;
import com.kma.taskmanagement.biometric.BiometricManager;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.local.ExpandableListDataPump;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.listener.OnItemClick;
import com.kma.taskmanagement.ui.adapter.MyExpandableListAdapter;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.kma.taskmanagement.ui.main.TaskViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAssignFragment extends Fragment implements BiometricCallback {

    private static final String ARG_PARAM1 = "items";
    private TaskViewModel taskViewModel;
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    private String token = "";
    private ProgressDialog progressDialog;
    private BiometricManager mBiometricManager;
    private String mName = "";
    private List<String> groupList;
    private List<String> childList;
    private Map<String, List<String>> assignCollection;
    private ExpandableListView expandableListView;
    private ExpandableListAdapter expandableListAdapter;
    private List<Task> mParam1;
    private ImageView ivBack;
    public GroupAssignFragment() {
        // Required empty public constructor
    }

    public static GroupAssignFragment newInstance(List<Task> param1) {
        GroupAssignFragment fragment = new GroupAssignFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, (Serializable) param1);
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupAssignFragment newInstance() {
        GroupAssignFragment fragment = new GroupAssignFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = (List<Task>) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);
        return inflater.inflate(R.layout.fragment_group_assign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createGroupList();
        createCollection();
        ivBack = view.findViewById(R.id.ivBack);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);
        ivBack.setOnClickListener(view1 -> {
            if(getFragmentManager().getBackStackEntryCount() > 0){
                getFragmentManager().popBackStackImmediate();
            }
            else{
                getActivity().onBackPressed();
            }
        });
        expandableListView = view.findViewById(R.id.expandableListView);
        expandableListAdapter = new MyExpandableListAdapter(getActivity(), groupList, assignCollection, new OnItemClick() {
            @Override
            public void onClick(String name) {
                int typeSecure = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getIntFromSharedPreferences(Constants.SECURE + GlobalInfor.username);
                mName = name;
                if(typeSecure == 1) {
                    mBiometricManager = new BiometricManager.BiometricBuilder(requireActivity())
                            .setTitle(getString(R.string.biometric_title))
                            .setSubtitle(getString(R.string.biometric_subtitle))
                            .setDescription(getString(R.string.biometric_description))
                            .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                            .build();
                    mBiometricManager.authenticate(GroupAssignFragment.this);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Task task = mParam1.stream().filter(item -> item.getName().equals(mName)).findAny()
                                .orElse(null);
                        taskViewModel.deleteTask(token, task.getId());
                    }
                }
            }
        });
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;
            @Override
            public void onGroupExpand(int i) {
                if(lastExpandedPosition != -1 && i != lastExpandedPosition){
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = i;
            }
        });
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String selected = expandableListAdapter.getChild(i,i1).toString();
                AssignTaskBottomSheetFragment createTaskBottomSheetFragment = new AssignTaskBottomSheetFragment();
                createTaskBottomSheetFragment.setAssigner(groupList);
                createTaskBottomSheetFragment.setGroupId((int) mParam1.get(0).getGroup_output_dto().getId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    createTaskBottomSheetFragment.setTask(mParam1.stream().filter(item -> item.getName().equals(selected)).findAny()
                            .orElse(null));
                }
                createTaskBottomSheetFragment.show(((AppCompatActivity) getActivity()).getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
                return true;
            }
        });
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
    }

    private void createCollection() {
        assignCollection = new HashMap<String, List<String>>();

        for(String name: groupList) {
            childList = new ArrayList<>();
            for(Task task : mParam1) {
                if(name.equals(task.getPerformer_name())) {
                    childList.add(task.getName());
                }
            }
            assignCollection.put(name, childList);
        }

    }

    private void createGroupList() {
        groupList = new ArrayList<>();
        for(Task task: mParam1) {
            if(!groupList.contains(task.getPerformer_name())) {
                groupList.add(task.getPerformer_name());
            }
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Task task = mParam1.stream().filter(item -> item.getName().equals(mName)).findAny()
                    .orElse(null);
            taskViewModel.deleteTask(token, task.getId());
        }
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