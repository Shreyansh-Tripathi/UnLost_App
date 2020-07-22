package com.example.unlost.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.unlost.R;

public class ChooseActivity extends AppCompatActivity {

    CardView noteActivity,lostActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        noteActivity=findViewById(R.id.noteActivity);
        lostActivity=findViewById(R.id.lostActivity);
        noteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseActivity.this,AllNotesActivity.class));
            }
        });
        lostActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseActivity.this,Lost_and_Found_activity.class));
            }
        });
    }
}