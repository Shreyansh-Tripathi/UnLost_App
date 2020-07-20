package com.example.unlost;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SignUpActivity extends AppCompatActivity {

    EditText sgfirstName, sglastName, sgEmail, sgPassword;
    Button signupbtn;
    ImageView googleSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        sgEmail=findViewById(R.id.sgemail);
        sgfirstName= findViewById(R.id.sgfirstname);
        sglastName= findViewById(R.id.sglastname);
        sgPassword= findViewById(R.id.sgpassword);

        signupbtn= findViewById(R.id.signupbtn);

        googleSignUp= findViewById(R.id.google_signup);
    }
}