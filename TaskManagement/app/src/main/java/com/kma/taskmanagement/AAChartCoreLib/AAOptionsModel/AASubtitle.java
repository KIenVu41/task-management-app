package com.kma.taskmanagement.AAChartCoreLib.AAOptionsModel;

import com.kma.taskmanagement.AAChartCoreLib.AAChartEnum.AAChartVerticalAlignType;

public class AASubtitle {
    public String text;
    public AAStyle style;
    public String align;
    public String verticalAlign;
    public Number x;
    public Number y;
    public Boolean userHTML;

    public AASubtitle text(String prop) {
        text = prop;
        return this;
    }

    public AASubtitle style(AAStyle prop) {
        style = prop;
        return this;
    }

    public AASubtitle align(String prop) {
        align = prop;
        return this;
    }

    public AASubtitle verticalAlign(AAChartVerticalAlignType prop) {
        verticalAlign = prop.toString();
        return this;
    }

    public AASubtitle x(Number prop) {
        x = prop;
        return this;
    }

    public AASubtitle y(Number prop) {
        y = prop;
        return this;
    }

    public AASubtitle userHTML(Boolean prop) {
        userHTML = prop;
        return this;
    }
}
