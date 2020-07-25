package com.example.unlost.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.unlost.R;

public class ShowImage extends AppCompatActivity {
    ImageView imgShow;
    ImageButton imgBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        imgShow=findViewById(R.id.imgShow1);
        imgBackBtn=findViewById(R.id.imgBackBtn);
        final Intent intent=getIntent();
        String imagePath=intent.getStringExtra("imagePath");
        imgShow.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        imgBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }
}