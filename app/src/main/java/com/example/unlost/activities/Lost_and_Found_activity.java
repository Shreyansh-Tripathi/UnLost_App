package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;
import java.util.Objects;

public class Lost_and_Found_activity extends AppCompatActivity implements Lost_adapter.ItemClicked {
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_CAPTURE_IMAGE =2 ;
    EditText item_category,item_brand,item_brief,contact_details,search_product,item_location;
    Button save_item;
    ImageButton back_btn;
    LinearLayout add_image;
    TextView found_title,lost_title;
    View found_line, lost_line;
    Uri image_uri;
    String url;
    ArrayList<String> id=new ArrayList<>();
    StorageReference mStorageRef;
    FirebaseUser user;
    FirebaseFirestore db;
    StorageTask mUploadTask;
    ArrayList<Product> productsList;
    RecyclerView recyclerView;
    Lost_adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ProgressBar image_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lost_and__found_activity);
        item_category=findViewById(R.id.item_category);
        item_brand=findViewById(R.id.item_brand);
        item_brief=findViewById(R.id.item_brief);
        item_location=findViewById(R.id.item_location);
        contact_details=findViewById(R.id.contact_details);
        search_product=findViewById(R.id.search_product);
        save_item=findViewById(R.id.save_item);
        found_title=findViewById(R.id.found_title);
        lost_title=findViewById(R.id.lost_title);
        found_line=findViewById(R.id.found_line);
        lost_line=findViewById(R.id.lost_line);
        add_image=findViewById(R.id.add_image);
        image_progress=findViewById(R.id.image_progress);
        back_btn=findViewById(R.id.back_btn);
        recyclerView=findViewById(R.id.recLostItems);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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
                lostFragment();
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
                }
            }
        });

        search_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                   filterItems(s.toString());
            }
        });
    }

    private void filterItems(String search){
        ArrayList<Product> searchList= new ArrayList<>();

        for (Product item : productsList){
            if (item.getCategory().toLowerCase().contains(search.toLowerCase()) ||
                    item.getSubcategory().toLowerCase().contains(search.toLowerCase()))
            {
                searchList.add(item);
            }
        }
        adapter.filterList(searchList);
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
                String location=item_location.getText().toString().trim();
                String contact=contact_details.getText().toString();

                if(TextUtils.isEmpty(category)||TextUtils.isEmpty(brief)||TextUtils.isEmpty(contact)||TextUtils.isEmpty(location))
                {
                    Toast.makeText(Lost_and_Found_activity.this, "Please enter all fields marked with *", Toast.LENGTH_SHORT).show();
                }
                else if (url==null){
                    Toast.makeText(Lost_and_Found_activity.this, "Please Add Image of the Item", Toast.LENGTH_SHORT).show();
                }

                else
                {
                    item.put("item_category", category);
                    item.put("item_brand",brand);
                    item.put("item_brief",brief);
                    item.put("item_location",location);
                    item.put("contact_number",contact);
                    item.put("url", url);
                    assert user != null;
                    item.put("contact_name",user.getDisplayName());
                    db.collection("Lost Items").add(item);
                    Toast.makeText(Lost_and_Found_activity.this, "Item Registered!", Toast.LENGTH_SHORT).show();
                    item_brand.setText(null);
                    item_brief.setText(null);
                    item_category.setText(null);
                    contact_details.setText(null);
                    image_uri=null;
                    url=null;

                }
            }
        });
    }

    private void lostFragment(){
        FragmentManager manager=this.getSupportFragmentManager();
        manager.beginTransaction().hide(Objects.requireNonNull(manager.findFragmentById(R.id.found_frag)))
                .show(Objects.requireNonNull(manager.findFragmentById(R.id.lost_frag))).commit();

        productsList=new ArrayList<>();
        adapter=new Lost_adapter(this, productsList);
        layoutManager=new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        Query dref=FirebaseFirestore.getInstance().collection("Lost Items");
        dref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult()))
                    {
                        Product product=new Product(snapshot.get("item_category").toString(),
                                Objects.requireNonNull(snapshot.get("item_brand")).toString(),snapshot.get("url").toString());
                        productsList.add(product);
                        id.add(snapshot.getId());
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(Lost_and_Found_activity.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage() {
        if (image_uri==null){
            Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
        }
        else {
            final StorageReference reference= mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(image_uri));
            mUploadTask= reference.putFile(image_uri)
                   .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                   url=uri.toString();
                                   image_progress.setVisibility(View.GONE);
                                   Toast.makeText(Lost_and_Found_activity.this, "Successful", Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                   })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Lost_and_Found_activity.this, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        image_progress.setVisibility(View.GONE);
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        image_progress.setVisibility(View.VISIBLE);
                    }
                });
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Lost_and_Found_activity.this,
                    new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        } else {
            cameraIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_CAMERA && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                cameraIntent();
            }
            else
            {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cameraIntent() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_CAPTURE_IMAGE && resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                image_uri=getImageUri(this,capturedImage);
                uploadImage();
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    @Override
    public void onItemClicked(int index) {
        Intent intent=new Intent(Lost_and_Found_activity.this,ProductDescriptionActivity.class);
        intent.putExtra("document_id",id.get(index));
        startActivity(intent);
    }
}