package com.example.unlost.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.unlost.R;
import com.example.unlost.database.NotesDataBase;
import com.example.unlost.entities.NoteEntity;

import java.util.HashSet;

public class EditNoteActivity extends AppCompatActivity {

    EditText editnoteTitle, editnoteContent;
    ImageButton cameraBtn, galleryBtn, reminderBtn, deletebtn, goBackbtn;
    CountDownTimer reminder;
    NotificationManagerCompat manager;
    final static int RQ_CODE=1;
    final static int notifyId=2;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        editnoteContent=findViewById(R.id.editnoteContent);
        editnoteTitle=findViewById(R.id.editnoteTitle);
        cameraBtn=findViewById(R.id.camerabtn);
        goBackbtn=findViewById(R.id.notebackbtn);
        galleryBtn=findViewById(R.id.gallerybtn);
        reminderBtn=findViewById(R.id.reminderbtn);
        deletebtn=findViewById(R.id.deleteNotebtn);

        manager= NotificationManagerCompat.from(this);

        goBackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Intent intent=getIntent();
        intent.getIntExtra(AllNotesActivity.NOTE_ID, -1);

        if (id != -1){
            editnoteTitle.setText(AllNotesActivity.AllNotes.get(id).getNoteTitle());
            editnoteContent.setText(AllNotesActivity.AllNotes.get(id).getNoteDetails());
        }

        else {
            AllNotesActivity.AllNotes.add(new Note("",""));
            id= AllNotesActivity.AllNotes.size()-1;
            AllNotesActivity.notesAdapter.notifyDataSetChanged();
        }

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences("com.example.unlost", MODE_PRIVATE);

            }
        });

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
                if (reminderBtn.getDrawable().equals(R.drawable.reminder_off))
                {
                    Intent intent= new Intent(EditNoteActivity.this, ReminderActivity.class);
                    startActivityForResult(intent, RQ_CODE);
                }
                else {
                        reminder.cancel();
                        reminderBtn.setImageResource(R.drawable.reminder_off);
                }
            }
        });
    }

    private void saveNote()
    {
        final NoteEntity note = new NoteEntity();
        note.setTitle(editnoteTitle.getText().toString().trim());
        note.setDescription(editnoteContent.getText().toString().trim());

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{


            @Override
            protected Void doInBackground(Void... voids) {
                NotesDataBase.getDatabase(getApplicationContext()).getNoteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent= new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RQ_CODE || resultCode==RESULT_OK)
        {
             reminderBtn.setImageResource(R.drawable.reminder_on);
             int total_secs=data.getIntExtra(ReminderActivity.totaltime, -1);
             if (total_secs!= -1)
             {
                 countertimer(total_secs);
             }
             else
                 Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void countertimer(long timer)
    {
        notifyReminderInProgress();
        reminder = new CountDownTimer(timer * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                notifyReminderFinished();
                reminder.cancel();
            }
        }.start();
    }

    public void notifyReminderInProgress()
    {
        Intent intent = new Intent(this, AllNotesActivity.class);
        PendingIntent gotoapp= PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification= new NotificationCompat.Builder(this)
                .setOnlyAlertOnce(true)
                .setContentIntent(gotoapp)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_ALARM)
                .setContentTitle("Reminder")
                .setContentText("In Progress")
                .setSmallIcon(R.drawable.reminder_on)
                .setOngoing(true)
                .build();
        manager.notify(notifyId, notification);
    }

    public void notifyReminderFinished()
    {
        Intent intent = new Intent(this, AllNotesActivity.class);
        PendingIntent gotoapp= PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification= new NotificationCompat.Builder(this)
                .setOnlyAlertOnce(true)
                .setContentIntent(gotoapp)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_ALARM)
                .setContentTitle("Reminder")
                .setContentText("Finished")
                .setSmallIcon(R.drawable.reminder_on)
                .setOngoing(true)
                .build();
        manager.notify(notifyId, notification);
    }
}