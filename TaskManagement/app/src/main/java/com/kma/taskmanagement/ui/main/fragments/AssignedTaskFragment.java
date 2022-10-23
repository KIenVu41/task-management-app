package com.kma.taskmanagement.ui.main.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.listener.HandleClickListener;
import com.kma.taskmanagement.ui.adpater.AssignedAdapter;
import com.kma.taskmanagement.ui.adpater.GroupAdapter;
import com.kma.taskmanagement.ui.adpater.TaskAdapter;
import com.kma.taskmanagement.ui.common.CustomSpinner;
import com.kma.taskmanagement.ui.dialog.AddGroupDialog;
import com.kma.taskmanagement.ui.main.GroupViewModel;
import com.kma.taskmanagement.ui.main.GroupViewModelFactory;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.kma.taskmanagement.ui.main.TaskViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.List;

public class AssignedTaskFragment extends Fragment {

    private LinearLayout llAnimation;
    private RecyclerView assignTaskRecycler;
    private AssignedAdapter assignedAdapter;
    private TaskViewModel taskViewModel;
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    private String token = "";
    Dialog filterDialog;
    ImageView ivFilter;
    TextView okay_text, cancel_text;
    CustomSpinner spinnerPrio, spinnerStatus;
    List<Task> taskList = new ArrayList<>();
    String[] prios = new String[]{"", "LOWEST", "MEDIUM", "HIGH"};
    String[] statuss = new String[]{"", "TODO", "DOING", "COMPLETED"};

    public AssignedTaskFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static AssignedTaskFragment newInstance() {
        AssignedTaskFragment fragment = new AssignedTaskFragment();
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
        taskViewModel.getAssign(Constants.BEARER + token);
        return inflater.inflate(R.layout.fragment_assigned_task, container, false);
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
                if (s != null) {
                    Log.d("TAG", s);
                }
            }
        });
        taskViewModel.getResult().observe(getActivity(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks.size() != 0 && tasks != null) {
                    llAnimation.setVisibility(View.GONE);
                    assignTaskRecycler.setVisibility(View.VISIBLE);
                    taskList = tasks;
                    assignedAdapter.setList(taskList);
                } else {
                    llAnimation.setVisibility(View.VISIBLE);
                    assignTaskRecycler.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initView(View view) {
        llAnimation = view.findViewById(R.id.llAnimationAssigned);
        assignTaskRecycler = view.findViewById(R.id.assignTaskRecycler);
        ivFilter = view.findViewById(R.id.ivAssignFilter);
        filterDialog = new Dialog(getActivity());
    }

    private void setOnClick() {
        ivFilter.setOnClickListener(view -> {
            openFilterDialog();
        });
    }

    private void setAdapter() {
        assignedAdapter = new AssignedAdapter(getActivity(), taskList);
        assignTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        assignTaskRecycler.setAdapter(assignedAdapter);
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
                    taskViewModel.getAssignByStatus(Constants.BEARER + token, status);
                } else if(status.equals("") && !prio.equals("")) {
                    taskViewModel.getAssignByPrio(Constants.BEARER + token, prio);
                } else if(!status.equals("") && !prio.equals("")) {
                    taskViewModel.getAssignByPrioAndStatus(Constants.BEARER + token, prio, status);
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
                final int position = viewHolder.getAdapterPosition();
                final Task task = assignedAdapter.taskList.get(position);

                taskViewModel.deleteTask(Constants.BEARER + token, task.getId());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        taskViewModel.getAssign(Constants.BEARER + token);
                    }
                },500);
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(assignTaskRecycler);

    }

}