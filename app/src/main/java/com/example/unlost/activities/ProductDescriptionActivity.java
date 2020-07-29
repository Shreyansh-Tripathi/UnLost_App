package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.unlost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ProductDescriptionActivity extends AppCompatActivity {
    TextView tvItemCategory,tvItemBrand,tvItemBrief,tvContactName,tvContactNumber,tvlocation;
    ImageView ivLostImage;
    ImageButton exit_description;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        tvItemCategory=findViewById(R.id.tvItemCategory);
        tvItemBrand=findViewById(R.id.tvItemBrand);
        tvItemBrief=findViewById(R.id.tvItemBrief);
        tvContactName=findViewById(R.id.tvContactName);
        tvContactNumber=findViewById(R.id.tvContactNumber);
        tvlocation=findViewById(R.id.tvlocation);
        ivLostImage=findViewById(R.id.ivLostImage);
        exit_description=findViewById(R.id.exit_description);
        progress_bar=findViewById(R.id.progress_bar);

        Intent intent=getIntent();
        String id=intent.getStringExtra("document_id");
        assert id != null;
        DocumentReference dref= FirebaseFirestore.getInstance().collection("Lost Items").document(id);
        dref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot doc=task.getResult();
                    tvItemCategory.setText(doc.get("item_category").toString());
                    tvItemBrand.setText(doc.get("item_brand").toString());
                    tvItemBrief.setText(doc.get("item_brief").toString());
                    tvContactName.setText(doc.get("contact_name").toString());
                    tvContactNumber.setText(doc.get("contact_number").toString());
                    tvlocation.setText(doc.get("item_location").toString());
                    Picasso.with(ProductDescriptionActivity.this).load(doc.get("url").toString()).fit().centerInside()
                            .into(ivLostImage);
                        progress_bar.setVisibility(View.GONE);
                }
            }
        });

        exit_description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}