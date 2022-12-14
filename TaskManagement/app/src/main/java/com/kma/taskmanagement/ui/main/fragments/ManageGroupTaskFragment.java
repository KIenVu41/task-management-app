package com.kma.taskmanagement.ui.main.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kma.taskmanagement.R;
import com.kma.taskmanagement.ui.adapter.FragmentGroupTaskAdapter;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

public class ManageGroupTaskFragment extends Fragment {

    ViewPager viewpager;
    SpringDotsIndicator springDotsIndicator;
    FragmentGroupTaskAdapter fragmentGroupTaskAdapter;
    ImageView ivBack, ivNext;
    TextView tvIndex;
    int index = -1;

    public ManageGroupTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage_group_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        setOnClick();

    }

    private void initView(View view) {
        viewpager = view.findViewById(R.id.view_pager2);
        springDotsIndicator = view.findViewById(R.id.spring_dots_indicator);
        tvIndex = view.findViewById(R.id.chart_index);
        ivBack = view.findViewById(R.id.ivBack);
        ivNext = view.findViewById(R.id.ivNext);
        fragmentGroupTaskAdapter = new FragmentGroupTaskAdapter(getChildFragmentManager());
        viewpager.setAdapter(fragmentGroupTaskAdapter);
        springDotsIndicator.attachTo(viewpager);
        viewpager.setCurrentItem(0);
        viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setIndex();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setOnClick() {
        ivBack.setOnClickListener(view -> {
            viewpager.setCurrentItem(getItem(-1), true);
            setIndex();
        });

        ivNext.setOnClickListener(view -> {
            viewpager.setCurrentItem(getItem(1), true);
            setIndex();
        });
    }

    private int getItem(int i) {
        return viewpager.getCurrentItem() + i;
    }

    private void setIndex() {
        index = viewpager.getCurrentItem() + 1;
        tvIndex.setText(index + "/" + 2);
    }

    @Override
    public void onResume() {
        super.onResume();
        setIndex();
    }
}