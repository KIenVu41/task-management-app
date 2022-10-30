package com.kma.taskmanagement.ui.chart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kma.taskmanagement.AAChartCoreLib.AAChartCreator.AAChartModel;
import com.kma.taskmanagement.AAChartCoreLib.AAChartCreator.AAChartView;
import com.kma.taskmanagement.AAChartCoreLib.AAChartCreator.AASeriesElement;
import com.kma.taskmanagement.AAChartCoreLib.AAChartEnum.AAChartAnimationType;
import com.kma.taskmanagement.AAChartCoreLib.AAChartEnum.AAChartType;
import com.kma.taskmanagement.AAChartCoreLib.AAOptionsModel.AADataLabels;
import com.kma.taskmanagement.AAChartCoreLib.AAOptionsModel.AAPie;
import com.kma.taskmanagement.AAChartCoreLib.AAOptionsModel.AAStyle;
import com.kma.taskmanagement.AAChartCoreLib.AATools.AAColor;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.data.model.Chart;
import com.kma.taskmanagement.data.repository.TaskRepository;
import com.kma.taskmanagement.data.repository.impl.TaskRepositoryImpl;
import com.kma.taskmanagement.ui.main.TaskViewModel;
import com.kma.taskmanagement.ui.main.TaskViewModelFactory;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.GlobalInfor;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class SubFragmentChart extends Fragment {

    private static final String ARG_PARAM1 = "INDEX";
    private static final String ARG_PARAM2 = "TYPE";
    private static final String ARG_PARAM3 = "TITLE";
    private static final String ARG_PARAM4 = "OBJ";
    private TaskViewModel taskViewModel;
    private TaskRepository taskRepository = new TaskRepositoryImpl();
    private String token = "";

    private int mParam1;
    private String mParam2, mParam3;
    private boolean isRefresh = false;
    private Chart chartValue;
    AAChartView aaChartView;

    public SubFragmentChart() {
        // Required empty public constructor
    }

    public static SubFragmentChart newInstance(int param1, String type, String title) {
        SubFragmentChart fragment = new SubFragmentChart();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, type);
        args.putString(ARG_PARAM3, title);
        //args.putSerializable(ARG_PARAM4, chart);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            //chart = (Chart) getArguments().getSerializable(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        token = SharedPreferencesUtil.getInstance(getActivity().getApplicationContext()).getUserToken(Constants.TOKEN + GlobalInfor.username);
        taskViewModel = new ViewModelProvider(this, new TaskViewModelFactory(taskRepository)).get(TaskViewModel.class);
        if(mParam2.equals("personal")) {
            taskViewModel.getPersonalChart(Constants.BEARER + token);
        } else {
            taskViewModel.getGroupChart(Constants.BEARER + token);
        }
        return inflater.inflate(R.layout.fragment_sub_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        taskViewModel.getChartResult().observe(requireActivity(), new Observer<Chart>() {
            @Override
            public void onChanged(Chart chart) {
                if(chart != null) {
                    Log.d("TAG", chart.toString());
                    //chartValue.add(chart);
                    configureChart(chart);
                }
            }
        });

        taskViewModel.getChart2Result().observe(requireActivity(), new Observer<Chart>() {
            @Override
            public void onChanged(Chart chart) {
                if(chart != null) {
                    Log.d("TAG", chart.toString());
                    //chartValue.add(chart);
                    configureChart(chart);
                }
            }
        });

        aaChartView = view.findViewById(R.id.AAChartView);
    }

    private void configureChart(Chart chart) {
        AAChartModel aaChartModel = new AAChartModel()
                .chartType(AAChartType.Pie)
                .backgroundColor("#ffffff")
                .title(mParam3)
                .subtitle("Năm 2022")
                .dataLabelsEnabled(true)//是否直接显示扇形图数据
                .series(new AASeriesElement[]{
                        new AASeriesElement()
                                .name("Số công việc")
                                .size("40%")//尺寸大小
                                .innerSize("30%")//内部圆环半径大小占比
                                .borderWidth(2f)//描边的宽度
                                .allowPointSelect(false)
                                .borderColor(AAColor.White)
                                .data(new Object[][]{
                                {"Todo", Integer.valueOf(chart.getTODO())},
                                {"Doing", Integer.valueOf(chart.getDOING())},
                                {"Complete", Integer.valueOf(chart.getCOMPLETED())},
                        })
                });
        aaChartView.aa_drawChartWithChartModel(aaChartModel);
    }
}