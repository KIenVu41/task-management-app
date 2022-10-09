package com.kma.taskmanagement.AAChartCoreLib.AAOptionsModel;

import com.kma.taskmanagement.AAChartCoreLib.AAChartEnum.AAChartAlignType;
import com.kma.taskmanagement.AAChartCoreLib.AAChartEnum.AAChartVerticalAlignType;

public class AATitle {
    public String text;
    public AAStyle style;
    public String align;
    public String verticalAlign;
    public Number x;
    public Number y;
    public Boolean userHTML;

    public AATitle text(String prop) {
        text = prop;
        return this;
    }

    public AATitle style(AAStyle prop) {
        style = prop;
        return this;
    }

    public AATitle align(String prop) {
        align = prop;
        return this;
    }

    public AATitle verticalAlign(String prop) {
        verticalAlign = prop;
        return this;
    }

    public AATitle x(Number prop) {
        x = prop;
        return this;
    }

    public AATitle y(Number prop) {
        y = prop;
        return this;
    }

    public AATitle userHTML(Boolean prop) {
        userHTML = prop;
        return this;
    }
}
