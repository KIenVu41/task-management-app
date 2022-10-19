package com.kma.taskmanagement.ui.main.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ShowCalendarViewBottomSheet extends BottomSheetDialogFragment {

    Unbinder unbinder;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    List<Task> tasks = new ArrayList<>();

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"RestrictedApi", "ClickableViewAccessibility"})
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_calendar_view, null);
        unbinder = ButterKnife.bind(this, contentView);
        dialog.setContentView(contentView);
        calendarView.setHeaderColor(R.color.colorAccent);
        calendarView.setEvents(getHighlitedDays());
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                String note = "";
                for(Task task: tasks) {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date endDate = sdf.parse(task.getEnd_date());
                        if(eventDay.getCalendar().getTime().equals(endDate)) {
                            note += task.getName() + "\n";
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getActivity(), "Bạn có công việc \n" + note, Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(view -> dialog.dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setList(List<Task> taskList) {
        this.tasks = taskList;
    }

    public List<EventDay> getHighlitedDays() {
        List<EventDay> events = new ArrayList<>();

        for(int i = 0; i < tasks.size(); i++) {
            Calendar calendar = Calendar.getInstance();
            String[] item1 = tasks.get(i).getEnd_date().split("T");
            String[] item2 = item1[0].split("-");
            String dd = item2[2];
            String month = item2[1];
            String year = item2[0];

            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dd));
            calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(year));
            events.add(new EventDay(calendar, R.drawable.dot));
        }
        return events;
    }

}
