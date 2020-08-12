package com.example.unlost.notification;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        if (user!=null){
            updateToken(refreshToken);
        }
    }
    public void updateToken(String refreshToken){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        Token token=new Token(refreshToken);
        assert user != null;
        FirebaseDatabase.getInstance().getReference("Tokens").child(user.getUid()).child("token").setValue(token);
    }
}
