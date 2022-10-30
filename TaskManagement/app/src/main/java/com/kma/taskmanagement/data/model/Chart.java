package com.kma.taskmanagement.data.model;

import java.io.Serializable;

public class Chart implements Serializable {
    private String TODO;
    private String DOING;
    private String COMPLETED;

    public Chart() {
    }

    public Chart(String TODO, String DOING, String COMPLETED) {
        this.TODO = TODO;
        this.DOING = DOING;
        this.COMPLETED = COMPLETED;
    }

    public String getTODO() {
        return TODO;
    }

    public void setTODO(String TODO) {
        this.TODO = TODO;
    }

    public String getDOING() {
        return DOING;
    }

    public void setDOING(String DOING) {
        this.DOING = DOING;
    }

    public String getCOMPLETED() {
        return COMPLETED;
    }

    public void setCOMPLETED(String COMPLETED) {
        this.COMPLETED = COMPLETED;
    }

    @Override
    public String toString() {
        return "Chart{" +
                "TODO='" + TODO + '\'' +
                ", DOING='" + DOING + '\'' +
                ", COMPLETED='" + COMPLETED + '\'' +
                '}';
    }
}
