package com.kma.taskmanagement.ui.main.fragments;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.data.local.ExpandableListDataPump;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.ui.adapter.MyExpandableListAdapter;

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
    List<String> groupList;
    List<String> childList;
    Map<String, List<String>> mobileCollection;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    private List<Task> mParam1;
    private TextView tvBack;
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
        return inflater.inflate(R.layout.fragment_group_assign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        createGroupList();
        createCollection();
        tvBack = view.findViewById(R.id.tvBack);
        tvBack.setOnClickListener(view1 -> {
            if(getFragmentManager().getBackStackEntryCount() > 0){
                getFragmentManager().popBackStackImmediate();
            }
            else{
                getActivity().onBackPressed();
            }
        });
        expandableListView = view.findViewById(R.id.expandableListView);
        expandableListAdapter = new MyExpandableListAdapter(getActivity(), groupList, mobileCollection);
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
                return true;
            }
        });
    }

    private void createCollection() {
//        String[] samsungModels = {"Samsung Galaxy M21", "Samsung Galaxy F41",
//                "Samsung Galaxy M51", "Samsung Galaxy A50s"};
//        String[] googleModels = {"Pixel 4 XL", "Pixel 3a", "Pixel 3 XL", "Pixel 3a XL",
//                "Pixel 2", "Pixel 3"};
//        String[] redmiModels = {"Redmi 9i", "Redmi Note 9 Pro Max", "Redmi Note 9 Pro"};
//        String[] vivoModels = {"Vivo V20", "Vivo S1 Pro", "Vivo Y91i", "Vivo Y12"};
//        String[] nokiaModels = {"Nokia 5.3", "Nokia 2.3", "Nokia 3.1 Plus"};
//        String[] motorolaModels = { "Motorola One Fusion+", "Motorola E7 Plus", "Motorola G9"};
        mobileCollection = new HashMap<String, List<String>>();

        for(String name: groupList) {
            childList = new ArrayList<>();
            for(Task task : mParam1) {
                if(name.equals(task.getPerformer_name())) {
                    childList.add(task.getName());
                }
            }
            mobileCollection.put(name, childList);
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