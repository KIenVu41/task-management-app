package com.kma.taskmanagement.ui.main.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
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

public class CreateTaskBottomSheetFragment extends BottomSheetDialogFragment {
    Unbinder unbinder;
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

    public void setTaskAction(boolean isEdit, long cateId) {
        this.isEdit = isEdit;
        this.cateId = cateId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_create_task, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        setupSpinner();
        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);
        addTask.setOnClickListener(view -> {
            if(validateFields())
                createTask();
        });
//        if (isEdit) {
//            showTaskFromId();
//        }

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

        ArrayAdapter adapterStatus = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.taskstatusarr));
        adapterStatus.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        if(!isEdit) {
            dropdownStatus.setEnabled(false);
            dropdownStatus.setClickable(false);
        } else {
            dropdownStatus.setEnabled(true);
            dropdownStatus.setClickable(true); }
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
       String prio = dropdownPrio.getSelectedItem().toString().toLowerCase();
       String status = dropdownStatus.getSelectedItem().toString().toLowerCase();

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

       taskViewModel.addTask(Constants.BEARER + token, new Task("", cateId, "", desc, endDateFormat, title, GlobalInfor.username, prio,  startDateFormat, status, null));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                taskViewModel.getAllTasks(Constants.BEARER + token);
            }
        },500);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createAnAlarm(title, desc, endDate, endTime);
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

    public interface setRefreshListener {
        void refresh();
    }
}
