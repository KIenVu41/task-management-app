package com.kma.taskmanagement.ui.main.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.ALARM_SERVICE;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.local.DatabaseHelper;
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
import com.kma.taskmanagement.utils.Utils;

public class CreateTaskBottomSheetFragment extends BottomSheetDialogFragment {
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
    @BindView(R.id.addTask)
    Button addTask;
    boolean isEdit;
    Task task;
    int mYear, mMonth, mDay;
    int mHour, mMinute;
    long cateId, taskId;
    AlarmManager alarmManager;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    TaskViewModel taskViewModel;
    private String token = "";
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    public static int count = 0;
    private DatabaseHelper db;

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

    public void setTaskAction(boolean isEdit, long cateId, Task task) {
        this.isEdit = isEdit;
        this.cateId = cateId;
        this.task = task;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_create_task, null);
        unbinder = ButterKnife.bind(this, contentView);
        if(isEdit) {
            dialogTitle.setText(getResources().getString(R.string.updatetask));
            addTask.setText(getResources().getString(R.string.edit));
            addTaskTitle.setText(task.getName());
            addTaskDescription.setText(task.getDescription());
        }
        dialog.setContentView(contentView);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
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
        db = new DatabaseHelper(requireActivity());
    }

    private void saveTaskToLocalStorage(Task task, int status) {
        db.addTask(task, status);
    }


    public void setupSpinner() {
        ArrayAdapter adapterPrio = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.taskprioarr));
        adapterPrio.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        dropdownPrio.setAdapter(adapterPrio);

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createTask() {
       String title = addTaskTitle.getText().toString();
       String desc = addTaskDescription.getText().toString();
       String endDate = taskDate.getText().toString();
       String endTime = taskTime.getText().toString();
       String prio = dropdownPrio.getSelectedItem().toString();
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

        if(Utils.isNetworkConnected(requireActivity())) {
            if(isEdit) {
                cancleAlarm();
                taskViewModel.update(Constants.BEARER + token, task.getId(), new Task("", cateId, "", desc, endDateFormat,null,  title, GlobalInfor.username, prio,  startDateFormat, status, null));
            } else {
                taskViewModel.addTask(Constants.BEARER + token, new Task("", cateId, "", desc, endDateFormat, null,  title, GlobalInfor.username, prio,  startDateFormat, status, null));
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    taskViewModel.getTasksByCategory(Constants.BEARER + token, cateId);
                }
            },500);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                createAnAlarm(title, desc, endDate, endTime);
            }
        } else {
            saveTaskToLocalStorage(new Task("", cateId, "", desc, endDateFormat, null,  title, GlobalInfor.username, prio,  startDateFormat, status, null), 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createAnAlarm(String title, String desc, String date, String time) {
        try {
            int typeRemind = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getIntFromSharedPreferences(Constants.REMIND + GlobalInfor.username);
            String dateProcess = DateUtils.findDateByType(typeRemind);
            String[] items1 = dateProcess.split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = time.split(":");
            String hour = itemTime[0];
            String min = itemTime[1];


            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour)); // hour
            cal.set(Calendar.MINUTE, Integer.parseInt(min)); // min
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, Integer.parseInt(dd)); //dd
            cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            cal.set(Calendar.YEAR, Integer.parseInt(year));

            if(cal.getTimeInMillis() < System.currentTimeMillis()) {
                return;
            }
            Intent alarmIntent = new Intent(getActivity(), AlarmBroadcastReceiver.class);
            alarmIntent.setAction(title);
            alarmIntent.putExtra("TITLE", title);
            alarmIntent.putExtra("DESC", desc);
            alarmIntent.putExtra("DATE", date);
            alarmIntent.putExtra("TIME", time);
            alarmIntent.putExtra("TYPE", typeRemind);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
                count ++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancleAlarm() {
        Intent intent  = new Intent(getActivity(), AlarmBroadcastReceiver.class);
        intent.setAction(task.getName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        if(pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    public interface setRefreshListener {
        void refresh();
    }
}
