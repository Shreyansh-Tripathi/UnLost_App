package com.example.unlost.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.unlost.R;
import com.example.unlost.notification.Token;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

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
        updateToken();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChooseActivity.this);
                alertDialog.setTitle("Are You Sure?").setMessage("Do You Want to Log out?").setIcon(R.drawable.warning)
                        .setPositiveButton("Yes, Logout!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(ChooseActivity.this, LoginActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
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

    public void updateToken(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Token token =new Token(refreshToken);
        assert user != null;
        FirebaseDatabase.getInstance().getReference("Tokens").child(user.getUid()).setValue(token);
    }
}