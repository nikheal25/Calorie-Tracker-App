package com.example.myapplication;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity
public class Stepstaken {
    @PrimaryKey
    @NonNull
    private String date;

    @ColumnInfo(name = "steps_taken")
    private int stepstaken;


    public Stepstaken(String date, int stepstaken) {
        this.date = date;
        this.stepstaken = stepstaken;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStepstaken() {
        return stepstaken;
    }

    public void setStepstaken(int stepstaken) {
        this.stepstaken = stepstaken;
    }
}
