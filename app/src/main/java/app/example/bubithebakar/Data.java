package app.example.bubithebakar;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import app.example.bubithebakar.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.skydoves.progressview.ProgressView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Data extends AppCompatActivity {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private final DatabaseReference ref = database.getReference();
    private final SimpleDateFormat currentDate = new SimpleDateFormat("d-M-yyyy");
    private final Date todayDate = new Date();
    private final String thisDate = currentDate.format(todayDate);
    private BottomNavigationView btnNav;
    private User user;
    private Dialog d;
    private TextView tvMisparTikufim, tvMisparKnasot, tvMisparBakarot, tvDate, tvWelcome;
    private EditText etMisparBakarot, etMisparTikufim, etMisparKnasot;
    private Button btnAddData, btnAccept, btnAchived, btnCancel;
    private ProgressView progressBakarot, progressTikufim, progressKnasot;
    private Intent intent;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_data);

        tvMisparTikufim = findViewById(R.id.tvMisparTikufim);
        tvMisparKnasot = findViewById(R.id.tvMisparKnasot);
        tvMisparBakarot = findViewById(R.id.tvMisparBakarot);
        btnAchived = findViewById(R.id.btnAchived);
        btnAddData = findViewById(R.id.btnAddData);
        progressBakarot = findViewById(R.id.progressBakarot);
        progressTikufim = findViewById(R.id.progressTikufim);
        progressKnasot = findViewById(R.id.progressKnasot);
        tvWelcome = findViewById(R.id.tvWelcome);
        tvDate = findViewById(R.id.tvDate);
        btnNav = findViewById(R.id.btnNav);

        user = readUser();

        progressSet();
        progressUpdate();
        tvDate.setText(thisDate.replace("-","/"));
        tvWelcome.setText("שלום " + user.getUserName());

        GlobalFunctions globalFunction = new GlobalFunctions(this);
        globalFunction.checkWifi();
        globalFunction.hideSystemUI();

        btnAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDataDialogCreate();
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isEmpty(etMisparKnasot) || (isEmpty(etMisparTikufim) || isEmpty(etMisparBakarot))) {
                            showToastMessage("חסרים לך נתונים", 1250);
                        } else {
                            d.cancel();
                            warningDialogCreate();
                            btnAccept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    user.setMonthlyTikufimGoal(Integer.parseInt(etMisparTikufim.getText().toString()));
                                    user.setMonthlyBakarotGoal(Integer.parseInt(etMisparBakarot.getText().toString()));
                                    user.setMonthlyKnasotGoal(Integer.parseInt(etMisparKnasot.getText().toString()));
                                    user.monthReset();
                                    d.cancel();
                                    progressSet();
                                    progressUpdate();
                                    insertUser(user);
                                    ref.child("Users").child(user.getUserName()).setValue(user);
                                }
                            });
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    d.cancel();
                                }
                            });
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.cancel();
                    }
                });
            }
        });
        btnAchived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvMisparKnasot.getText().equals("") || (tvMisparBakarot.getText().equals("") || (tvMisparTikufim.getText().equals("")))) {
                    showToastMessage("אין לך נתונים חודשיים", 1250);
                    return;
                }
                addDataDialogCreate();
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isEmpty(etMisparKnasot) || (isEmpty(etMisparTikufim) || isEmpty(etMisparBakarot))) {
                            showToastMessage("חסרים לך נתונים", 1250);
                        } else {
                            DayData today = new DayData(Integer.parseInt(etMisparTikufim.getText().toString()), Integer.parseInt(etMisparKnasot.getText().toString()), Integer.parseInt(etMisparBakarot.getText().toString()));
                            user.addMonthlyBakarot(Integer.parseInt(etMisparBakarot.getText().toString()));
                            user.addMonthlyTikufim(Integer.parseInt(etMisparTikufim.getText().toString()));
                            user.addMonthlyKnasot(Integer.parseInt(etMisparKnasot.getText().toString()));
                            d.cancel();
                            user.updateHistory(thisDate,today);
                            user.setLastUpdatedDate(thisDate);
                            progressSet();
                            progressUpdate();
                            insertUser(user);
                            ref.child("Users").child(user.getUserName()).setValue(user);
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.cancel();
                    }
                });
            }
        });
        btnNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_dayData:
                        intent = new Intent(Data.this, DataHistory.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_leaderboard:
                        intent = new Intent(Data.this, LeaderBoard.class);
                        startActivity(intent);
                        break;
                    case R.id.nav_logout:
                        Intent myIntent = new Intent(Data.this, LoginScreen.class);
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

    public void addDataDialogCreate() {
        d = new Dialog(Data.this);
        d.setContentView(R.layout.d_add_data);
        d.show();
        etMisparBakarot = d.findViewById(R.id.etMisparBakarot);
        etMisparTikufim = d.findViewById(R.id.etMisparTikufim);
        etMisparKnasot = d.findViewById(R.id.etMisparKnasot);
        etMisparBakarot.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMisparTikufim.setInputType(InputType.TYPE_CLASS_NUMBER);
        etMisparKnasot.setInputType(InputType.TYPE_CLASS_NUMBER);
        btnAccept = d.findViewById(R.id.btnAccept);
        btnCancel = d.findViewById(R.id.btnCancel);
    }

    public void warningDialogCreate() {
        d = new Dialog(Data.this);
        d.setContentView(R.layout.d_warning);
        d.show();
        btnAccept = d.findViewById(R.id.btnAccept);
        btnCancel = d.findViewById(R.id.btnCancel);
    }

    public void showToastMessage(String text, int duration) {
        final Toast toast = Toast.makeText(Data.this, text, Toast.LENGTH_SHORT);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();
            }
        }, duration);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private void progressUpdate() {
        progressKnasot.setProgress((float) user.getMonthlyKnasot() / user.getMonthlyKnasotGoal() * 100);
        progressTikufim.setProgress((float) user.getMonthlyTikufim() / user.getMonthlyTikufimGoal() * 100);
        progressBakarot.setProgress((float) user.getMonthlyBakarot() / user.getMonthlyBakarotGoal() * 100);
        progressKnasot.setLabelText(String.format(Locale.US, "%.0f", (float) user.getMonthlyKnasot() / user.getMonthlyKnasotGoal() * 100) + "%");
        progressTikufim.setLabelText(String.format(Locale.US, "%.0f", (float) user.getMonthlyTikufim() / user.getMonthlyTikufimGoal() * 100) + "%");
        progressBakarot.setLabelText(String.format(Locale.US, "%.0f", (float) user.getMonthlyBakarot() / user.getMonthlyBakarotGoal() * 100) + "%");
    }

    @SuppressLint("SetTextI18n")
    private void progressSet() {
        if (user.getMonthlyBakarotGoal() - user.getMonthlyBakarot() > 0)
            tvMisparBakarot.setText(user.getMonthlyBakarot() + "/" + user.getMonthlyBakarotGoal() + " נותרו: " + (user.getMonthlyBakarotGoal() - user.getMonthlyBakarot()));
        else
            tvMisparBakarot.setText(user.getMonthlyBakarot() + "/" + user.getMonthlyBakarotGoal() + " נותרו: " + 0);
        if (user.getMonthlyTikufimGoal() - user.getMonthlyTikufim() > 0)
            tvMisparTikufim.setText(user.getMonthlyTikufim() + "/" + user.getMonthlyTikufimGoal() + " נותרו: " + (user.getMonthlyTikufimGoal() - user.getMonthlyTikufim()));
        else
            tvMisparTikufim.setText(user.getMonthlyTikufim() + "/" + user.getMonthlyTikufimGoal() + " נותרו: " + 0);
        if (user.getMonthlyKnasotGoal() - user.getMonthlyKnasot() > 0)
            tvMisparKnasot.setText(user.getMonthlyKnasot() + "/" + user.getMonthlyKnasotGoal() + " נותרו: " + (user.getMonthlyKnasotGoal() - user.getMonthlyKnasot()));
        else
            tvMisparKnasot.setText(user.getMonthlyKnasot() + "/" + user.getMonthlyKnasotGoal() + " נותרו: " + 0);
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