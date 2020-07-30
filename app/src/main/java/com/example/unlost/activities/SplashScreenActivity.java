package com.example.unlost.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.unlost.R;

public class SplashScreenActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    ProgressBar splashscreenProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splashscreenProgressBar=findViewById(R.id.splashscreenprogress);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this,LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}