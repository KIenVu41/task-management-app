package com.kma.taskmanagement.ui.main.fragments;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupAssignFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupAssignFragment extends Fragment {

    private static final String ARG_PARAM1 = "items";
    private TaskViewModel taskViewModel;
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    private String token = "";
    private ProgressDialog progressDialog;
    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> assignCollection;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Task task = mParam1.stream().filter(item -> item.getName().equals(name)).findAny()
                            .orElse(null);
                    taskViewModel.deleteTask(token, task.getId());
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
                Toast.makeText(TaskApplication.getAppContext(), "Selected: " + selected, Toast.LENGTH_SHORT).show();
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

    private void loadChild(String[] mobileModels) {
        childList = new ArrayList<>();
        for(String model : mobileModels){
            childList.add(model);
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
}