package com.kma.taskmanagement.AAChartCoreLib.AAOptionsModel;

import com.kma.taskmanagement.AAChartCoreLib.AATools.AAJSStringPurer;

public class AASeriesEvents {
    public String legendItemClick;

    public AASeriesEvents legendItemClick(String prop) {
        String pureJSFunctionStr = "(" + prop + ")";
        pureJSFunctionStr = AAJSStringPurer.pureJavaScriptFunctionString(pureJSFunctionStr);
        legendItemClick = pureJSFunctionStr;
        return this;
    }

}
