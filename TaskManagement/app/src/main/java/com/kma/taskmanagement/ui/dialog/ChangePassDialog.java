package com.kma.taskmanagement.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.request.ChangePassRequest;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.user.UserViewModel;
import com.kma.taskmanagement.ui.user.UserViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

public class ChangePassDialog extends AppCompatDialogFragment {
    private EditText editNew, editOld, editRe;
    private UserViewModel userViewModel;
    private UserRepository userRepository = new UserRepositoryImpl();
    private String token = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;
        view = inflater.inflate(R.layout.layout_change_pass, null);

        userViewModel.getChangePass().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                switch (integer) {
                    case 1:
                        Toast.makeText(TaskApplication.getAppContext(), "Thành công", Toast.LENGTH_SHORT).show();
                        break;
                    case  -1:
                        Toast.makeText(TaskApplication.getAppContext(), "Lỗi xảy ra!", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        builder.setView(view)
                .setTitle("Đổi mật khẩu")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newpass = editNew.getText().toString();
                        String oldpass = editOld.getText().toString();
                        String repass = editRe.getText().toString();
                        if(!newpass.equals(repass)) {
                            Toast.makeText(getActivity(), "Mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);

                        userViewModel.changePass(Constants.BEARER + token, new ChangePassRequest(oldpass, newpass));
                    }
                });

        editNew = view.findViewById(R.id.edtNew);
        editOld = view.findViewById(R.id.edtOld);
        editRe = view.findViewById(R.id.edtRe);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        userViewModel =  new ViewModelProvider(this, new UserViewModelFactory(userRepository)).get(UserViewModel.class);

    }
}
