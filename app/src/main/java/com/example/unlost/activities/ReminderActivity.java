package com.example.unlost.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.unlost.R;

public class ReminderActivity extends Activity {

    EditText ethours, etmins, etsecs;
    Button startTimerbtn;
    public static final String totaltime="total_seconds";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        ethours=findViewById(R.id.ethours);
        etmins=findViewById(R.id.etmins);
        etsecs=findViewById(R.id.etsecs);
        startTimerbtn=findViewById(R.id.startTimerbtn);

        DisplayMetrics displayMetrics= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width= displayMetrics.widthPixels;
        int height= displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*0.7), (int)(height*0.4));

        WindowManager.LayoutParams params= getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=0;

        getWindow().setAttributes(params);;

        startTimerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (ethours.getText().toString().trim().isEmpty() && etmins.getText().toString().trim().isEmpty()
                        && etsecs.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(ReminderActivity.this, "No Values found!", Toast.LENGTH_SHORT).show();
                }

                else {

                   try {
                       if (TextUtils.isEmpty(ethours.getText().toString()))
                           ethours.setText("0");

                       if (TextUtils.isEmpty(etmins.getText().toString()))
                           etmins.setText("0");

                       if (TextUtils.isEmpty(etsecs.getText().toString()))
                           etsecs.setText("0");

                       int hours = Integer.parseInt(ethours.getText().toString());
                       int mins = Integer.parseInt(etmins.getText().toString());
                       int secs = Integer.parseInt(etsecs.getText().toString());

                       int total= secs + (mins*60) + (hours*60*60);
                       final InputMethodManager iManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                       assert iManager != null;
                       iManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

                       Intent intent = new Intent(ReminderActivity.this, EditNoteActivity.class );
                       intent.putExtra(totaltime, total);
                       setResult(RESULT_OK, intent);
                       ReminderActivity.this.finish();

                   }catch (Exception e)
                   {
                       Toast.makeText(ReminderActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                   }


                }
            }
        });
    }
}