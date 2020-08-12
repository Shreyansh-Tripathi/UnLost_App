package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unlost.R;
import com.example.unlost.notification.APIService;
import com.example.unlost.notification.Client;
import com.example.unlost.notification.Data;
import com.example.unlost.notification.NotificationSender;
import com.example.unlost.notification.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class ProductDescriptionActivity extends AppCompatActivity {
    EditText etVerification;
    Button verifyBtn;
    TextView tvItemCategory,tvItemBrand,tvContactName,tvContactNumber,tvlocation,textView14,tvVerify;
    ImageView ivLostImage;
    ImageButton exit_description;
    ProgressBar progress_bar;
    String id;
     ArrayList<HashMap> answers;
     private APIService apiService;
     String title,message,userIdTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        tvVerify=findViewById(R.id.tvVerify);
        textView14=findViewById(R.id.textView14);
        verifyBtn=findViewById(R.id.verifyBtn);
        etVerification=findViewById(R.id.etVerification);
        tvItemCategory=findViewById(R.id.tvItemCategory);
        tvItemBrand=findViewById(R.id.tvItemBrand);
        tvContactName=findViewById(R.id.tvContactName);
        tvContactNumber=findViewById(R.id.tvContactNumber);
        tvlocation=findViewById(R.id.tvlocation);
        ivLostImage=findViewById(R.id.ivLostImage);
        exit_description=findViewById(R.id.exit_description);
        progress_bar=findViewById(R.id.progress_bar);
        Intent intent=getIntent();
        id=intent.getStringExtra("document_id");
        assert id != null;
        final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String userId=user.getUid();
        apiService=Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        final DocumentReference dref= FirebaseFirestore.getInstance().collection("Lost Items").document(id);
        dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    answers = (ArrayList<HashMap>)(doc.get("answers"));
                    if (userId.equals(doc.get("user_id"))){
                        verifyBtn.setVisibility(View.GONE);
                        etVerification.setVisibility(View.GONE);
                        tvVerify.setVisibility(View.GONE);
                        textView14.setVisibility(View.VISIBLE);
                        tvContactName.setVisibility(View.VISIBLE);
                        tvContactNumber.setVisibility(View.VISIBLE);
                    }
                        showAnswers(answers, userId);

                    tvItemCategory.setText(Objects.requireNonNull(doc.get("item_category")).toString());
                    tvItemBrand.setText(Objects.requireNonNull(doc.get("item_brand")).toString());
                    tvContactName.setText(Objects.requireNonNull(doc.get("contact_name")).toString());
                    tvContactNumber.setText(Objects.requireNonNull(doc.get("contact_number")).toString());
                    tvlocation.setText(Objects.requireNonNull(doc.get("item_location")).toString());
                    Picasso.with(ProductDescriptionActivity.this).load(Objects.requireNonNull(doc.get("url")).toString()).fit().centerInside()
                            .into(ivLostImage);
                    progress_bar.setVisibility(View.GONE);
                }
            }
        });

        verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, ArrayList<HashMap>> items=new HashMap<>();
                HashMap<String, Object> answersMap=new HashMap<>();
                String ans=etVerification.getText().toString().trim();
                answersMap.put("user_id", user.getUid());
                answersMap.put("answer", ans);
                answersMap.put("username", user.getDisplayName());
                answersMap.put("verified", false);
                answers.add(answersMap);
                items.put("answers", answers);

                final DocumentReference documentReference= FirebaseFirestore.getInstance().collection("Lost Items").document(id);
                documentReference.set(items, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()){
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot snapshot= task.getResult();
                                            assert snapshot != null;
                                             title= snapshot.get("item_category").toString() +":"+ snapshot.get("item_brand").toString();
                                             message= "You have received a Reply on "+snapshot.get("item_brand").toString() +" "+ snapshot.get("item_category").toString();
                                            userIdTo=snapshot.get("user_id").toString();

                                            FirebaseDatabase.getInstance().getReference().child("Tokens").child(userIdTo).child("token").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                    String usertoken=dataSnapshot.getValue(String.class);
                                                    sendNotification(usertoken, title, message);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                });

                               Toast.makeText(ProductDescriptionActivity.this, "Data Sent To The Person who is having your object. Please Wait Till Verification is done!", Toast.LENGTH_LONG).show();
                               etVerification.setText("");
                           }
                           else
                               Toast.makeText(ProductDescriptionActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        exit_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public void showAnswers(ArrayList<HashMap> answers, String userId){
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).get("user_id").toString().equals(userId)) {
                if ((boolean) answers.get(i).get("verified")) {
                    verifyBtn.setVisibility(View.GONE);
                    etVerification.setVisibility(View.GONE);
                    tvVerify.setVisibility(View.GONE);
                    textView14.setVisibility(View.VISIBLE);
                    tvContactName.setVisibility(View.VISIBLE);
                    tvContactNumber.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }

    }

    public void sendNotification(String userToken, String title, String message){
        Data data=new Data(title, message);
        NotificationSender sender=new NotificationSender(data, userToken);
        apiService.sendNotification(sender).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code()==200){
                    if (response.body().success!=1){
                        Toast.makeText(ProductDescriptionActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {

            }
        });
    }
}