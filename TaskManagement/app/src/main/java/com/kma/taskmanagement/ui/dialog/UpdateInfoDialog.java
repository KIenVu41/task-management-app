package com.kma.taskmanagement.ui.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Token;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.user.UserViewModel;
import com.kma.taskmanagement.ui.user.UserViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.TextUtils;

public class UpdateInfoDialog extends AppCompatDialogFragment {
    private EditText editTextEmail, editTextPhone;
    private RadioGroup radioGroup;
    private RadioButton radioSexButton;
    private UserViewModel userViewModel;
    private UserRepository userRepository = new UserRepositoryImpl();
    private String token = "";
    private ProgressDialog progressDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;
        view = inflater.inflate(R.layout.layout_dialog, null);

        userViewModel.getProgress().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d("TAG", s);
            }
        });
        userViewModel.getProgress().observe(getActivity(), new Observer<String>() {
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
        userViewModel.getUpdateResult().observe(getActivity(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user != null) {
                    GlobalInfor.sex = user.getSex();
                    GlobalInfor.email = user.getEmail();
                    GlobalInfor.phone = user.getPhone();
                }
            }
        });
        builder.setView(view)
                .setTitle("Cập nhật")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phone = editTextPhone.getText().toString();
                        String email = editTextEmail.getText().toString();
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioSexButton = (RadioButton) view.findViewById(selectedId);
                        String sex = radioSexButton.getText().toString();

                        if(email.trim().length() == 0) {
                            Toast.makeText(getActivity(), "Chưa nhập email", Toast.LENGTH_SHORT).show();
                            return;
                        } else if(phone.trim().length() == 0) {
                            Toast.makeText(getActivity(), "Chưa nhập phone", Toast.LENGTH_SHORT).show();
                            return;
                        } else if(!TextUtils.isValidEmail(email)) {
                            Toast.makeText(getActivity(), "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        } else if(!phone.startsWith("0")) {
                            Toast.makeText(getActivity(), "Số điện thoại không hợp lệ", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);

                        userViewModel.update(token, GlobalInfor.id, new User(null, email, "kocopass", phone, sex, "", null));
                    }
                });

        editTextPhone = view.findViewById(R.id.edit_phone);
        editTextEmail = view.findViewById(R.id.edit_email);
        radioGroup = view.findViewById(R.id.radioGroup);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userViewModel =  new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);

    }
}