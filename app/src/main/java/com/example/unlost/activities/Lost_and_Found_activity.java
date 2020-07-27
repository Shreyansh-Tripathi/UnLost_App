package com.example.unlost.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unlost.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Lost_and_Found_activity extends AppCompatActivity {
    EditText item_category,item_brand,item_brief,contact_details;
    Button save_item;
    TextView found_title,lost_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and__found_activity);
        item_category=findViewById(R.id.item_category);
        item_brand=findViewById(R.id.item_brand);
        item_brief=findViewById(R.id.item_brief);
        contact_details=findViewById(R.id.contact_details);
        save_item=findViewById(R.id.save_item);
        found_title=findViewById(R.id.found_title);
        lost_title=findViewById(R.id.lost_title);

        foundFragment();
        found_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foundFragment();
            }
        });


    }

    private void foundFragment() {
        FragmentManager manager=this.getSupportFragmentManager();
        manager.beginTransaction().hide(manager.findFragmentById(R.id.lost_frag))
                .show(manager.findFragmentById(R.id.found_frag)).commit();
        final FirebaseFirestore db=FirebaseFirestore.getInstance();
        final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        final Map<String,Object> item=new HashMap<>();
        save_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category=item_category.getText().toString();
                String brand=item_brand.getText().toString();
                String brief=item_brief.getText().toString();
                String contact=contact_details.getText().toString();
                if(TextUtils.isEmpty(category)||TextUtils.isEmpty(brief)||TextUtils.isEmpty(brief))
                {
                    Toast.makeText(Lost_and_Found_activity.this, "Please enter all fields marked with asterisk!!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    item.put("item_brand",brand);
                    item.put("item_brief",brief);
                    item.put("contact_details",contact);
                    item.put("Name",user.getDisplayName());
                    db.collection("Lost Items")
                            .document(category).set(item);
                }

            }
        });
    }
}