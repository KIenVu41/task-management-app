package com.kma.taskmanagement.ui.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.kma.taskmanagement.ui.main.fragments.GroupTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.ChartFragment;
import com.kma.taskmanagement.ui.main.fragments.PersonTaskFragment;


public class TaskPagerAdapter extends FragmentStateAdapter {

    public TaskPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    // return fragments at every position
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PersonTaskFragment();
            case 1:
                return new GroupTaskFragment();
            case 2:
                return new ChartFragment();
        }
        return new PersonTaskFragment();
    }


    @Override
    public int getItemCount() {
        return 3;
    }
}
