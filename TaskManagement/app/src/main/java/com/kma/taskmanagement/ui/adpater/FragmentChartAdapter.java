package com.kma.taskmanagement.ui.adpater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kma.taskmanagement.data.model.Chart;
import com.kma.taskmanagement.ui.chart.SubFragmentChart;
import com.kma.taskmanagement.ui.main.fragments.AssignedTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.GroupTaskFragment;
import com.kma.taskmanagement.ui.main.fragments.MyTaskFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FragmentChartAdapter extends FragmentPagerAdapter {

    private int numItems = -1;
    List<String> titles = new ArrayList<>();
    List<Chart> charts = new ArrayList<>();

    public FragmentChartAdapter(@NonNull FragmentManager fm, int numItems, List<String> titles, List<Chart> charts) {
        super(fm);
        this.numItems = numItems;
        this.titles = titles;
        this.charts = charts;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
//        SubFragmentChart subFragmentChart = SubFragmentChart.newInstance(position);
//        fragments.put(position, subFragmentChart);
//        return subFragmentChart;
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return SubFragmentChart.newInstance(position, "personal", titles.get(0));
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return SubFragmentChart.newInstance(position, "group", titles.get(1));
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numItems;
    }

    public void setCharts(List<Chart> charts) {
        this.charts = charts;
        notifyDataSetChanged();
    }
}
