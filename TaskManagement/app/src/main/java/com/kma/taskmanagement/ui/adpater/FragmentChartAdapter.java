package com.kma.taskmanagement.ui.adpater;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.kma.taskmanagement.ui.chart.SubFragmentChart;

import java.util.LinkedHashMap;
import java.util.Map;

public class FragmentChartAdapter extends FragmentStatePagerAdapter {

    private int numItems = -1;
    public Map<Integer, SubFragmentChart> fragments = new LinkedHashMap<>();

    public FragmentChartAdapter(@NonNull FragmentManager fm, int numItems) {
        super(fm);
        this.numItems = numItems;
        this.fragments = new LinkedHashMap<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        SubFragmentChart subFragmentChart = SubFragmentChart.newInstance(position);
        fragments.put(position, subFragmentChart);
        return subFragmentChart;
    }

    @Override
    public int getCount() {
        return numItems;
    }
}
