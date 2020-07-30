package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unlost.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MyUploads extends AppCompatActivity implements Lost_adapter.ItemClicked{

    ArrayList<Product> productsList;
    RecyclerView recyclerView;
    EditText etSearch;
    Lost_adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<String> id=new ArrayList<>();

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

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        Query dref= FirebaseFirestore.getInstance().collection("Lost Items")
                .whereEqualTo("user_id",user.getUid());
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
                    Toast.makeText(MyUploads.this, "Error!", Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onItemClicked(int index) {
        Intent intent=new Intent(MyUploads.this,ProductDescriptionActivity.class);
        intent.putExtra("document_id",id.get(index));
        startActivity(intent);
    }
}