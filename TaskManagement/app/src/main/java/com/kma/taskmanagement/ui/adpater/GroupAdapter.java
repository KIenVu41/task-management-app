package com.kma.taskmanagement.ui.adpater;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.data.model.Task;
import com.kma.taskmanagement.ui.main.fragments.CreateTaskBottomSheetFragment;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskBottomSheetFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends ListAdapter<Group, GroupAdapter.GroupHolder> {

    private Context context;

    public GroupAdapter(@NonNull DiffUtil.ItemCallback<Group> diffCallback, Context context) {
        super(diffCallback);
        this.context = context;
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder holder, int position) {
        Group group = getItem(position);
        holder.bind(group);
    }

   class GroupHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvGroupTitle)
        TextView tvTitle;
        @BindView(R.id.tvGroupMemberQuantity)
        TextView tvQuantity;
        @BindView(R.id.options)
        ImageView options;
        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Group group) {
            tvTitle.setText(group.getName());
            if(group.getMember() != null) {
                if(group.getMember().size() == 0 ){
                    tvQuantity.setText(context.getResources().getString(R.string.memberquantity) + 0);
                }else {
                    tvQuantity.setText(context.getResources().getString(R.string.memberquantity) + group.getMember().size());
                }
            }
            options.setOnClickListener(view -> showPopUpMenu(view, group));
        }

       public void showPopUpMenu(View view, Group group) {
           PopupMenu popupMenu = new PopupMenu(context, view);
           popupMenu.getMenuInflater().inflate(R.menu.group_menu, popupMenu.getMenu());
           popupMenu.setOnMenuItemClickListener(item -> {
               switch (item.getItemId()) {
                   case R.id.menuAssign:
                       GroupTaskBottomSheetFragment createTaskBottomSheetFragment = new GroupTaskBottomSheetFragment();
                       createTaskBottomSheetFragment.setAction(group);
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
    }
}
