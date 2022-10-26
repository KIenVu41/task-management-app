package com.kma.taskmanagement.ui.chart;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.List;

public class SubFragmentChart extends Fragment {

    private static final String ARG_PARAM1 = "INDEX";

    private int mParam1;
    private boolean isRefresh = false;

    AAChartView aaChartView;

    public SubFragmentChart() {
        // Required empty public constructor
    }

    public static SubFragmentChart newInstance(int param1) {
        SubFragmentChart fragment = new SubFragmentChart();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sub_chart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        aaChartView = view.findViewById(R.id.AAChartView);
        AAChartModel aaChartModel = new AAChartModel()
                .chartType(AAChartType.Pie)
                .backgroundColor("#ffffff")
                .title("Thống kê công việc cá nhân")
                .subtitle("Năm 2022")
                .dataLabelsEnabled(true)//是否直接显示扇形图数据
              //  .series(configurePieSeries());
                .yAxisTitle("℃")
                .series(new AASeriesElement[]{
                        new AASeriesElement()
                                .name("Số công việc")
                                .size("40%")//尺寸大小
                                .innerSize("30%")//内部圆环半径大小占比
                                .borderWidth(0f)//描边的宽度
                                .allowPointSelect(false)//是否允许在点击数据点标记(扇形图点击选中的块发生位移)
                                .data(new Object[][]{
                                {"Todo", 33},
                                {"Doing", 20},
                                {"Complete", 50},
                        })
                });
            aaChartView.aa_drawChartWithChartModel(aaChartModel);
    }

    private Object[] configurePieSeries() {
        List<AASeriesElement> aaSeriesElementList = new ArrayList<>();
        AASeriesElement aaSeriesElement = new AASeriesElement();

        aaSeriesElement.size("46%")
                .innerSize("55%")
                .dataLabels(new AADataLabels()
                .enabled(true)
                .align("left")
                .softConnector(false)
                .overflow("none")
                .distance(24f)
                .style(AAStyle.style("#000000", 10f))
                .color(AAColor.Black)
                .format("{point.name}<br>{point.percentage:.1f} %"))
                .borderWidth(2f)
                .borderColor(AAColor.White)
                .allowPointSelect(false);
//                .data(new Object[][]);

        aaSeriesElementList.add(aaSeriesElement);
        return aaSeriesElementList.toArray();
    }
}