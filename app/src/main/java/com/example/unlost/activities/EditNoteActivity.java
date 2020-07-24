package com.example.unlost.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.unlost.R;
import com.example.unlost.database.NotesDataBase;
import com.example.unlost.entities.Note;

import static com.example.unlost.notification.NotificationChannel.channelId;

public class EditNoteActivity extends AppCompatActivity {

    EditText editnoteTitle, editnoteContent;
    ImageButton cameraBtn, galleryBtn, reminderBtn, deletebtn, goBackbtn;
    CountDownTimer reminder;
    private NotificationManagerCompat manager;
    final static int RQ_CODE=1;
    final static int notifyId1=2;
    public static final String EDIT_NOTE_ID="edit_note_id";
    public static final String DELETE_NOTE="deleteNote";
    boolean activetimer=false;
    int id=-99;

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
                final InputMethodManager iManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                assert iManager != null;
                iManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        Intent intent= getIntent();
        id=intent.getIntExtra(AllNotesActivity.NOTE_ID, -1);

        if (id != -1){
            editnoteTitle.setText(AllNotesActivity.AllNotes.get(id).getTitle());
            editnoteContent.setText(AllNotesActivity.AllNotes.get(id).getDescription());
        }

        else {
            Note newNote= new Note();
            newNote.setDescription("");
            newNote.setTitle("");
            AllNotesActivity.AllNotes.add(newNote);
            id= AllNotesActivity.AllNotes.size()-1;
        }

        if (TextUtils.isEmpty(editnoteContent.getText().toString().trim()) && TextUtils.isEmpty(editnoteTitle.getText().toString().trim()))
        {
            deletebtn.setVisibility(View.GONE);
        }
        else {
            deletebtn.setVisibility(View.VISIBLE);
        }

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
                if (!activetimer) {
                    Intent intent = new Intent(EditNoteActivity.this, ReminderActivity.class);
                    startActivityForResult(intent, RQ_CODE);
                }
                else {
                    reminderBtn.setImageResource(R.drawable.reminder_off);
                    reminder.cancel();
                    notifyReminderFinished(v);
                    activetimer=false;
                }
            }
        });

        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog= new AlertDialog.Builder(EditNoteActivity.this);
                alertDialog.setTitle("Delete Note!").setMessage("Are You Sure?").setIcon(R.drawable.delete)
                        .setPositiveButton("Yes, Delete!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteNote(id);
                            }
                        })
                        .setNegativeButton("No", null).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (editnoteTitle.getText().toString().trim().isEmpty() && editnoteContent.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Not Saved!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }

        else{
            saveNote(id);
        }
    }

    private void deleteNote(final int note_id)
    {
        @SuppressLint("StaticFieldLeak")
        class DeleteNoteTask extends AsyncTask<Void, Void, Void>
        {
            final Note note= AllNotesActivity.AllNotes.get(note_id);
            @Override
            protected Void doInBackground(Void... voids) {
                NotesDataBase.getDatabase(getApplicationContext()).getNoteDao().deleteNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent=new Intent();
                intent.putExtra(EDIT_NOTE_ID, note_id);
                intent.putExtra(DELETE_NOTE, true);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new DeleteNoteTask().execute();
    }

    private void saveNote(final int noteId)
    {
        final Note note = AllNotesActivity.AllNotes.get(noteId);
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
                Intent intent=new Intent();
                intent.putExtra(EDIT_NOTE_ID, noteId);
                intent.putExtra(DELETE_NOTE, false);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RQ_CODE && resultCode==RESULT_OK)
        {
            if (data != null){
                reminderBtn.setImageResource(R.drawable.reminder_on);
                int total_secs=data.getIntExtra(ReminderActivity.totaltime, -1);
                if (total_secs!= -1)
                {
                    notifyReminderInProgress(reminderBtn);
                    countertimer(total_secs, reminderBtn);
                }
                else
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void countertimer(final long timer, final View v)
    {
        reminderBtn.setImageResource(R.drawable.reminder_on);
        reminder = new CountDownTimer(timer * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                  activetimer=true;
            }

            @Override
            public void onFinish() {
                notifyReminderFinished(v);
                reminderBtn.setImageResource(R.drawable.reminder_off);
                reminder.cancel();
                activetimer=false;
                Toast.makeText(EditNoteActivity.this, "Reminder Time Finished!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }


    private void notifyReminderInProgress(View view)
    {
        Intent intent = new Intent(this, AllNotesActivity.class);
        PendingIntent gotoapp= PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification= new NotificationCompat.Builder(this, channelId)
                .setContentIntent(gotoapp)
                .setCategory(Notification.CATEGORY_ALARM)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentTitle("Reminder")
                .setContentText("In Progress")
                .setColor(Color.BLUE)
                .setSmallIcon(R.drawable.reminder_on)
                .setOngoing(true)
                .build();

        manager.notify(notifyId1, notification);
    }


    private void notifyReminderFinished(View view)
    {
        Intent intent = new Intent(this, AllNotesActivity.class);
        PendingIntent gotoapp= PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification= new NotificationCompat.Builder(this,channelId)
                .setContentIntent(gotoapp)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_ALARM)
                .setContentTitle("Reminder")
                .setContentText("Finished")
                .setColor(Color.BLUE)
                .setSmallIcon(R.drawable.reminder_over)
                .build();

        manager.notify(notifyId1, notification);
    }
}