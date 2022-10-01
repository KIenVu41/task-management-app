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
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.ALARM_SERVICE;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.model.Task;

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
    @BindView(R.id.taskEvent)
    EditText taskEvent;
    @BindView(R.id.addTask)
    Button addTask;
    int taskId;
    boolean isEdit;
    Task task;
    int mYear, mMonth, mDay;
    int mHour, mMinute;
    setRefreshListener setRefreshListener;
    AlarmManager alarmManager;
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    public static int count = 0;


    public void setTaskId(int taskId, boolean isEdit, setRefreshListener setRefreshListener) {
        this.taskId = taskId;
        this.isEdit = isEdit;
        this.setRefreshListener = setRefreshListener;
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
        addTask.setOnClickListener(view -> {
//            if(validateFields())
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

//    public boolean validateFields() {
//        if(addTaskTitle.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(activity, "Please enter a valid title", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else if(addTaskDescription.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(activity, "Please enter a valid description", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else if(taskDate.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(activity, "Please enter date", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else if(taskTime.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(activity, "Please enter time", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else if(taskEvent.getText().toString().equalsIgnoreCase("")) {
//            Toast.makeText(activity, "Please enter an event", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        else {
//            return true;
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void createTask() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createAnAlarm();
        }
//        class saveTaskInBackend extends AsyncTask<Void, Void, Void> {
//            @SuppressLint("WrongThread")
//            @Override
//            protected Void doInBackground(Void... voids) {
//                Task createTask = new Task();
//                createTask.setTaskTitle(addTaskTitle.getText().toString());
//                createTask.setTaskDescrption(addTaskDescription.getText().toString());
//                createTask.setDate(taskDate.getText().toString());
//                createTask.setLastAlarm(taskTime.getText().toString());
//                createTask.setEvent(taskEvent.getText().toString());
//
//
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    createAnAlarm();
//                }
//                setRefreshListener.refresh();
//                Toast.makeText(getActivity(), "Your event is been added", Toast.LENGTH_SHORT).show();
//                dismiss();
//
//            }
//        }
//        saveTaskInBackend st = new saveTaskInBackend();
//        st.execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createAnAlarm() {
        try {
            String[] items1 = "30-9-2022".toString().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = "00:15".toString().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];

//            Calendar cur_cal = new GregorianCalendar();
//            cur_cal.setTimeInMillis(System.currentTimeMillis());

            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour)); // hour
            cal.set(Calendar.MINUTE, Integer.parseInt(min)); // min
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, Integer.parseInt(dd)); //dd

            if(cal.getTimeInMillis() < System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent alarmIntent = new Intent(getActivity(), AlarmBroadcastReceiver.class);
            alarmIntent.putExtra("TITLE", addTaskTitle.getText().toString());
            alarmIntent.putExtra("DESC", addTaskDescription.getText().toString());
            alarmIntent.putExtra("DATE", taskDate.getText().toString());
            alarmIntent.putExtra("TIME", taskTime.getText().toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),count, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
                count ++;

//                PendingIntent intent = PendingIntent.getBroadcast(getActivity(), count, alarmIntent, 0);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
//                    } else {
//                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() - 600000, intent);
//                    }
//                }
//                count ++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setDataInUI() {
        addTaskTitle.setText(task.getTaskTitle());
        addTaskDescription.setText(task.getTaskDescrption());
        taskDate.setText(task.getDate());
        taskTime.setText(task.getLastAlarm());
        taskEvent.setText(task.getEvent());
    }

    public interface setRefreshListener {
        void refresh();
    }
}
