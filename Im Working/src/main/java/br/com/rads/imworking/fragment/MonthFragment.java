package br.com.rads.imworking.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class MonthFragment extends Fragment {

    private CalendarView calendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_month, container, false);

        calendarView = (CalendarView) rootView.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener( new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                Toast.makeText(getActivity(), "year-" + year + " month-" + month + " day-" + day, Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }
}
