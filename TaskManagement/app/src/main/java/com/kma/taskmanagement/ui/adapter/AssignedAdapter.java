package com.kma.taskmanagement.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskBottomSheetFragment;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private RequestQueue requestQueue;

    public AssignedAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
       // requestQueue = Volley.newRequestQueue(context);
       // FirebaseMessaging.getInstance().subscribeToTopic("news");
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
        holder.tvGroup.setText("Nhóm: " + task.getGroup_output_dto().getId());
        holder.tvPerfomer.setText("Người thực hiện: " + task.getPerformer_name());
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
                case R.id.menuNotice:
                    final EditText edittext = new EditText(context);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setTitle(R.string.notice_confirmation).setView(edittext).
                            setPositiveButton(R.string.send, (dialog, which) -> {

                            })
                            .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel()).show();
                    break;
                case R.id.menuUpdate:
                    GroupTaskBottomSheetFragment createTaskBottomSheetFragment = new GroupTaskBottomSheetFragment();
                    for(Group group: GlobalInfor.groups) {
                        if(group.getId() == task.getGroup_output_dto().getId()) {
                            createTaskBottomSheetFragment.setAction(group);
                            break;
                        }
                    }
                    //createTaskBottomSheetFragment.setAction(task.getGroup_output_dto());
                    createTaskBottomSheetFragment.setEdit(true, true, task);
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

    private void sendNotification(String message) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("to", "/topics/" + "news");
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "Notice");
            notificationObj.put("body", message);
            jsonObject.put("notification", notificationObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, Constants.FIREBASE_URL, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAAQLKl4r0:APA91bFQAcV8TDKNazoleMVtqd2e_K1jYQnTanzjO_6TuK4DMTntS2LmTmwXuS1yedmvDVUEetGha6bcQRNZVhH3q9G7pvxmBqDl2CXM5_IbDTMjFhY4DFFer2WMLFO05SSTQjsvtomE");

                    return header;
                }
            };
            requestQueue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        @BindView(R.id.assignGroup)
        TextView tvGroup;
        @BindView(R.id.assignPerfomer)
        TextView tvPerfomer;
//        @BindView(R.id.spinnerStatus)
//        Spinner spinnerStatus;
        @BindView(R.id.assignoptions)
        ImageView options;
        @BindView(R.id.assigntime)
        TextView time;

        AssignViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
