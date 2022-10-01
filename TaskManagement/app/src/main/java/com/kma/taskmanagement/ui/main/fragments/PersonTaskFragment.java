package com.kma.taskmanagement.ui.main.fragments;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.broadcastReceiver.AlarmBroadcastReceiver;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.ui.adpater.TaskAdapter;
import com.kma.taskmanagement.utils.SwipeToDeleteCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.internal.concurrent.TaskLoggerKt;

public class PersonTaskFragment extends Fragment {

    RecyclerView taskRecycler;
    TextView addTask;
    ImageView noDataImage;
    ImageView calendar;
    LinearLayout linearLayout;
    TaskAdapter taskAdapter;
    List<Task> tasks = new ArrayList<>();
    public static int count = 0;
    AlarmManager alarmManager;

    public PersonTaskFragment() {
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
        return inflater.inflate(R.layout.fragment_person_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        // initData();
        setUpAdapter();
        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ComponentName receiver = new ComponentName(getActivity(), AlarmBroadcastReceiver.class);
        PackageManager pm = getActivity().getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        addTask.setOnClickListener(view1 -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                createAnAlarm();
//            }
            CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
            //createTaskBottomSheetFragment.setTaskId(0, false, getActivity(), getActivity());
            createTaskBottomSheetFragment.show(getActivity().getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
        });

        enableSwipeToDeleteAndUndo();
    }

    public void initView(View view) {
        taskRecycler = view.findViewById(R.id.taskRecycler);
        addTask = view.findViewById(R.id.addTask);
        noDataImage = view.findViewById(R.id.noDataImage);
        calendar = view.findViewById(R.id.calendar);
        linearLayout = view.findViewById(R.id.linearLayout);
    }

    public void initData() {
        tasks.add(new Task(1, "xxx", "28-09-2022", "xxx", false, "22:00", "22:00", "22:00", "ss"));
        tasks.add(new Task(1, "xxx", "28-09-2022", "xxx", false, "22:00", "22:00", "22:00", "ss"));
        tasks.add(new Task(1, "xxx", "28-09-2022", "xxx", false, "22:00", "22:00", "22:00", "ss"));
        tasks.add(new Task(1, "xxx", "28-09-2022", "xxx", false, "22:00", "22:00", "22:00", "ss"));
        tasks.add(new Task(1, "xxx", "28-09-2022", "xxx", false, "22:00", "22:00", "22:00", "ss"));
    }

    public void setUpAdapter() {
        taskAdapter = new TaskAdapter(tasks);
        taskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        taskRecycler.setAdapter(taskAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createAnAlarm() {
        try {
            String[] items1 = "29-9-2022".toString().split("-");
            String dd = items1[0];
            String month = items1[1];
            String year = items1[2];

            String[] itemTime = "00:07".toString().split(":");
            String hour = itemTime[0];
            String min = itemTime[1];
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour)); // hour
            cal.set(Calendar.MINUTE, Integer.parseInt(min)); // min
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.DATE, Integer.parseInt(dd)); //dd

            if (cal.getTimeInMillis() < System.currentTimeMillis()) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
            }

            Intent alarmIntent = new Intent(getActivity(), AlarmBroadcastReceiver.class);
            alarmIntent.putExtra("TITLE", "xxx");
            alarmIntent.putExtra("DESC", "ss");
            alarmIntent.putExtra("DATE", "zzz");
            alarmIntent.putExtra("TIME", "zzz");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), count, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                }
                count++;

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

        private void enableSwipeToDeleteAndUndo() {
            SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity().getApplicationContext()) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                    final int position = viewHolder.getAdapterPosition();
                    final Task task = taskAdapter.taskList.get(position);

                    taskAdapter.taskList.remove(position);
                    taskAdapter.notifyItemRemoved(position);


                    Snackbar snackbar = Snackbar
                            .make(linearLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            taskAdapter.taskList.add(task);
                            taskAdapter.notifyItemInserted(position);
                            taskRecycler.scrollToPosition(position);
                        }
                    });

                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();

                }
            };

            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
            itemTouchhelper.attachToRecyclerView(taskRecycler);
        }


    }
