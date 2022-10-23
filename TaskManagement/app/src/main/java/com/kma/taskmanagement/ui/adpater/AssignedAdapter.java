package com.kma.taskmanagement.ui.adpater;

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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.listener.HandleClickListener;
import com.kma.taskmanagement.ui.main.fragments.CreateTaskBottomSheetFragment;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskBottomSheetFragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AssignedAdapter extends RecyclerView.Adapter<AssignedAdapter.AssignViewHolder> {

    private Context context;
    private long cateId;
    public List<Task> taskList;
    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
    public SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Date date = null;
    String outputDateString = null;

    public AssignedAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public AssignViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_assign, viewGroup, false);
        return new AssignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getName());
        holder.description.setText(task.getDescription());
        holder.status.setText(task.getStatus());
        holder.options.setOnClickListener(view -> showPopUpMenu(view, position));
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

    public class AssignViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.assignday)
        TextView day;
        @BindView(R.id.assigndate)
        TextView date;
        @BindView(R.id.assignmonth)
        TextView month;
        @BindView(R.id.assigntitle)
        TextView title;
        @BindView(R.id.assigndescription)
        TextView description;
        @BindView(R.id.assignstatus)
        TextView status;
//        @BindView(R.id.spinnerStatus)
//        Spinner spinnerStatus;
        @BindView(R.id.options)
        ImageView options;
        @BindView(R.id.assigntime)
        TextView time;

        AssignViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
