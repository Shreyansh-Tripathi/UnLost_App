package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unlost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProductDescriptionActivity extends AppCompatActivity {
    TextView tvItemCategory,tvItemBrand,tvItemBrief,tvContactName,tvContactNumber;
    ImageView ivLostImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        tvItemCategory=findViewById(R.id.tvItemCategory);
        tvItemBrand=findViewById(R.id.tvItemBrand);
        tvItemBrief=findViewById(R.id.tvItemBrief);
        tvContactName=findViewById(R.id.tvContactName);
        tvContactNumber=findViewById(R.id.tvContactNumber);
        ivLostImage=findViewById(R.id.ivLostImage);
        Intent intent=getIntent();
        String id=intent.getStringExtra("document_id");
        DocumentReference dref= FirebaseFirestore.getInstance().collection("Lost Items").document(id);
        dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc=task.getResult();
                    tvItemCategory.setText(doc.get("item_category").toString());
                    tvItemBrand.setText(doc.get("item_brand").toString());
                    tvItemBrief.setText(doc.get("item_brief").toString());
                    tvContactName.setText(doc.get("contact_name").toString());
                    tvContactNumber.setText(doc.get("contact_number").toString());
                    Picasso.with((Context) ProductDescriptionActivity.this).load(doc.get("url").toString()).fit().centerInside()
                            .into(ivLostImage);
                }

            }
        });
    }
}