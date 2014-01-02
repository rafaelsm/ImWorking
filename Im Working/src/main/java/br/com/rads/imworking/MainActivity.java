package br.com.rads.imworking;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import br.com.rads.imworking.fragment.MonthFragment;
import br.com.rads.imworking.fragment.TodayFragment;
import br.com.rads.imworking.fragment.WeekFragment;
import br.com.rads.imworking.model.Check;
import br.com.rads.imworking.model.CheckType;
import br.com.rads.imworking.model.Day;
import br.com.rads.imworking.util.DataManager;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, WeekFragment.OnCheckWeekListener, TodayFragment.OnTodayLoadListener {

    private static final String TAG = "MainActivity";

    private TodayFragment todayFragment;
    private WeekFragment weekFragment;
    private MonthFragment monthFragment;

    SectionsPagerAdapter tabsPageAdapter;
    ViewPager viewPager;

    private List<Check> todayChecks = new ArrayList<Check>();
    private List<Day> weekDaysWorked = new ArrayList<Day>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        loadTodayChecks();
        loadWeekDaysWorked();

        //Initiate fragments
        todayFragment = new TodayFragment(todayChecks);
        weekFragment = new WeekFragment(weekDaysWorked);
        monthFragment = new MonthFragment();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        tabsPageAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(tabsPageAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < tabsPageAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(tabsPageAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

    }

    private void loadTodayChecks() {

        Time today = new Time();
        today.setToNow();

        DataManager manager = DataManager.getInstance();
        this.todayChecks = manager.loadChecks(this, today);

    }

    private void loadWeekDaysWorked() {

        Time day = new Time(TimeZone.getDefault().toString());
        day.setToNow();

        if (day.weekDay == 0) {
            updateFirstDayOfWeek();
        }
         else {

            int subtract = day.weekDay;
            subtract = subtract * 24 * 60 * 60 * 1000;
            long sundayInMillis = day.toMillis(false) - subtract;
            day.set(sundayInMillis);

            Day sunday = new Day (day);
            this.weekDaysWorked.add(sunday);

            Time mondayTime = new Time(TimeZone.getDefault().toString());
            Time tuesdayTime = new Time(TimeZone.getDefault().toString());
            Time wednesdayTime = new Time(TimeZone.getDefault().toString());
            Time thursdayTime = new Time(TimeZone.getDefault().toString());
            Time fridayTime = new Time(TimeZone.getDefault().toString());
            Time saturdayTime = new Time(TimeZone.getDefault().toString());

            int oneMoreDay = 24 * 60 * 60 * 1000;
            mondayTime.set(day.toMillis(false) + oneMoreDay);
            tuesdayTime.set(mondayTime.toMillis(false) +  oneMoreDay);
            wednesdayTime.set(tuesdayTime.toMillis(false) +  oneMoreDay);
            thursdayTime.set(wednesdayTime.toMillis(false) +  oneMoreDay);
            fridayTime.set(thursdayTime.toMillis(false) + oneMoreDay);
            saturdayTime.set(fridayTime.toMillis(false) + oneMoreDay);

            this.weekDaysWorked.add(new Day(mondayTime));
            this.weekDaysWorked.add(new Day(tuesdayTime));
            this.weekDaysWorked.add(new Day(wednesdayTime));
            this.weekDaysWorked.add(new Day(thursdayTime));
            this.weekDaysWorked.add(new Day(fridayTime));
            this.weekDaysWorked.add(new Day(saturdayTime));

        }
    }

    private void updateFirstDayOfWeek() {

        Time day = new Time(TimeZone.getDefault().toString());
        day.setToNow();


        for (int i = 0; i < 7; i++) {

            long oneMoreDay = 24 * 60 * 60 * 1000;

            day.set(day.toMillis(false) + oneMoreDay);
           this.weekDaysWorked.add( new Day(day));

        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        savepoint();
    }

    private void savepoint() {

        SharedPreferences preferences = this.getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (!preferences.contains(CheckType.CHECK_IN.toString())) {
            editor.putBoolean(CheckType.CHECK_IN.toString(), true);
        }

        if (preferences.getBoolean(CheckType.CHECK_IN.toString(), true)) {
            editor.putBoolean(CheckType.CHECK_IN.toString(), false);
            todayFragment.addCheckIn();
        } else {
            editor.putBoolean(CheckType.CHECK_IN.toString(), true);
            todayFragment.addCheckOut();
            todayFragment.calculateHoursRemaining();
            onCheckoutUpdateWeek();
        }

        editor.commit();

        saveCheck();
        clearDataForDebug(false);
    }

    private void clearDataForDebug(boolean clear) {
        if (clear) {

            Time t = new Time();
            t.setToNow();

            DataManager manager = DataManager.getInstance();
            manager.clearData(this, t);
        }
    }

    private void saveCheck() {
        DataManager manager = DataManager.getInstance();
        Check c = todayFragment.getLastCheck();

        if (c != null)
            manager.saveCheck(this, c);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_write_tag:
                startActivity(new Intent(MainActivity.this, WriteTagActivity.class));
                break;
            case R.id.action_erase_tag:
                Toast.makeText(this, getString(R.string.toast_function_not_implemented), Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;
        }

        return true;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTodayFinishLoad() {
        this.weekFragment.updateWeek();
    }

    @Override
    public void onCheckoutUpdateWeek() {
        this.weekFragment.updateWeek();
    }

    public class SectionsPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            switch (position + 1) {
                case 1:
                    return todayFragment;
                case 2:
                    return weekFragment;
                case 3:
                    return monthFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

}
