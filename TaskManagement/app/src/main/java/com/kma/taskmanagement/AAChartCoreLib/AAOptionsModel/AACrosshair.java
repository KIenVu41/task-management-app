package com.kma.taskmanagement.AAChartCoreLib.AAOptionsModel;

public class AACrosshair {
    public Number width;
    public String color;
    public String dashStyle;

    public AACrosshair width(Number prop) {
        width = prop;
        return this;
    }

    public AACrosshair color(String prop) {
        color = prop;
        return this;
    }

    public AACrosshair dashStyle(String prop) {
        dashStyle = prop;
        return this;
    }
}
