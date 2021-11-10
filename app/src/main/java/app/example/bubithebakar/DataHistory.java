package app.example.bubithebakar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import app.example.bubithebakar.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataHistory extends Activity {

    private final Date firstDayOfMonth = getFirstDateOfCurrentMonth();
    private final Date lastDayOfMonth = getLastDateOfCurrentMonth();
    private final Date currentDay = getCurrentDate();
    private final int monthMaxWorkingDays = getWorkingDaysBetweenTwoDates(firstDayOfMonth, lastDayOfMonth);
    private final int monthCurrentWorkingDay = getWorkingDaysBetweenTwoDates(firstDayOfMonth, currentDay);
    private final int monthWorkingDaysLeft = getWorkingDaysBetweenTwoDates(currentDay, lastDayOfMonth);
    private TextView tvBakarotDarush, tvTikufimDarush, tvKnasotDarush, tvBakarotYesh, tvTikufimYesh, tvKnasotYesh, tvDaysLeft;
    private int misparTikufimStart = 0, misparBakarotStart = 0, misparKnasotStart = 0, misparTikufim = 0, misparBakarot = 0, misparKnasot = 0;
    private BottomNavigationView btnNav;
    private Intent intent;
    private SharedPreferences mPrefs;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_data_history);

        user = readUser();

        tvBakarotDarush = findViewById(R.id.tvBakarotDarush);
        tvKnasotDarush = findViewById(R.id.tvKnasotDarush);
        tvTikufimDarush = findViewById(R.id.tvTikufimDarush);
        tvBakarotYesh = findViewById(R.id.tvBakarotYesh);
        tvTikufimYesh = findViewById(R.id.tvTikufimYesh);
        tvKnasotYesh = findViewById(R.id.tvKnasotYesh);
        btnNav = findViewById(R.id.btnNav);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);

        misparBakarotStart = user.getMonthlyBakarotGoal();
        misparBakarot = user.getMonthlyBakarot();
        misparTikufimStart = user.getMonthlyTikufimGoal();
        misparTikufim = user.getMonthlyTikufim();
        misparKnasotStart = user.getMonthlyKnasotGoal();
        misparKnasot = user.getMonthlyKnasot();

        tvBakarotDarush.setText("מספר הבקרות היומי הדרוש להשגת המטרה: " + (String.format(Locale.US, "%.2f", (((float) misparBakarotStart / monthMaxWorkingDays)))));
        tvTikufimDarush.setText("מספר התיקופים היומי הדרוש להשגת המטרה: " + (String.format(Locale.US, "%.2f", (((float) misparTikufimStart / monthMaxWorkingDays)))));
        tvKnasotDarush.setText("מספר הקנסות היומי הדרוש להשגת המטרה: " + (String.format(Locale.US, "%.2f", (((float) misparKnasotStart / monthMaxWorkingDays)))));

        tvBakarotYesh.setText("מספר הבקרות היומי שלך: " + (String.format(Locale.US, "%.2f", (((float) misparBakarot / monthCurrentWorkingDay)))));
        tvTikufimYesh.setText("מספר התיקופים היומי שלך: " + (String.format(Locale.US, "%.2f", (((float) misparTikufim / monthCurrentWorkingDay)))));
        tvKnasotYesh.setText("מספר הקנסות היומי שלך: " + (String.format(Locale.US, "%.2f", (((float) misparKnasot / monthCurrentWorkingDay)))));

        tvDaysLeft.setText("נותרו "+ monthWorkingDaysLeft +" ימי עבודה");


        GlobalFunctions globalFunction = new GlobalFunctions(this);
        globalFunction.checkWifi();
        globalFunction.hideSystemUI();

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView1);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                try {
                    String today = dayOfMonth + "-" + (month + 1) + "-" + year;
                    Toast.makeText(getApplicationContext(), user.getHistory().get(today).toString(), Toast.LENGTH_LONG).show();
                } catch (Throwable e) {
                    Toast.makeText(getApplicationContext(), "אין לך נתונים באותו יום.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_data:
                        intent = new Intent(DataHistory.this, Data.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_leaderboard:
                        intent = new Intent(DataHistory.this, LeaderBoard.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        Intent myIntent = new Intent(DataHistory.this, LoginScreen.class);
                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "no");
                        editor.apply();
                        startActivity(myIntent);
                        break;
                }
                return false;
            }
        });

    }

    public int getWorkingDaysBetweenTwoDates(Date startDate, Date endDate) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);

        int workDays = 0;

        //Return 0 if start and end are the same
        if (startCal.getTimeInMillis() == endCal.getTimeInMillis()) {
            return 0;
        }

        if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
            startCal.setTime(endDate);
            endCal.setTime(startDate);
        }

        do {
            if (startCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY && startCal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY) {
                ++workDays;
            }
            startCal.add(Calendar.DAY_OF_MONTH, 1);
        } while (startCal.getTimeInMillis() <= endCal.getTimeInMillis());

        return workDays;
    }

    private Date getFirstDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private Date getLastDateOfCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    private Date getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        return cal.getTime();
    }

    private User readUser() {
        mPrefs = getSharedPreferences("user", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("user", "");
        User user = gson.fromJson(json, User.class);
        return user;
    }


    private void insertUser(User user) {
        mPrefs = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        prefsEditor.putString("user", json);
        prefsEditor.commit();
    }
}