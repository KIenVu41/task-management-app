package com.kma.taskmanagement.AAChartCoreLib.AAChartCreator;

import com.kma.taskmanagement.AAChartCoreLib.AATools.AAJSStringPurer;

public class AAChartEvents {
    public String load;
    public String selection;


    public AAChartEvents load(String prop) {
        String pureJSFunctionStr = "(" + prop + ")";
        pureJSFunctionStr = AAJSStringPurer.pureJavaScriptFunctionString(pureJSFunctionStr);
        load = pureJSFunctionStr;
        return this;
    }

    public AAChartEvents selection(String prop) {
        String pureJSFunctionStr = "(" + prop + ")";
        pureJSFunctionStr = AAJSStringPurer.pureJavaScriptFunctionString(pureJSFunctionStr);
        selection = pureJSFunctionStr;
        return this;
    }
}
