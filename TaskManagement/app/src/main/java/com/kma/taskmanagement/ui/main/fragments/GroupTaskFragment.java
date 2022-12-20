package com.kma.taskmanagement.ui.main.fragments;

import android.content.DialogInterface;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.taskmanagement.R;
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

public class GroupTaskFragment extends Fragment {

    private LinearLayout llAnimation;
    private RecyclerView groupTaskRecycler;
    private TextView tvAddGroup, tvGroupTitle;
    private GroupAdapter groupAdapter;
    private GroupViewModel groupViewModel;
    private GroupRepository groupRepository = new GroupRepositoryImpl();
    private TaskViewModel taskViewModel;
    private TaskRepository taskRepository = new TaskRepositoryImpl();
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
                final int position = viewHolder.getAbsoluteAdapterPosition();
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
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(groupTaskRecycler);

    }
}