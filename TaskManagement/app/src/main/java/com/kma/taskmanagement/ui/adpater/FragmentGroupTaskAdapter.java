package com.kma.taskmanagement.ui.adpater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kma.taskmanagement.ui.chart.SubFragmentChart;
import com.kma.taskmanagement.ui.main.fragments.AssignedTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.MyTaskFragment;

import java.util.LinkedHashMap;
import java.util.Map;

public class FragmentGroupTaskAdapter  extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 3;

    public FragmentGroupTaskAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return GroupTaskFragment.newInstance();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return AssignedTaskFragment.newInstance();
            case 2: // Fragment # 0 - This will show FirstFragment different title
                return MyTaskFragment.newInstance();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
