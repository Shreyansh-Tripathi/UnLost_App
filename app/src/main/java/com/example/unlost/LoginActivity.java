package com.example.unlost;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {


    EditText logemail, logpassword;
    TextView signuptxt;
    ImageView googlelogin;
    Button loginbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logemail= findViewById(R.id.logemail);
        logpassword= findViewById(R.id.logpassword);
        signuptxt= findViewById(R.id.signuptxt);
        googlelogin= findViewById(R.id.google_login);
        loginbtn= findViewById(R.id.logbtn);

        signuptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });



        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}