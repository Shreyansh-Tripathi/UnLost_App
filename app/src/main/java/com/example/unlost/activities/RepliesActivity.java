package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.unlost.R;
import com.example.unlost.notification.APIService;
import com.example.unlost.notification.Client;
import com.example.unlost.notification.Data;
import com.example.unlost.notification.NotificationSender;
import com.example.unlost.notification.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class RepliesActivity extends AppCompatActivity implements ReplyAdapter.ItemClick {
    ImageButton back;
    String doc_id;
   ArrayList<Reply> replies=new ArrayList<>();
   ReplyAdapter adapter;
   RecyclerView repliesList;
   RecyclerView.LayoutManager layoutManager;
    ArrayList<HashMap> mapList;
    private APIService apiService;
    String message,title,userIdTo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replies);
        back=findViewById(R.id.back);
        repliesList=findViewById(R.id.repliesList);
        Intent intent=getIntent();
        doc_id=intent.getStringExtra("DOC_ID");
        layoutManager=new LinearLayoutManager(this);
        repliesList.setHasFixedSize(true);
        repliesList.setLayoutManager(layoutManager);
        apiService= Client.getClient("https://fcmgoogleapis.com/").create(APIService.class);

        DocumentReference documentReference= FirebaseFirestore.getInstance().collection("Lost Items").document(doc_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                     mapList= (ArrayList<HashMap>) doc.get("answers");

                    for (int i=0; i<mapList.size();i++)
                    {
                        HashMap<String, Object> map= mapList.get(i);
                        replies.add(new Reply(Objects.requireNonNull(map.get("username")).toString(), Objects.requireNonNull(map.get("answer")).toString()));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
         adapter=new ReplyAdapter(replies, this);
         repliesList.setAdapter(adapter);
    }

    @Override
    public void onClick(final int index) {
        final DocumentReference dref= FirebaseFirestore.getInstance().collection("Lost Items").document(doc_id);

        AlertDialog.Builder alertDialog= new AlertDialog.Builder(RepliesActivity.this);
        alertDialog.setTitle("Is That Enough?").setMessage("Please Make Sure that enough details are provided for Verification")
                .setPositiveButton("Yes, Verify!", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mapList.get(index).replace("verified",true);
                        dref.update("answers",mapList);
                        message="Your details have been verified. You can now find the contact details of that person in the Lost Section!";
                        dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot snapshot=task.getResult();
                                    assert snapshot != null;
                                    title= snapshot.get("item_category").toString()+":"+snapshot.get("item_brand").toString();
                                    userIdTo=mapList.get(index).get("user_id").toString();

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

                    }
                })

                .setNegativeButton("No, Delete!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                replies.remove(index);
                adapter.notifyItemRemoved(index);
                mapList.remove(index);
                dref.update("answers", mapList);
                message="Your answer doesn't match the required credentials, so your product request has been declined!";
                dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot=task.getResult();
                            assert snapshot != null;
                            title= snapshot.get("item_category").toString()+":"+snapshot.get("item_brand").toString();
                            userIdTo=mapList.get(index).get("user_id").toString();

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
            }
        }).show();
    }

    public void sendNotification(String userToken, String title, String message){
        Data data=new Data(title, message);
        NotificationSender sender=new NotificationSender(data, userToken);
        apiService.sendNotification(sender).enqueue(new Callback<Response>() {
            @Override
            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                if (response.code()==200){
                    if (response.body().success!=1){
                        Toast.makeText(RepliesActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Response> call, Throwable t) {
            }
        });
    }

    public void onbackpressed(View v){
        onBackPressed();
    }
}