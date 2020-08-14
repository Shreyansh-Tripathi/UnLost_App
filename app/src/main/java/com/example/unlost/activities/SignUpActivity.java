package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.unlost.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    EditText sgfirstName, sglastName, sgEmail, sgPassword;
    Button signupbtn;
    ImageView googleSignUp;
    private FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth=FirebaseAuth.getInstance();

        sgfirstName= findViewById(R.id.sgfirstname);
        sglastName= findViewById(R.id.sglastname);
        sgPassword= findViewById(R.id.sgpassword);
        sgEmail=findViewById(R.id.sgemail);
        signupbtn= findViewById(R.id.signupbtn);
        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=sgEmail.getText().toString();
                String password=sgPassword.getText().toString();
                String fName=sgfirstName.getText().toString();
                String lName=sglastName.getText().toString();
                if(TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(fName))
                {
                    Toast.makeText(SignUpActivity.this, "Please enter name,email and password!", Toast.LENGTH_SHORT).show();
                }
                else if(password.length()<=6)
                {
                    Toast.makeText(SignUpActivity.this, "Password length must be greater than 6!", Toast.LENGTH_SHORT).show();
                }
                else {
                    registerUser(email, password,fName,lName);
                }
            }
        });
        googleSignUp= findViewById(R.id.google_signup);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account.getIdToken());
            }
            catch (ApiException e) {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            Toast.makeText(SignUpActivity.this, "User Registered Successfully. You're Logged In!", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        }
                        else
                        {
                            Toast.makeText(SignUpActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                            finish();
                        }
                    }
                });
    }

    private void updateUI(Object o) {
        if (o != null) {
            startActivity(new Intent(SignUpActivity.this, ChooseActivity.class));
        }
    }

    private void registerUser(String email, String password, final String fName, final String lName) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(SignUpActivity.this, "User Registered Successfully!", Toast.LENGTH_SHORT).show();

                    FirebaseUser user = auth.getCurrentUser();

                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fName+" "+lName).build();

                    assert user != null;
                    user.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

                                        startActivity(new Intent(SignUpActivity.this,ChooseActivity.class));
                                        finish();
                                    }
                                }
                            });

                }
                else
                {
                    Toast.makeText(SignUpActivity.this, "User Cannot Be Registered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}