package com.kma.taskmanagement.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.remote.request.GroupRequest;
import com.kma.taskmanagement.data.repository.GroupRepository;
import com.kma.taskmanagement.data.repository.impl.GroupRepositoryImpl;
import com.kma.taskmanagement.ui.main.GroupViewModel;
import com.kma.taskmanagement.ui.main.GroupViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

public class UpdateGroupDialog extends AppCompatDialogFragment {
    private EditText editTextGName, editTextGCode;
    private String token = "";
    private GroupViewModel groupViewModel;
    private GroupRepository groupRepository = new GroupRepositoryImpl();
    private long id;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        groupViewModel.getResponse().observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s != null) {
                    Log.d("TAG", s);
                }
            }
        });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view;
        view = inflater.inflate(R.layout.layout_invite, null);

        builder.setView(view)
                .setTitle("Mời")
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
                        groupViewModel.updateGroup(Constants.BEARER + token, id,  groupRequest);
                    }
                });

        editTextGName = view.findViewById(R.id.edit_ugname);
        editTextGCode = view.findViewById(R.id.edit_invite);


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        groupViewModel =  new ViewModelProvider(this, new GroupViewModelFactory(groupRepository)).get(GroupViewModel.class);
    }

    public void setId(long id) {
        this.id = id;
    }
}
