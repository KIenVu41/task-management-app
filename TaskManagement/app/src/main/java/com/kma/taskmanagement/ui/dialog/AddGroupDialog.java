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

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.User;
import com.kma.taskmanagement.data.remote.request.GroupRequest;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.UserRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.data.repository.impl.UserRepositoryImpl;
import com.kma.taskmanagement.ui.main.GroupViewModel;
import com.kma.taskmanagement.ui.main.GroupViewModelFactory;
import com.kma.taskmanagement.ui.user.UserViewModel;
import com.kma.taskmanagement.ui.user.UserViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

public class AddGroupDialog extends AppCompatDialogFragment {
    private EditText editTextGName, editTextGCode;
    private String token = "";
    private GroupViewModel groupViewModel;
    private GroupRepository groupRepository = new GroupRepositoryImpl();
    private ProgressDialog progressDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        groupViewModel.getResponse().observe(getActivity(), new Observer<String>() {
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

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;
        view = inflater.inflate(R.layout.layout_add_group_dialog, null);

        builder.setView(view)
                .setTitle("Thêm")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
                        String name = editTextGName.getText().toString();
                        String member = editTextGCode.getText().toString();

                        if(name.trim().length() == 0) {
                            Toast.makeText(getActivity(), "Chưa nhập tên nhóm", Toast.LENGTH_SHORT).show();
                            return;
                        } else if(member.trim().length() == 0) {
                            Toast.makeText(getActivity(), "Chưa nhập thành viên", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        GroupRequest groupRequest = new GroupRequest();
                        groupRequest.setName(name);
                        groupRequest.setMember_name(member);
                        groupViewModel.addGroup(token, groupRequest);
                    }
                });

        editTextGName = view.findViewById(R.id.edit_gname);
        editTextGCode = view.findViewById(R.id.edit_gcode);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(true);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        groupViewModel =  new ViewModelProvider(this, new GroupViewModelFactory(groupRepository)).get(GroupViewModel.class);

    }
}
