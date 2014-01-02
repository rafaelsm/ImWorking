package br.com.rads.imworking.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import br.com.rads.imworking.R;
import br.com.rads.imworking.adapters.TodayListAdapter;
import br.com.rads.imworking.model.Check;
import br.com.rads.imworking.model.Day;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class TodayFragment extends Fragment {

    private static final int HOURS_DAY = 8;
    public static String TIME_PATTERN = "%H:%M";

    private Day today;
    private List<Check> checks = new ArrayList<Check>();

    private ListView checkListView;
    private TextView dayNumberTextView;
    private TextView hoursWorkedTextView;
    private TextView hoursLeftTextView;

    public ActionMode actionMode;

    private OnTodayLoadListener todayListener;

    public interface OnTodayLoadListener{
        public void onTodayFinishLoad();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            todayListener = (OnTodayLoadListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException( activity.toString() + " must implement OnTodayLoadListener");
        }

    }

    public TodayFragment( List<Check> todayChecks){
        Time day = new Time();
        day.setToNow();
        this.today = new Day(day);

        this.checks = todayChecks;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        checkListView = (ListView) rootView.findViewById(R.id.listview);
        dayNumberTextView = (TextView) rootView.findViewById(R.id.day_number_tv);
        hoursWorkedTextView = (TextView) rootView.findViewById(R.id.worked_tv);
        hoursLeftTextView = (TextView) rootView.findViewById(R.id.left_tv);

        dayNumberTextView.setText(this.today.getTime().format("%d/%m"));
        hoursWorkedTextView.setText("00:00");
        hoursLeftTextView.setText("08:00");

        TodayListAdapter adapter = new TodayListAdapter(this.getActivity(), checks);
        checkListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        checkListView.setAdapter(adapter);
        checkListView.setOnItemLongClickListener(new TodayListerner());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean showSeconds = preferences.getBoolean("pref_seconds", false);

        TIME_PATTERN = showSeconds ? "%H:%M:%S" : "%H:%M";

        ((BaseAdapter) this.checkListView.getAdapter()).notifyDataSetChanged();
        calculateHoursRemaining();
    }

    public void calculateHoursRemaining() {
        if (this.checks != null && this.checks.size() > 0) {

            long eightHours = HOURS_DAY * 60 * 60 * 1000;
            long timeLeft = eightHours;
            long timeWorked = 0;

            for (Check c : checks) {
                long diff = c.differenceBetweenInAndOut();
                timeLeft -= diff;
                timeWorked += diff;
            }

            Time tLeft = new Time(TimeZone.getDefault().toString());
            tLeft.set(timeLeft);
            this.hoursLeftTextView.setText(tLeft.format(TIME_PATTERN));
            this.hoursLeftTextView.invalidate();

            Time tWorked = new Time(TimeZone.getDefault().toString());
            tWorked.set(timeWorked);
            this.hoursWorkedTextView.setText(tWorked.format(TIME_PATTERN));
            this.hoursWorkedTextView.invalidate();

        }
    }

    public void addCheckIn() {
        Time t = new Time();
        t.setToNow();
        this.checks.add(new Check(t));
        ((BaseAdapter) this.checkListView.getAdapter()).notifyDataSetChanged();
    }

    public void addCheckOut() {

        //remove if after development
        if (this.checks.size() > 0) {
            Time t = new Time();
            t.setToNow();
            this.checks.get(checks.size() - 1).setCheckOut(t);
            this.getView().invalidate();
            ((BaseAdapter) this.checkListView.getAdapter()).notifyDataSetChanged();
        }
    }

    public Check getLastCheck() {
        if (this.checks != null && this.checks.size() > 0)
            return this.checks.get(checks.size() - 1);
        else
            return null;
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.cab_menu_checks, menu);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.cab_delete:
                    deleteCheck();
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

    private void deleteCheck() {
        Toast.makeText(this.getActivity(), "Bom vai deletar...", Toast.LENGTH_LONG).show();
    }

    private class TodayListerner implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {

            Check c = checks.get(pos);

            if (actionMode != null) {
                return false;
            }

            actionMode = getActivity().startActionMode(actionModeCallback);
            view.setSelected(true);
            return true;
        }
    }

}