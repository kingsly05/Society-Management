package com.sonawane_ad.societymanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    String TAG="SplashActivity";
    private LottieAnimationView splash;
    private Intent intent = new Intent();
    private TimerTask timer;
    private SharedPreferences file;
    private Timer _timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        file = getSharedPreferences("file", Activity.MODE_PRIVATE);
        setContentView(R.layout.activity_splash);
        com.google.firebase.FirebaseApp.initializeApp(this);
        initialize();
        initializeLogic();
    }

    private void initialize()
    {
        splash = findViewById(R.id.UI_Splash);
    }
    private void initializeLogic()
    {
        timer = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!file.getString("Introflag", "").equals("1"))
                        {
                            intent.setClass(getApplicationContext(), IntroActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        if (file.getString("Introflag", "").equals("1"))
                        {
                            intent.setClass(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }

                    }
                });
            }
        };
        _timer.schedule(timer, (int)(3700));
    }


}