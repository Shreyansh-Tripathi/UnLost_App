package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.unlost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyUploads extends AppCompatActivity implements Lost_adapter.ItemClicked, Lost_adapter.LongItemClicked, PopupMenu.OnMenuItemClickListener {

    ArrayList<Product> productsList;
    RecyclerView recyclerView;
    EditText etSearch;
    Lost_adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<String> id=new ArrayList<>();
    int position=-1;
    final static int RQ_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_uploads);
        recyclerView=findViewById(R.id.recMyUploads);
        etSearch=findViewById(R.id.etSearch);
        productsList=new ArrayList<>();
        adapter=new Lost_adapter(this, productsList);
        layoutManager=new LinearLayoutManager(this);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                filterItems(s.toString());
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        showAllProducts();
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

    @Override
    public void onItemClicked(int index) {
        Intent intent=new Intent(MyUploads.this,ProductDescriptionActivity.class);
        intent.putExtra("document_id",id.get(index));
        startActivity(intent);
    }

    @Override
    public void onLongItemClicked(int index, View view) {
        PopupMenu popupMenu=new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
        position=index;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.deleteproduct:
                AlertDialog.Builder deleteDialog= new AlertDialog.Builder(this);
                deleteDialog.setTitle("Delete Item!").setMessage("Are You Sure?").setIcon(R.drawable.delete)
                        .setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DocumentReference documentReference= FirebaseFirestore.getInstance().collection("Lost Items")
                                        .document(id.get(position));
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MyUploads.this, "Deleted", Toast.LENGTH_SHORT).show();
                                        id.remove(position);
                                        productsList.remove(position);
                                        adapter.notifyItemRemoved(position);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MyUploads.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("No", null).show();
                return true;

            case R.id.editproduct:
                Intent intent=new Intent(MyUploads.this, EditProductActivity.class);
                intent.putExtra("product_id",id.get(position));
                startActivityForResult(intent, RQ_CODE);
                return true;

            default:
            return false;
        }
    }

    private void showAllProducts(){

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        Query dref= FirebaseFirestore.getInstance().collection("Lost Items")
                .whereEqualTo("user_id",user.getUid());
        dref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot snapshot: Objects.requireNonNull(task.getResult()))
                    {
                        Product product=new Product(Objects.requireNonNull(snapshot.get("item_category")).toString(),
                                Objects.requireNonNull(snapshot.get("item_brand")).toString(), Objects.requireNonNull(snapshot.get("url")).toString());
                        productsList.add(product);
                        id.add(snapshot.getId());
                    }
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(MyUploads.this, "Error!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RQ_CODE && resultCode==RESULT_OK){
            adapter.notifyItemChanged(position);
            showAllProducts();
        }
    }
}