package com.example.unlost.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.example.unlost.R;

public class SplashScreenActivity extends AppCompatActivity {

    ProgressBar splashscreenProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        splashscreenProgressBar=findViewById(R.id.splashscreenprogress);
    }
}