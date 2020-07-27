package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.unlost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Lost_and_Found_activity extends AppCompatActivity {
    EditText item_category,item_brand,item_brief,contact_details;
    Button save_item;
    LinearLayout add_image;
    TextView found_title,lost_title;
    View found_line, lost_line;
    Uri image_uri;
    String url;
    StorageReference mStorageRef;
    FirebaseUser user;
    FirebaseFirestore db;
    StorageTask mUploadTask;

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
        found_line=findViewById(R.id.found_line);
        lost_line=findViewById(R.id.lost_line);
        add_image=findViewById(R.id.add_image);

        db=FirebaseFirestore.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        foundFragment();

        found_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                found_line.setVisibility(View.VISIBLE);
                lost_line.setVisibility(View.INVISIBLE);
                foundFragment();
            }
        });
        lost_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lost_line.setVisibility(View.VISIBLE);
                found_line.setVisibility(View.INVISIBLE);

            }
        });
        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUploadTask!=null){
                    Toast.makeText(Lost_and_Found_activity.this, "Upload Already in Progress!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    openCamera();
                    uploadImage();
                }
            }
        });
    }

    private void foundFragment() {

        FragmentManager manager=this.getSupportFragmentManager();
        manager.beginTransaction().hide(Objects.requireNonNull(manager.findFragmentById(R.id.lost_frag)))
                .show(Objects.requireNonNull(manager.findFragmentById(R.id.found_frag))).commit();

          final Map<String, Object> item= new HashMap<>();

        save_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String category=item_category.getText().toString();
                String brand=item_brand.getText().toString();
                String brief=item_brief.getText().toString();
                String contact=contact_details.getText().toString();

                if(TextUtils.isEmpty(category)||TextUtils.isEmpty(brief)||TextUtils.isEmpty(contact)||TextUtils.isEmpty(url))
                {
                    Toast.makeText(Lost_and_Found_activity.this, "Please enter all fields marked with *", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    item.put("item_category", category);
                    item.put("item_brand",brand);
                    item.put("item_brief",brief);
                    item.put("contact_details",contact);
                    item.put("url", url);
                    assert user != null;
                    item.put("Name",user.getDisplayName());
                    db.collection("Lost Items")
                            .document(category).collection(user.getUid()).add(item);
                }
            }
        });
    }

    private void uploadImage() {
        if (image_uri==null){
            Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
        }
        else {
            final StorageReference reference= mStorageRef.child(user.getUid()+"."+getFileExtension(image_uri));
            mUploadTask= reference.putFile(image_uri)
                   .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   url=uri.toString();
                                   Toast.makeText(Lost_and_Found_activity.this, "Successful", Toast.LENGTH_SHORT).show();                             }
                           });
                       }
                   })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Lost_and_Found_activity.this, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(Lost_and_Found_activity.this, "Uploading", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void openCamera() {
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}