package com.kma.taskmanagement.ui.main.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.ui.common.CustomSpinner;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.kma.taskmanagement.ui.main.TaskViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.DateUtils;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AssignTaskBottomSheetFragment extends BottomSheetDialogFragment {
    Unbinder unbinder;
    @BindView(R.id.dialogTitle)
    TextView dialogTitle;
    @BindView(R.id.addTaskTitle)
    EditText addTaskTitle;
    @BindView(R.id.addTaskDescription)
    EditText addTaskDescription;
    @BindView(R.id.taskDate)
    EditText taskDate;
    @BindView(R.id.taskTime)
    EditText taskTime;
    @BindView(R.id.spinnerPrio)
    CustomSpinner dropdownPrio;
    @BindView(R.id.spinnerAssign)
    CustomSpinner dropdownAssign;
    @BindView(R.id.spinnerStatus)
    CustomSpinner dropdownStatus;
    @BindView(R.id.addTask)
    Button addTask;
    Task task;
    int mYear, mMonth, mDay;
    int mHour, mMinute;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    TaskViewModel taskViewModel;
    private List<String> assinger;
    private int groupId = 0;
    private String token = "";
    private TaskRepository taskRepository = new TaskRepositoryImpl();

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    public void setAssigner(List<String> assigner) {
        this.assinger = assigner;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_assign_task_bottom_sheet, null);
        unbinder = ButterKnife.bind(this, contentView);

        dialog.setContentView(contentView);

        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);

        addTaskTitle.setText(task.getName());
        addTaskDescription.setText(task.getDescription());
        setupSpinner();

        addTask.setOnClickListener(view -> {
            if(validateFields())
                createTask();
        });
        taskDate.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getActivity(),
                        (view1, year, monthOfYear, dayOfMonth) -> {
                            taskDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            datePickerDialog.dismiss();
                        }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
            return true;
        });

        taskTime.setOnTouchListener((view, motionEvent) -> {
            if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(getActivity(),
                        (view12, hourOfDay, minute) -> {
                            taskTime.setText(hourOfDay + ":" + minute);
                            timePickerDialog.dismiss();
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
            return true;
        });
    }

    public void setupSpinner() {
        ArrayAdapter adapterPrio = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.taskprioarr));
        adapterPrio.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        dropdownPrio.setAdapter(adapterPrio);

        String[] arrayName = new String[assinger.size()];
        for(int i = 0; i < assinger.size(); i++) arrayName[i] = assinger.get(i);
        ArrayAdapter adapterAssign = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayName);
        adapterAssign.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        dropdownAssign.setAdapter(adapterAssign);

        ArrayAdapter adapterStatus = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.taskstatusarr));
        adapterStatus.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        if(task.getPriority().equals("low")) {
            dropdownPrio.setSelection(0);
        } else if(task.getPriority().equals("medium")) {
            dropdownPrio.setSelection(1);
        } else if(task.getPriority().equals("high")) {
            dropdownPrio.setSelection(2);
        }
        dropdownStatus.setAdapter(adapterStatus);
        dropdownStatus.setEnabled(false);
    }

    public boolean validateFields() {
        if(addTaskTitle.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireActivity(), "Hãy nhập tiêu đề", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(addTaskDescription.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireActivity(), "Hãy nhập mô tả", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(taskDate.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireActivity(), "Hãy nhập ngày", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(taskTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireActivity(), "Hãy nhập thời gian", Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTask() {
        String title = addTaskTitle.getText().toString();
        String desc = addTaskDescription.getText().toString();
        String endDate = taskDate.getText().toString();
        String endTime = taskTime.getText().toString();
        String prio = dropdownPrio.getSelectedItem().toString();
//        String status = "TODO";
        String performer = dropdownAssign.getSelectedItem().toString();
        String status = dropdownStatus.getSelectedItem().toString();
        //yyyy-MM-dd-HH-mm-ss.zzz
        String[] items1 = endDate.split("-");
        String ddEnd = items1[0];
        String monthEnd = items1[1];
        String yearEnd = items1[2];

        String[] itemTime1 = endTime.split(":");
        String hourEnd = itemTime1[0];
        String minEnd = itemTime1[1];

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String startDateFormat = now.format(format).replace(" ", "T") + "Z";
        String endDateFormat =  DateUtils.convert(ddEnd, monthEnd, yearEnd) + "T" + DateUtils.convertTime(hourEnd, minEnd) + ":00.000Z";

        taskViewModel.update(Constants.BEARER + token, task.getId(), new Task(GlobalInfor.username, null, "", desc, endDateFormat, (long) groupId, title, performer, prio,  startDateFormat, status, null));
    }
}