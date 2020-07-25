package com.example.unlost.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.unlost.R;
import com.google.firebase.auth.FirebaseAuth;

public class ChooseActivity extends AppCompatActivity {

    CardView noteActivity,lostActivity;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        logout=findViewById(R.id.logout_btn);
        noteActivity=findViewById(R.id.noteActivity);
        lostActivity=findViewById(R.id.lostActivity);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChooseActivity.this, LoginActivity.class));
                finish();
            }
        });

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