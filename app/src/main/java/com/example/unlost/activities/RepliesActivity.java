package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.unlost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class RepliesActivity extends AppCompatActivity {
    ImageButton back;
    String doc_id;
   ArrayList<Reply> replies=new ArrayList<>();
   ReplyAdapter adapter;
   RecyclerView repliesList;
   RecyclerView.LayoutManager layoutManager;

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

        DocumentReference documentReference= FirebaseFirestore.getInstance().collection("Lost Items").document(doc_id);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    assert doc != null;
                    ArrayList<HashMap> mapList= (ArrayList<HashMap>) doc.get("answers");

                    for (int i=0; i<mapList.size();i++)
                    {
                        HashMap<String, String> map= mapList.get(i);
                        replies.add(new Reply(map.get("username"), map.get("answer")));
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
         adapter=new ReplyAdapter(replies, this);
         repliesList.setAdapter(adapter);
    }

    public void onbackpressed(View v){
        onBackPressed();
    }
}