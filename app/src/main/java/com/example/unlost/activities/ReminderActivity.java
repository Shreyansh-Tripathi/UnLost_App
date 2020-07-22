package com.example.unlost.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
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

        getWindow().setLayout((int)(width*.7), (int)(height*.5));

        WindowManager.LayoutParams params= getWindow().getAttributes();
        params.gravity= Gravity.CENTER;
        params.x=0;
        params.y=-10;

        getWindow().setAttributes(params);

        startTimerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (ethours.getText().toString().trim().isEmpty() && etmins.getText().toString().trim().isEmpty()
                        && etsecs.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(ReminderActivity.this, "No Values found", Toast.LENGTH_SHORT).show();
                }

                else {
                    int hours = Integer.parseInt(ethours.getText().toString().trim());
                    int mins = Integer.parseInt(etmins.getText().toString().trim());
                    int secs = Integer.parseInt(etsecs.getText().toString().trim());

                    Intent intent = new Intent();
                      int total= secs + (mins*60) + (hours*60*60);
                      intent.putExtra(totaltime, total);
                      setResult(RESULT_OK, intent);

                      ReminderActivity.this.finish();
                }
            }
        });
    }
}