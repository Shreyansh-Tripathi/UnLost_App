package com.example.unlost.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.unlost.R;
import com.example.unlost.database.NotesDataBase;
import com.example.unlost.entities.Note;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;

import static com.example.unlost.notification.NotificationChannel.channelId;

public class EditNoteActivity extends AppCompatActivity {

    EditText editnoteTitle, editnoteContent;
    ImageButton cameraBtn, galleryBtn, reminderBtn, deletebtn, goBackbtn;
    static CountDownTimer reminder;
    NotificationManagerCompat manager;
    final static int RQ_CODE=1;
    final static int notifyId=2;
    public static final String EDIT_NOTE_ID="edit_note_id";
    public static final String DELETE_NOTE="deleteNote";
    static boolean activetimer;
    private static final int REQUEST_CODE_STORAGE_PERMISSION=3;
    private static final int REQUEST_CODE_CAMERA=5;
    private static final int REQUEST_CODE_SELECT_IMAGE=4;
    private static final int REQUEST_CODE_CAPTURE_IMAGE=6;
    String selectedImagePath1="",selectedImagePath2="",selectedImagePath3="";
    ImageView imgShow1,imgShow2,imgShow3;
    LinearLayout imgLayout;
    int id=500;

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
        imgShow1=findViewById(R.id.imgShow1);
        imgShow2=findViewById(R.id.imgShow2);
        imgShow3=findViewById(R.id.imgShow3);
        imgLayout=findViewById(R.id.imgLayout);

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

        if (!activetimer){
            reminderBtn.setImageResource(R.drawable.reminder_off);
        }
        else {
            reminderBtn.setImageResource(R.drawable.reminder_on);
        }

        Intent intent= getIntent();
        id=intent.getIntExtra(AllNotesActivity.NOTE_ID, -1);

        if (id != -1){
            editnoteTitle.setText(AllNotesActivity.AllNotes.get(id).getTitle());
            editnoteContent.setText(AllNotesActivity.AllNotes.get(id).getDescription());
            final Note note = AllNotesActivity.AllNotes.get(id);
            if(note.getImagePath1() != null)
            {
                imgLayout.setVisibility(View.VISIBLE);
                imgShow1.setVisibility(View.VISIBLE);
                imgShow1.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath1()));
                if(note.getImagePath2()!=null)
                {
                    imgShow2.setVisibility(View.VISIBLE);
                    imgShow2.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath2()));
                    if(note.getImagePath3()!=null)
                    {
                        imgShow3.setVisibility(View.VISIBLE);
                        imgShow3.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath3()));
                    }
                }
            }

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
                if(imgShow3.getDrawable()!=null)
                {
                    Toast.makeText(EditNoteActivity.this, "you cannot store more than 3 images!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditNoteActivity.this,
                                new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                    } else {
                        openCamera();
                    }
                }
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgShow3.getDrawable()!=null)
                {
                    Toast.makeText(EditNoteActivity.this, "you cannot store more than 3 images!", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),
                            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(EditNoteActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                    } else {
                        selectImage();
                    }
                }
            }
        });
        imgShow1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Note note = AllNotesActivity.AllNotes.get(id);
                Intent intent=new Intent(EditNoteActivity.this,ShowImage.class);
                intent.putExtra("imagePath",note.getImagePath1());
                startActivity(intent);
            }
        });
        imgShow2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Note note = AllNotesActivity.AllNotes.get(id);
                Intent intent=new Intent(EditNoteActivity.this,ShowImage.class);
                intent.putExtra("imagePath",note.getImagePath2());
                startActivity(intent);
            }
        });
        imgShow3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Note note = AllNotesActivity.AllNotes.get(id);
                Intent intent=new Intent(EditNoteActivity.this,ShowImage.class);
                intent.putExtra("imagePath",note.getImagePath3());
                startActivity(intent);
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
                    Toast.makeText(EditNoteActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
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
    private void openCamera() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,REQUEST_CODE_CAPTURE_IMAGE);
        }
    }

    private void selectImage() {
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                selectImage();
            }
            else
            {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==REQUEST_CODE_CAMERA && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                openCamera();
            }
            else
            {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (editnoteTitle.getText().toString().trim().isEmpty() && editnoteContent.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "Not Saved!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }

        else{
            saveNote(id);
        }
        super.onBackPressed();
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
        note.setDescription(editnoteContent.getText().toString());

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
        if(requestCode==REQUEST_CODE_SELECT_IMAGE && resultCode==RESULT_OK)
        {
            if(data!=null) {
                Uri selectedImageUri = data.getData();
                processImage(selectedImageUri);
            }

        }
        if(requestCode==REQUEST_CODE_CAPTURE_IMAGE && resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                Bitmap capturedImage = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                assert capturedImage != null;
                Uri capturedImageUri=getImageUri(this,capturedImage);
                processImage(capturedImageUri);
            }
        }
    }
    private Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void processImage(Uri imageUri) {
        final Note note = AllNotesActivity.AllNotes.get(id);
        if (imageUri!=null)
        {
            try{
                imgLayout.setVisibility(View.VISIBLE);
                InputStream inputStream=getContentResolver().openInputStream(imageUri);
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                if(imgShow1.getDrawable()==null)
                {
                    imgShow1.setVisibility(View.VISIBLE);
                    imgShow1.setImageBitmap(bitmap);
                    selectedImagePath1=getPathFromUri(imageUri);
                    note.setImagePath1(selectedImagePath1);
                }
                else if(imgShow2.getDrawable()==null)
                {
                    imgShow2.setVisibility(View.VISIBLE);
                    imgShow2.setImageBitmap(bitmap);
                    selectedImagePath2=getPathFromUri(imageUri);
                    note.setImagePath2(selectedImagePath2);
                }
                else if(imgShow3.getDrawable()==null)
                {
                    imgShow3.setVisibility(View.VISIBLE);
                    imgShow3.setImageBitmap(bitmap);
                    selectedImagePath3=getPathFromUri(imageUri);
                    note.setImagePath3(selectedImagePath3);
                }


            }
            catch (Exception e)
            {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri contentUri)
    {
        String filePath;
        Cursor cursor=getContentResolver().query(
                contentUri,null,null,null,null
        );
        if(cursor==null)
        {
            filePath=contentUri.getPath();
        }
        else
        {
            cursor.moveToFirst();
            int index=cursor.getColumnIndex("_data");
            filePath=cursor.getString(index);
            cursor.close();
        }
        return filePath;
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
                reminderBtn.setImageResource(R.drawable.reminder_off);
                notifyReminderFinished(v);
                reminder.cancel();
                activetimer=false;
                Toast.makeText(EditNoteActivity.this, "Reminder Time Finished!", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    public void notifyReminderInProgress(View view)
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

        manager.notify(notifyId, notification);
    }

    public void notifyReminderFinished(View view)
    {
        Intent intent = new Intent(this, AllNotesActivity.class);
        PendingIntent gotoapp= PendingIntent.getActivity(this, 0, intent, 0);

        Notification notification= new NotificationCompat.Builder(this, channelId)
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
        manager.notify(notifyId, notification);
    }
}