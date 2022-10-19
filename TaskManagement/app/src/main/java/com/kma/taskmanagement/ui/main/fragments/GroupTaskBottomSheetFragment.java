package com.kma.taskmanagement.ui.main.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GroupTaskBottomSheetFragment extends BottomSheetDialogFragment {
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
    @BindView(R.id.spinnerStatus)
    CustomSpinner dropdownStatus;
    @BindView(R.id.spinnerAssign)
    CustomSpinner dropdownAssign;
    @BindView(R.id.addTask)
    Button addTask;
    boolean isEdit;
    Task task;
    int mYear, mMonth, mDay;
    int mHour, mMinute;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    TaskViewModel taskViewModel;
    private Group group = new Group();
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

    public void setAction(Group group) {
        this.group = group;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_create_group_task, null);
        unbinder = ButterKnife.bind(this, contentView);

        dialog.setContentView(contentView);
        setupSpinner();

        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);

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

        String[] arrayName = new String[group.getMember().size()];
        for(int i = 0; i < group.getMember().size(); i++) arrayName[i] = group.getMember().get(i).getUsername();
        ArrayAdapter adapterAssign = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, arrayName);
        adapterAssign.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        dropdownAssign.setAdapter(adapterAssign);

        ArrayAdapter adapterStatus = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.taskstatusarr));
        adapterStatus.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        if(!isEdit) {
            dropdownStatus.setEnabled(false);
            dropdownStatus.setClickable(false);
        } else {
            dropdownStatus.setEnabled(true);
            dropdownStatus.setClickable(true);
            if(task.getPriority().equals("low")) {
                dropdownPrio.setSelection(0);
            } else if(task.getPriority().equals("medium")) {
                dropdownPrio.setSelection(1);
            } else if(task.getPriority().equals("high")) {
                dropdownPrio.setSelection(2);
            }

            if(task.getStatus().equals("todo")) {
                dropdownPrio.setSelection(0);
            } else if(task.getPriority().equals("doing")) {
                dropdownPrio.setSelection(1);
            } else if(task.getPriority().equals("completed")) {
                dropdownPrio.setSelection(2);
            }

        }
        dropdownStatus.setAdapter(adapterStatus);
    }

    public boolean validateFields() {
        if(addTaskTitle.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireActivity(), "Please enter a valid title", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(addTaskDescription.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireActivity(), "Please enter a valid description", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(taskDate.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireActivity(), "Please enter date", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(taskTime.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(requireActivity(), "Please enter time", Toast.LENGTH_SHORT).show();
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
        String status = dropdownStatus.getSelectedItem().toString();
        String performer = dropdownAssign.getSelectedItem().toString();
        //yyyy-MM-dd-HH-mm-ss.zzz
        String[] items1 = endDate.split("-");
        String ddEnd = items1[0];
        String monthEnd = items1[1];
        String yearEnd = items1[2];

        String[] itemTime1 = endTime.split(":");
        String hourEnd = itemTime1[0];
        String minEnd = itemTime1[1];

//       String startDate = DateUtils.getLocalDate();
//       String[] items2 = startDate.split(" ");
//       String dateStart = items2[0];
//       String timeStart = items2[1];

        //String startDateFormat = dateStart + "-" + timeStart.replace(":", "-");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String startDateFormat = now.format(format).replace(" ", "T") + "Z";
        String endDateFormat =  DateUtils.convert(ddEnd, monthEnd, yearEnd) + "T" + DateUtils.convertTime(hourEnd, minEnd) + ":00.000Z";

        if(isEdit) {
            taskViewModel.update(Constants.BEARER + token, task.getId(), new Task("", 1, "", desc, endDateFormat, group.getId(), title, GlobalInfor.username, prio,  startDateFormat, status, null));
        } else {
            taskViewModel.addTask(Constants.BEARER + token, new Task(GlobalInfor.username, 1, "", desc, endDateFormat, group.getId(), title, performer, prio,  startDateFormat, status, null));
        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                taskViewModel.getAllTasks(Constants.BEARER + token);
//            }
//        },500);
    }
}