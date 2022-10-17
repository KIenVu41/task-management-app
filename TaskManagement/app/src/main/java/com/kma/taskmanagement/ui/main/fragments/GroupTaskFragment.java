package com.kma.taskmanagement.ui.main.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
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
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.remote.request.InviteRequest;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.ui.adpater.GroupAdapter;
import com.kma.taskmanagement.ui.dialog.AddGroupDialog;
import com.kma.taskmanagement.ui.dialog.UpdateInfoDialog;
import com.kma.taskmanagement.ui.main.GroupViewModel;
import com.kma.taskmanagement.ui.main.GroupViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.SwipeToDeleteCallback;
import com.learnoset.materialdialogs.AppUpdateDialog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;

public class GroupTaskFragment extends Fragment {

    private LinearLayout llAnimation;
    private RecyclerView groupTaskRecycler;
    private TextView tvAddGroup;
    private GroupAdapter groupAdapter;
    private GroupViewModel groupViewModel;
    private GroupRepository groupRepository = new GroupRepositoryImpl();
    private String token = "";

    public GroupTaskFragment() {
        // Required empty public constructor
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
        initView(view);
        setOnClick();
        setAdapter();
        enableSwipeToDelete();

        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        groupViewModel.getGroups(Constants.BEARER + token);

        groupViewModel.getGroupResponse().observe(getActivity(), new Observer<List<Group>>() {
            @Override
            public void onChanged(List<Group> groups) {
                Log.d("TAG", groups.toString());
                if(groups.size() != 0) {
                    Log.d("TAG", groups.toString());
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
    }

    private void initView(View view) {
        llAnimation = view.findViewById(R.id.llAnimation);
        groupTaskRecycler = view.findViewById(R.id.groupTaskRecycler);
        tvAddGroup = view.findViewById(R.id.addGroupTask);
    }

    private void setOnClick() {
        tvAddGroup.setOnClickListener(view -> {
           AddGroupDialog addGroupDialog = new AddGroupDialog();
           addGroupDialog.show(getChildFragmentManager(), "add group dialog");
        });
    }

    private void setAdapter() {
        groupAdapter = new GroupAdapter( Group.itemCallback , getActivity());
        groupTaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        groupTaskRecycler.setAdapter(groupAdapter);
    }

    private void enableSwipeToDelete() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity().getApplicationContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAbsoluteAdapterPosition();
                final Group group = groupAdapter.getCurrentList().get(position);

                groupViewModel.delete(Constants.BEARER + token, group.getId());
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(groupTaskRecycler);

    }
}