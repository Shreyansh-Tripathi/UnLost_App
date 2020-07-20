package com.example.unlost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {


    EditText logemail, logpassword;
    TextView signuptxt;
    ImageView googlelogin;
    Button loginbtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logemail= findViewById(R.id.logemail);
        logpassword= findViewById(R.id.logpassword);
        signuptxt= findViewById(R.id.signuptxt);
        googlelogin= findViewById(R.id.google_login);
        loginbtn= findViewById(R.id.logbtn);

        mAuth = FirebaseAuth.getInstance();

        signuptxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uemail= logemail.getText().toString().trim();
                String upassword= logpassword.getText().toString().trim();

                if (TextUtils.isEmpty(uemail) || TextUtils.isEmpty(upassword)){
                    Toast.makeText(LoginActivity.this, "Empty Crediential(s)", Toast.LENGTH_SHORT).show();
                }

                else{
                    loginUser(uemail, upassword);
                }
            }

            private void loginUser(String email,String password) {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            startActivity(new Intent(LoginActivity.this, ChooseActivity.class));
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            this.finish();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Error Logging You Up", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}