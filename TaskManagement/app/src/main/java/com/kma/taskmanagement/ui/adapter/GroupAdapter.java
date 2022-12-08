package com.kma.taskmanagement.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Group;
import com.kma.taskmanagement.listener.HandleClickListener;
import com.kma.taskmanagement.ui.dialog.UpdateGroupDialog;
import com.kma.taskmanagement.ui.main.fragments.GroupChatFragment;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskBottomSheetFragment;
import com.kma.taskmanagement.utils.GlobalInfor;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupAdapter extends ListAdapter<Group, GroupAdapter.GroupHolder> {

    private Context context;
    private HandleClickListener handleClickListener;

    public GroupAdapter(@NonNull DiffUtil.ItemCallback<Group> diffCallback, Context context, HandleClickListener handleClickListener) {
        super(diffCallback);
        this.context = context;
        this.handleClickListener = handleClickListener;
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

            itemView.setOnClickListener(view -> {
                handleClickListener.onGroupClick(getItem(getAbsoluteAdapterPosition()));
            });
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
            if(!group.getLeader_name().equals(GlobalInfor.username)) {

                Toast.makeText(context, "Bạn không phải leader", Toast.LENGTH_SHORT).show();
                return;
            }
           PopupMenu popupMenu = new PopupMenu(context, view);
           popupMenu.getMenuInflater().inflate(R.menu.group_menu, popupMenu.getMenu());
           popupMenu.setOnMenuItemClickListener(item -> {
               switch (item.getItemId()) {
                   case R.id.menuAssign:
                       GroupTaskBottomSheetFragment createTaskBottomSheetFragment = new GroupTaskBottomSheetFragment();
                       createTaskBottomSheetFragment.setEdit(false, true, null);
                       createTaskBottomSheetFragment.setAction(group);
                       createTaskBottomSheetFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), createTaskBottomSheetFragment.getTag());
                       break;
                   case R.id.menuInvite:
                       UpdateGroupDialog updateGroupDialog = new UpdateGroupDialog();
                       updateGroupDialog.setId(group.getId());
                       updateGroupDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "update group dialog");
                   case R.id.menuChat:
                       GroupChatFragment groupChatFragment = new GroupChatFragment();
                       groupChatFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), groupChatFragment.getTag());
               }
               return false;
           });
           popupMenu.show();
       }
    }
}
