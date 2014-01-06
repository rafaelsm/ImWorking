package br.com.rads.imworking.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.TimeZone;

import br.com.rads.imworking.R;
import br.com.rads.imworking.model.Check;
import br.com.rads.imworking.model.Day;
import br.com.rads.imworking.util.DataManager;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class WeekFragment extends Fragment {

    private static final String TAG = "WeekFragment";

    private TextView sundayView;
    private TextView mondayView;
    private TextView tuesdayView;
    private TextView wednesdayView;
    private TextView thursdayView;
    private TextView fridayView;
    private TextView saturdayView;

    private TextView sundayTotalTimeWorked;
    private TextView mondayTotalTimeWorked;
    private TextView tuesdayTotalTimeWorked;
    private TextView wednesdayTotalTimeWorked;
    private TextView thursdayTotalTimeWorked;
    private TextView fridayTotalTimeWorked;
    private TextView saturdayTotalTimeWorked;


    private List<Day> daysOfWeek;

    public WeekFragment(List<Day> weekDaysWorked) {
        this.daysOfWeek = weekDaysWorked;
    }

    private OnCheckWeekListener weekListener;

    public interface OnCheckWeekListener {
        public void onCheckoutUpdateWeek(Day day);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            this.weekListener = (OnCheckWeekListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException( activity.toString() + " must implement OnCheckWeekListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_week, container, false);

        //Dias da semana pattern: 01/01
        sundayView = (TextView) rootView.findViewById(R.id.sunday_day);
        mondayView = (TextView) rootView.findViewById(R.id.monday_day);
        tuesdayView = (TextView) rootView.findViewById(R.id.tuesday_day);
        wednesdayView = (TextView) rootView.findViewById(R.id.wednesday_day);
        thursdayView = (TextView) rootView.findViewById(R.id.thursday_day);
        fridayView = (TextView) rootView.findViewById(R.id.friday_day);
        saturdayView = (TextView) rootView.findViewById(R.id.saturday_day);

        //Total de horas trabalhadas por dia
        sundayTotalTimeWorked = (TextView) rootView.findViewById(R.id.sunday_total_time_worked);
        mondayTotalTimeWorked = (TextView) rootView.findViewById(R.id.monday_total_time_worked);
        tuesdayTotalTimeWorked = (TextView) rootView.findViewById(R.id.tuesday_total_time_worked);
        wednesdayTotalTimeWorked = (TextView) rootView.findViewById(R.id.wednesday_total_time_worked);
        thursdayTotalTimeWorked = (TextView) rootView.findViewById(R.id.thursday_total_time_worked);
        fridayTotalTimeWorked = (TextView) rootView.findViewById(R.id.friday_total_time_worked);
        saturdayTotalTimeWorked = (TextView) rootView.findViewById(R.id.saturday_total_time_worked);

        updateWeek();

        return rootView;
    }


    public void updateWeek() {

        for (Day day : daysOfWeek) {
            String formattedDay = day.getTime().format("%d/%m");
            setDayForWeek(formattedDay,day);
        }

    }

    private void setDayForWeek(String formattedDay, Day day) {

        switch (day.getTime().weekDay) {
            case 0:
                sundayView.setText(formattedDay);
                break;
            case 1:
                mondayView.setText(formattedDay);
                break;
            case 2:
                tuesdayView.setText(formattedDay);
                break;
            case 3:
                wednesdayView.setText(formattedDay);
                break;
            case 4:
                thursdayView.setText(formattedDay);
                break;
            case 5:
                fridayView.setText(formattedDay);
                break;
            case 6:
                saturdayView.setText(formattedDay);
                break;
        }
    }

    public void updateDay(Day day, long workTime) {
        Time worked = new Time(TimeZone.getDefault().toString());
        worked.set(workTime);
        String workedText = worked.format("%H:%M:%S");

        switch (day.getTime().weekDay) {
            case 0:
                sundayTotalTimeWorked.setText(workedText);
                break;
            case 1:
                mondayTotalTimeWorked.setText(workedText);
                break;
            case 2:
                tuesdayTotalTimeWorked.setText(workedText);
                break;
            case 3:
                wednesdayTotalTimeWorked.setText(workedText);
                break;
            case 4:
                thursdayTotalTimeWorked.setText(workedText);
                break;
            case 5:
                fridayTotalTimeWorked.setText(workedText);
                break;
            case 6:
                saturdayTotalTimeWorked.setText(workedText);
                break;
        }
    }

}
