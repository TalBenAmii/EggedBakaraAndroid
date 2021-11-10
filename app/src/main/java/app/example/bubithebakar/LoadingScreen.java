package app.example.bubithebakar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

import app.example.bubithebakar.R;

public class LoadingScreen extends AppCompatActivity {

    private TextView tvError;
    private Button btnTryAgain;
    private ProgressBar progressBar;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lo_loading_screen);

        tvError = findViewById(R.id.tvError);
        btnTryAgain = findViewById(R.id.btnTryAgain);
        progressBar = findViewById(R.id.progressBar);

        GlobalFunctions globalFunction = new GlobalFunctions(this);
        checkWifi();
        globalFunction.hideSystemUI();
    }

    public void checkWifi() {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressBar.setAlpha(1);
                tvError.setAlpha(0);
                btnTryAgain.setAlpha(0);
                ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                        mobile != null && mobile.isConnectedOrConnecting();
                if (isConnected) {
                    preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    String checkbox = preferences.getString("remember", "");
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (checkbox.equals("yes")) {
                                Intent mainIntent = new Intent(LoadingScreen.this, Data.class);
                                startActivity(mainIntent);
                                finish();
                            } else {
                                Intent mainIntent = new Intent(LoadingScreen.this, LoginScreen.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        }
                    }, 1000);

                } else {
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setAlpha(0);
                            tvError.setAlpha(1);
                            btnTryAgain.setAlpha(1);
                            btnTryAgain.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    checkWifi();
                                }
                            });
                        }
                    }, 1000);
                }
            }
        };
        this.registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
}