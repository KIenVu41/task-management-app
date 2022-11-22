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
import com.kma.taskmanagement.data.model.Chart;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.ui.adapter.FragmentChartAdapter;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartFragment extends Fragment {

    ViewPager viewpager;
    SpringDotsIndicator springDotsIndicator;
    FragmentChartAdapter fragmentChartAdapter;
    ImageView ivBack, ivNext;
    TextView tvIndex;
    int index = -1;
    private TaskViewModel taskViewModel;
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    private String token = "";
    List<Chart> chartList = new ArrayList<>();

    public ChartFragment() {
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
//        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
//        taskViewModel = new ViewModelProvider(requireActivity(), new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);
//        taskViewModel.getPersonalChart(Constants.BEARER + token);
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        taskViewModel.getChartResult().observe(requireActivity(), new Observer<Chart>() {
//            @Override
//            public void onChanged(Chart chart) {
//                if(chart != null) {
//                    chartList.add(chart);
//                }
//            }
//        });

        initView(view);
        setOnClick();

    }

    private void initView(View view) {
        viewpager = view.findViewById(R.id.view_pager);
        springDotsIndicator = view.findViewById(R.id.spring_dots_indicator);
        tvIndex = view.findViewById(R.id.chart_index);
        ivBack = view.findViewById(R.id.ivBack);
        ivNext = view.findViewById(R.id.ivNext);
        fragmentChartAdapter = new FragmentChartAdapter(getChildFragmentManager(), 2, Arrays.asList(getActivity().getResources().getString(R.string.chart_personal_title),
                getActivity().getResources().getString(R.string.chart_group_title)), chartList);
        viewpager.setAdapter(fragmentChartAdapter);
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