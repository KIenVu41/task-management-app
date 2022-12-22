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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.biometric.BiometricCallback;
import com.kma.taskmanagement.biometric.BiometricManager;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.request.ChangePassRequest;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.dialog.ChangePassDialog;
import com.kma.taskmanagement.ui.dialog.UpdateInfoDialog;
import com.kma.taskmanagement.ui.user.LoginActivity;
import com.kma.taskmanagement.ui.user.UserViewModel;
import com.kma.taskmanagement.ui.user.UserViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.DateUtils;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

public class SettingFragment extends Fragment implements BiometricCallback {

    private TextView tvInDate, tv3Day, tv7Day, tvOn, tvOff, tvUsername, tvSex, tvPhone, tvEmail, tvEdit;
    private Button btnLogout, btnChangepass;
    private UserViewModel userViewModel;
    private UserRepository userRepository = new UserRepositoryImpl();
    private BiometricManager mBiometricManager;

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
        secureData();
        setOnClick();
    }

    private void initView(View v) {
         tvInDate = v.findViewById(R.id.tvInDate);
         tv3Day = v.findViewById(R.id.tv3Day);
         tv7Day = v.findViewById(R.id.tv7Day);
         tvOn = v.findViewById(R.id.tvSecureOn);
         tvOff = v.findViewById(R.id.tvSecureOff);
         tvUsername = v.findViewById(R.id.tvUsername);
         tvSex = v.findViewById(R.id.tvSex);
         tvPhone = v.findViewById(R.id.tvPhone);
         tvEmail = v.findViewById(R.id.tvEmail);
         tvEdit = v.findViewById(R.id.tvEdit);
         btnChangepass = v.findViewById(R.id.changepass);
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
        tvOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeSecureType(1);
            }
        });
        tvOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onChangeSecureType(0);
            }
        });
        tvEdit.setOnClickListener(view -> {
            UpdateInfoDialog updateDialog = new UpdateInfoDialog();
            updateDialog.show(getChildFragmentManager(), "update info dialog");
        });
        btnChangepass.setOnClickListener(view -> {
                    ChangePassDialog changePassDialog = new ChangePassDialog();
                    changePassDialog.show(getChildFragmentManager(), "change pass dialog");
        }
        );
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String key = Constants.INTRO;
                String token = Constants.TOKEN;
                SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).storeBooleanInSharedPreferences(key, false);
                SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).storeUserToken(token + GlobalInfor.username, "");
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
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

    private void secureData() {
        int typeSecure = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getIntFromSharedPreferences(Constants.SECURE + GlobalInfor.username);
        if(typeSecure == 0) {
            tvOff.setBackgroundResource(R.drawable.chart_bg_left_focus);
            tvOn.setBackgroundResource(R.drawable.chart_bg_right_default);
            tvOn.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
        } else if(typeSecure == 1) {
            tvOn.setBackgroundResource(R.drawable.chart_bg_right_focus);
            tvOn.setTextColor(this.getResources().getColor(R.color.WHITE_COLOR));
            tvOff.setBackgroundResource(R.drawable.chart_bg_left_default);
            tvOff.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
        }
    }

    private void onChangeSecureType(int typeSecure) {
        if(typeSecure == 0) {
            mBiometricManager = new BiometricManager.BiometricBuilder(requireActivity())
                    .setTitle(getString(R.string.biometric_title))
                    .setSubtitle(getString(R.string.biometric_subtitle))
                    .setDescription(getString(R.string.biometric_description))
                    .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                    .build();
            mBiometricManager.authenticate(SettingFragment.this);
        } else if(typeSecure == 1) {
            tvOn.setBackgroundResource(R.drawable.chart_bg_right_focus);
            tvOn.setTextColor(this.getResources().getColor(R.color.WHITE_COLOR));
            tvOff.setBackgroundResource(R.drawable.chart_bg_left_default);
            tvOff.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
            SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).storeIntInSharedPreferences(Constants.SECURE + GlobalInfor.username, typeSecure);
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
        tvOff.setBackgroundResource(R.drawable.chart_bg_left_focus);
        tvOff.setTextColor(this.getResources().getColor(R.color.WHITE_COLOR));
        tvOn.setBackgroundResource(R.drawable.chart_bg_right_default);
        tvOn.setTextColor(this.getResources().getColor(R.color.COLOR_BUTTON));
        SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).storeIntInSharedPreferences(Constants.SECURE + GlobalInfor.username, 0);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(requireActivity(), helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Toast.makeText(requireActivity(), errString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}