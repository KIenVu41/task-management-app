package com.kma.taskmanagement.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.listener.HandleClickListener;
import com.kma.taskmanagement.ui.main.fragments.CreateTaskBottomSheetFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private long cateId;
    public List<Task> taskList;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private HandleClickListener handleClickListener;
    Date date = null;
    String outputDateString = null;

    public TaskAdapter(Context context, List<Task> taskList, HandleClickListener handleClickListener) {
        this.context = context;
        this.taskList = taskList;
        this.handleClickListener = handleClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_task, viewGroup, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getName());
        holder.description.setText(task.getDescription());
        //holder.status.setText(task.getStatus());
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));
        if(task.getStatus().equals("TODO")) {
            holder.spinnerStatus.setSelection(0, true);
        } else if(task.getStatus().equals("DOING")) {
            holder.spinnerStatus.setSelection(1, true);
        } else if(task.getStatus().equals("COMPLETED")) {
            holder.spinnerStatus.setSelection(2, true);
        }
        holder.spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                handleClickListener.onTaskClick(task, adapterView.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        try {
            date = inputDateFormat.parse(task.getEnd_date());
            outputDateString = dateFormat.format(date);
            String[] items1 = outputDateString.split(" ");
            String day = items1[0];
            String dd = items1[1];
            String month = items1[2];

            holder.day.setText(day);
            holder.date.setText(dd);
            holder.month.setText(month);
            holder.time.setText(task.getEnd_date());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showPopUpMenu(View view, int position) {
        final Task task = taskList.get(position);
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
//                case R.id.menuDelete:
//                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//                    alertDialogBuilder.setTitle(R.string.delete_confirmation).setMessage(R.string.sureToDelete).
//                            setPositiveButton(R.string.yes, (dialog, which) -> {
//                            })
//                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
//                    break;
                case R.id.menuUpdate:
                    CreateTaskBottomSheetFragment createTaskBottomSheetFragment = new CreateTaskBottomSheetFragment();
                    createTaskBottomSheetFragment.setTaskAction(true, cateId, task);
                    createTaskBottomSheetFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
                    break;
                case R.id.menuComplete:
                    AlertDialog.Builder completeAlertDialog = new AlertDialog.Builder(context);
                    completeAlertDialog.setTitle(R.string.confirmation).setMessage(R.string.sureToMarkAsComplete).
                            setPositiveButton(R.string.yes, (dialog, which) -> {})
                            .setNegativeButton(R.string.no, (dialog, which) -> dialog.cancel()).show();
                    break;
            }
            return false;
        });
        popupMenu.show();
    }

    public void setList(List<Task> taskList) {
        this.taskList.clear();
        this.taskList = taskList;
        notifyDataSetChanged();
    }
//
//    public void showCompleteDialog(int taskId, int position) {
//        Dialog dialog = new Dialog(context, R.style.AppTheme);
//        dialog.setContentView(R.layout.dialog_completed_theme);
//        Button close = dialog.findViewById(R.id.closeButton);
//        close.setOnClickListener(view -> {
//            deleteTaskFromId(taskId, position);
//            dialog.dismiss();
//        });
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.show();
//    }

    private void removeAtPosition(int position) {
        taskList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, taskList.size());
    }

    @Override
    public int getItemCount() {
        if (taskList != null) {
            return taskList.size();
        }
        return 0;
    }

    public void setCateId(long cateId) {
        this.cateId = cateId;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.day)
        TextView day;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.month)
        TextView month;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.description)
        TextView description;
//        @BindView(R.id.status)
//        TextView status;
        @BindView(R.id.spinnerStatus)
        Spinner spinnerStatus;
        @BindView(R.id.options)
        ImageView options;
        @BindView(R.id.time)
        TextView time;

        TaskViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);

            ArrayAdapter adapterStatus = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.taskstatusarr));
            spinnerStatus.setAdapter(adapterStatus);
        }
    }

}