package com.example.unlost;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class EditNoteActivity extends AppCompatActivity {

    EditText editnoteTitle, editnoteContent;
    ImageButton cameraBtn, galleryBtn, reminderBtn;
    CountDownTimer reminder;
    final static int RQ_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        editnoteContent=findViewById(R.id.editnoteContent);
        editnoteTitle=findViewById(R.id.editnoteTitle);
        cameraBtn=findViewById(R.id.camerabtn);
        galleryBtn=findViewById(R.id.gallerybtn);
        reminderBtn=findViewById(R.id.reminderbtn);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        reminderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reminderBtn.getDrawable().equals(R.drawable.reminder_on))
                {
                    Intent intent= new Intent(EditNoteActivity.this, ReminderActivity.class);
                    startActivityForResult(intent, RQ_CODE);
                }

                else {
                        reminder.cancel();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RQ_CODE || resultCode==RESULT_OK)
        {
             reminderBtn.setImageResource(R.drawable.reminder_off);
             int total_secs=data.getIntExtra(ReminderActivity.totaltime, -1);
             if (total_secs!= -1)
             {
                 countertimer(total_secs);
             }
        }
    }

    public void countertimer(long timer)
    {
        reminder = new CountDownTimer(timer * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {


            }

            @Override
            public void onFinish() {

            }
        }.start();
    }
}