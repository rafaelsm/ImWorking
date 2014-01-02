package br.com.rads.imworking.model;

import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class Day {

    private Time day;
    private List<Check> checkList;

    public Day(Time day) {
        this.day = day;
        this.checkList = new ArrayList<Check>();
    }

    public List<Check> getCheckList() {
        return this.checkList;
    }

    public void addCheck(Check check) {
        this.checkList.add(check);
    }

    public Time getTime() {
        return day;
    }

    public static void showWeek() {
        Time t = new Time(TimeZone.getDefault().toString());
        t.setToNow();


        if (t.weekDay > 0) {

            for (int i = 0; i < 7; i++) {
                long oneMoreDay = i * 24 * 60 * 60 * 1000;
                t.set(t.toMillis(false) + oneMoreDay);
                Log.d("WEEK", t.format("%d=") + t.format("%A=") + t.weekDay);
                t.setToNow();
            }

        }


        switch (t.weekDay) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
        }

    }

    @Override
    public String toString() {
        return this.getTime().monthDay + "/" + (this.getTime().month+1) + "/" + this.getTime().year;
    }
}
