package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.unlost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProductActivity extends AppCompatActivity {
       ImageButton back_btn_productedit;
       Button update_item;
       EditText edit_category, edit_brand, edit_brief, edit_location, edit_contact;
       String url, doc_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        update_item=findViewById(R.id.update_item);
        edit_brand=findViewById(R.id.edit_brand);
        edit_brief=findViewById(R.id.edit_brief);
        edit_category=findViewById(R.id.edit_category);
        edit_contact=findViewById(R.id.edit_contact);
        edit_location=findViewById(R.id.edit_location);
        back_btn_productedit = findViewById(R.id.back_btn_productedit);

        final Intent intent= getIntent();
        doc_id=intent.getStringExtra("product_id");

        final DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Lost Items")
                             .document(doc_id);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc= task.getResult();
                    assert doc != null;
                    edit_category.setText(Objects.requireNonNull(doc.get("item_category")).toString());
                    edit_brand.setText(Objects.requireNonNull(doc.get("item_brand")).toString());
                    edit_brief.setText(Objects.requireNonNull(doc.get("item_brief")).toString());
                    edit_location.setText(Objects.requireNonNull(doc.get("item_location")).toString());
                    edit_contact.setText(Objects.requireNonNull(doc.get("contact_number")).toString());
                    url=Objects.requireNonNull(doc.get("url")).toString();
                }
                else {
                    Toast.makeText(EditProductActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        update_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String category=edit_category.getText().toString();
                final String brand=edit_brand.getText().toString();
                final String brief=edit_brief.getText().toString();
                final String location=edit_location.getText().toString().trim();
                final String contact=edit_contact.getText().toString();

                if(TextUtils.isEmpty(category)||TextUtils.isEmpty(brief)||TextUtils.isEmpty(contact)||TextUtils.isEmpty(location))
                {
                    Toast.makeText(EditProductActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                    final Map<String, Object> item= new HashMap<>();
                    assert user != null;
                    item.put("user_id",user.getUid());
                    item.put("item_category", category);
                    item.put("item_brand",brand);
                    item.put("item_brief",brief);
                    item.put("item_location",location);
                    item.put("contact_number",contact);
                    item.put("contact_name",user.getDisplayName());
                    item.put("url", url);
                    documentReference.update(item);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        back_btn_productedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    setResult(RESULT_CANCELED);
                }
        });
    }
}