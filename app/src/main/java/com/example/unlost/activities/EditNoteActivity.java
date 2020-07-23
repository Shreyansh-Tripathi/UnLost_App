package com.example.unlost.activities;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.unlost.R;
import com.example.unlost.database.NotesDataBase;
import com.example.unlost.entities.Note;

public class EditNoteActivity extends AppCompatActivity {

    EditText editnoteTitle, editnoteContent;
    ImageButton cameraBtn, galleryBtn, reminderBtn, deletebtn, goBackbtn;
    CountDownTimer reminder;
    NotificationManagerCompat manager;
    final static int RQ_CODE=1;
    final static int notifyId=2;
    public static final String EDIT_NOTE_ID="edit_note_id";
    public static final String DELETE_NOTE="deleteNote";
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
                 reminderBtn.setTag("reminder_off");
                if (reminderBtn.getId() == R.id.reminder_off)
                {
                    Intent intent= new Intent(EditNoteActivity.this, ReminderActivity.class);
                    startActivityForResult(intent, RQ_CODE);
                }
                else {
                        reminderBtn.setImageResource(R.drawable.reminder_off);
                    Toast.makeText(EditNoteActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show();
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
                Intent intent=new Intent(EditNoteActivity.this, AllNotesActivity.class);
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
                Intent intent=new Intent(EditNoteActivity.this, AllNotesActivity.class);
                intent.putExtra(EDIT_NOTE_ID, noteId);
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
                    countertimer((long)total_secs);
                }
                else
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show();
            }
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
                .setColor(Color.BLUE)
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
                .setColor(Color.BLUE)
                .setSmallIcon(R.drawable.reminder_over)
                .setOngoing(true)
                .build();
        manager.notify(notifyId, notification);
    }
}