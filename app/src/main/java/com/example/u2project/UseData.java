package com.example.u2project;

import android.app.Application;

public class UseData extends Application {

    private int totalidx;
    private int Score;

    public int getScore() {
        return Score;
    }

    public void setScore(int Score) {
        this.Score = Score;
    }

    public int getTotalIndex() {
        return totalidx;
    }

    public void setTotalIndex(int totalindex) {
        this.totalidx = totalindex;
    }

}
