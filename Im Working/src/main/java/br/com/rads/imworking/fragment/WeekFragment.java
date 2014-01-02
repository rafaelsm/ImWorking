package br.com.rads.imworking.fragment;

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

    public interface OnCheckWeekListener {
        public void onCheckoutUpdateWeek();
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
                sundayTotalTimeWorked.setText(getWorkedText(day));
                break;
            case 1:
                mondayView.setText(formattedDay);
                mondayTotalTimeWorked.setText(getWorkedText(day));
                break;
            case 2:
                tuesdayView.setText(formattedDay);
                tuesdayTotalTimeWorked.setText(getWorkedText(day));
                break;
            case 3:
                wednesdayView.setText(formattedDay);
                wednesdayTotalTimeWorked.setText(getWorkedText(day));
                break;
            case 4:
                thursdayView.setText(formattedDay);
                thursdayTotalTimeWorked.setText(getWorkedText(day));
                break;
            case 5:
                fridayView.setText(formattedDay);
                fridayTotalTimeWorked.setText(getWorkedText(day));
                break;
            case 6:
                saturdayView.setText(formattedDay);
                saturdayTotalTimeWorked.setText(getWorkedText(day));
                break;
        }
    }

    private String getWorkedText(Day day) {
        long timeInMillis = getHoursWorked(day);
        Time worked = new Time(TimeZone.getDefault().toString());
        worked.set(timeInMillis);
        String workedText = worked.format("%H:%M:%S");
        return workedText;

    }

    public long getHoursWorked(Day workedDay) {
        long hoursInMillis = 0;

        DataManager manager = DataManager.getInstance();

        List<Check> checksForWorkedDay = manager.loadChecks(this.getActivity(),workedDay.getTime());

        for (Check check : checksForWorkedDay) {
            hoursInMillis += check.differenceBetweenInAndOut();
            Log.d(TAG, "check=" + check.toString());
        }

        return hoursInMillis;
    }

}
