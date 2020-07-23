package com.example.unlost.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.unlost.R;
import com.example.unlost.database.NotesDataBase;
import com.example.unlost.entities.Note;

import java.util.ArrayList;
import java.util.List;

public class AllNotesActivity extends AppCompatActivity implements NotesAdapter.ItemClick {

    ImageButton addNote, backBtn;
    RecyclerView recNotes;
    RecyclerView.LayoutManager layoutManager;
    static NotesAdapter notesAdapter;
    static ArrayList<Note> AllNotes;
    TextView noteTitle, noteContent;
    public static final int ADDNOTE_REQUEST=-1;
    public static final int GETALLNOTES=2;
    public static final String NOTE_ID="note_Id";
    public static int UPDATENOTE_REQUEST= 3;
    public static final String demoNote="This is a Demo Note. You can Add Your Own Note By Clicking On The Add Button " +
            "and then you can add Notes, images, videos and can even set Reminders for all those things you think you misgt forget, we'll remind you!";
    Note demo= new Note();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backBtn=findViewById(R.id.backbtn);
        setContentView(R.layout.activity_all_notes);
        addNote= findViewById(R.id.addNote);
        noteContent= findViewById(R.id.note_content);
        noteTitle=findViewById(R.id.note_title);
        recNotes=findViewById(R.id.recNotes);
        AllNotes= new ArrayList<>();

        notesAdapter= new NotesAdapter(AllNotes, AllNotesActivity.this);
        layoutManager=new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        recNotes.setLayoutManager(layoutManager);
        recNotes.setAdapter(notesAdapter);

        getAllNotes(GETALLNOTES, false);

        if(AllNotes.size()==0)
        {
            demo.setDescription(demoNote);
            demo.setTitle("Demo Note");
            AllNotes.add(demo);
        }

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=new Intent(AllNotesActivity.this, EditNoteActivity.class);
              intent.putExtra(NOTE_ID, -1);
              startActivityForResult(intent, ADDNOTE_REQUEST);
            }
        });
    }

    private void getAllNotes(final int request_code, final boolean isNoteDeleted){

         @SuppressLint("StaticFieldLeak")
         class GetAllNotes extends AsyncTask<Void, Void, List<Note>>{

             @Override
             protected List<Note> doInBackground(Void... voids) {
                 return NotesDataBase.getDatabase(getApplicationContext()).getNoteDao().getAllNotes();
             }

             @Override
             protected void onPostExecute(List<Note> notes) {
                 super.onPostExecute(notes);
                 if (request_code == GETALLNOTES)
                 {
                     AllNotes.addAll(notes);
                     notesAdapter.notifyDataSetChanged();
                 }
                 else if (request_code == ADDNOTE_REQUEST)
                 {
                     AllNotes.remove(demo);
                     AllNotes.add(0, notes.get(0));
                     notesAdapter.notifyItemInserted(0);
                     notesAdapter.notifyDataSetChanged();
                     recNotes.smoothScrollToPosition(0);
                 }
                 else if (request_code == UPDATENOTE_REQUEST && UPDATENOTE_REQUEST != -1)
                 {
                     AllNotes.remove(UPDATENOTE_REQUEST);
                     if (isNoteDeleted)
                     {
                         notesAdapter.notifyItemRemoved(UPDATENOTE_REQUEST);
                     }
                     else {
                         AllNotes.add(UPDATENOTE_REQUEST, notes.get(UPDATENOTE_REQUEST));
                         notesAdapter.notifyItemChanged(UPDATENOTE_REQUEST);
                         notesAdapter.notifyDataSetChanged();
                     }
                 }
             }
         }
         new GetAllNotes().execute();
    }

    @Override
    public void onClick(int index) {
        Intent intent= new Intent(getApplicationContext(), EditNoteActivity.class);
        intent.putExtra(NOTE_ID, index);
        startActivityForResult(intent, UPDATENOTE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== ADDNOTE_REQUEST && resultCode==RESULT_OK)
        {
            getAllNotes(ADDNOTE_REQUEST, false);
        }
        else if (requestCode==UPDATENOTE_REQUEST && resultCode==RESULT_OK)
        {
                if (data!=null){
                    UPDATENOTE_REQUEST=data.getIntExtra(EditNoteActivity.EDIT_NOTE_ID, -1);
                    getAllNotes(UPDATENOTE_REQUEST, data.getBooleanExtra(EditNoteActivity.DELETE_NOTE, false));
                }
        }
    }
    public void backPressed(View view){
        onBackPressed();
    }
}