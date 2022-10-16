package com.kma.taskmanagement.ui.main.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.dialog.UpdateInfoDialog;
import com.kma.taskmanagement.ui.user.LoginActivity;
import com.kma.taskmanagement.ui.user.UserViewModel;
import com.kma.taskmanagement.ui.user.UserViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.DateUtils;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

public class SettingFragment extends Fragment {

    TextView tvInDate, tv3Day, tv7Day, tvUsername, tvSex, tvPhone, tvEmail, tvEdit;
    Button btnLogout;
    private UserViewModel userViewModel;
    private UserRepository userRepository = new UserRepositoryImpl();

    public SettingFragment() {
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
        userViewModel =  new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        initData();
        data();
        setOnClick();
    }

    private void initView(View v) {
         tvInDate = v.findViewById(R.id.tvInDate);
         tv3Day = v.findViewById(R.id.tv3Day);
         tv7Day = v.findViewById(R.id.tv7Day);
         tvUsername = v.findViewById(R.id.tvUsername);
         tvSex = v.findViewById(R.id.tvSex);
         tvPhone = v.findViewById(R.id.tvPhone);
         tvEmail = v.findViewById(R.id.tvEmail);
         tvEdit = v.findViewById(R.id.tvEdit);
         btnLogout = v.findViewById(R.id.btnLogout);
    }

    private void initData() {
        tvUsername.setText("Username: " + GlobalInfor.username);
        if (GlobalInfor.sex == null ) {
            tvSex.setText("Giới tính: ");
        } else {
            tvSex.setText("Giới tính: " + GlobalInfor.sex);
        }
        tvPhone.setText(GlobalInfor.phone);
        tvEmail.setText(GlobalInfor.email);
    }

    private void setOnClick() {
        tvInDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeType(0);
            }
        });
        tv3Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeType(1);
            }
        });
        tv7Day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeType(2);
            }
        });
        tvEdit.setOnClickListener(view -> {
            UpdateInfoDialog updateDialog = new UpdateInfoDialog();
            updateDialog.show(getChildFragmentManager(), "update info dialog");
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = Constants.INTRO;
                String token = Constants.TOKEN;
                SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).storeBooleanInSharedPreferences(key, false);
                SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).storeUserToken(token, "");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().onBackPressed();
            }
        });
    }

    private void onChangeType(int typeRemind) {
        SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).storeIntInSharedPreferences(Constants.REMIND + GlobalInfor.username, typeRemind);
        if(typeRemind == 0) {
            tvInDate.setBackgroundResource(R.drawable.chart_bg_left_focus);
            tvInDate.setTextColor(this.getResources().getColor(R.color.WHITE_COLOR));
            tv3Day.setBackgroundResource(R.drawable.chart_bg_center_default);
            tv3Day.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
            tv7Day.setBackgroundResource(R.drawable.chart_bg_right_default);
            tv7Day.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
        } else if(typeRemind == 1) {
            tv3Day.setBackgroundResource(R.drawable.chart_bg_center_focus);
            tv3Day.setTextColor(this.getResources().getColor(R.color.WHITE_COLOR));
            tvInDate.setBackgroundResource(R.drawable.chart_bg_left_default);
            tvInDate.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
            tv7Day.setBackgroundResource(R.drawable.chart_bg_right_default);
            tv7Day.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
        } else {
            tvInDate.setBackgroundResource(R.drawable.chart_bg_left_default);
            tvInDate.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
            tv3Day.setBackgroundResource(R.drawable.chart_bg_center_default);
            tv3Day.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
            tv7Day.setBackgroundResource(R.drawable.chart_bg_right_focus);
            tv7Day.setTextColor(this.getResources().getColor(R.color.WHITE_COLOR));
        }
    }

    private void data() {
        int typeRemind = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getIntFromSharedPreferences(Constants.REMIND + GlobalInfor.username);
        if(typeRemind == 0) {
            tvInDate.setBackgroundResource(R.drawable.chart_bg_left_focus);
            tv3Day.setBackgroundResource(R.drawable.chart_bg_center_default);
            tv7Day.setBackgroundResource(R.drawable.chart_bg_right_default);
            tv7Day.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
        } else if(typeRemind == 1) {
            tv3Day.setBackgroundResource(R.drawable.chart_bg_center_focus);
            tv3Day.setTextColor(this.getResources().getColor(R.color.WHITE_COLOR));
            tvInDate.setBackgroundResource(R.drawable.chart_bg_left_default);
            tvInDate.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
            tv7Day.setBackgroundResource(R.drawable.chart_bg_right_default);
            tv7Day.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
        } else {
            tvInDate.setBackgroundResource(R.drawable.chart_bg_left_default);
            tvInDate.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
            tv3Day.setBackgroundResource(R.drawable.chart_bg_center_default);
            tv3Day.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
            tv7Day.setBackgroundResource(R.drawable.chart_bg_right_focus);
            tv7Day.setTextColor(this.getResources().getColor(R.color.WHITE_COLOR));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG", "resume");
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("TAG", "stop");
    }
}