package com.kma.taskmanagement.ui.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.kma.taskmanagement.data.remote.request.InviteRequest;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.ui.dialog.AddGroupDialog;
import com.kma.taskmanagement.ui.dialog.UpdateInfoDialog;
import com.kma.taskmanagement.ui.main.GroupViewModel;
import com.kma.taskmanagement.ui.main.GroupViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.learnoset.materialdialogs.AppUpdateDialog;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;

public class GroupTaskFragment extends Fragment {

    private LinearLayout llAnimation;
    private RecyclerView groupTaskRecycler;
    private TextView tvAddGroup;
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

        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        //groupViewModel.getInvites(Constants.BEARER + token);
//        groupViewModel.getInviteResponse().observe(getActivity(), new Observer<List<InviteRequest>>() {
//            @Override
//            public void onChanged(List<InviteRequest> inviteRequests) {
//                if (inviteRequests != null) {
//                    Log.d("TAG", "invite " + inviteRequests.toString());
//                    openDialog();
//                }
//            }
//        });
//        groupViewModel.getInviteResponse().observeForever(new Observer<List<InviteRequest>>() {
//            @Override
//            public void onChanged(List<InviteRequest> inviteRequests) {
//                if (inviteRequests != null) {
//                    Log.d("TAG", "invite " + inviteRequests.toString());
//                    openDialog();
//                }
//            }
//        });
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
}