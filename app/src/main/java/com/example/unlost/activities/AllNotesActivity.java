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
    public static final int ADDNOTE_REQUEST=2;
    public static final int GETALLNOTES=1;
    public static int UPDATENOTE_REQUEST= 3;
    private int noteClicked;

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

        notesAdapter= new NotesAdapter(AllNotes, this);
        layoutManager=new StaggeredGridLayoutManager(2, GridLayoutManager.VERTICAL);
        recNotes.setAdapter(notesAdapter);
        recNotes.setLayoutManager(layoutManager);

        getAllNotes(GETALLNOTES, false);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent=new Intent(getApplicationContext(), EditNoteActivity.class);
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
                     AllNotes.add(0, notes.get(0));
                     notesAdapter.notifyItemInserted(0);
                 }
                 else if (request_code == UPDATENOTE_REQUEST)
                 {
                     AllNotes.remove(noteClicked);
                     if (isNoteDeleted)
                     {
                         notesAdapter.notifyItemRemoved(noteClicked);
                     }
                     else {
                         AllNotes.add(noteClicked, notes.get(noteClicked));
                         notesAdapter.notifyItemChanged(noteClicked);
                     }
                 }
                 recNotes.smoothScrollToPosition(0);
             }
         }
         new GetAllNotes().execute();
    }

    @Override
    public void onClick(Note note, int index) {
        noteClicked=index;
        Intent intent= new Intent(getApplicationContext(), EditNoteActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("updateOrViewNote", true);
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
            assert data != null;
            getAllNotes(UPDATENOTE_REQUEST, data.getBooleanExtra(EditNoteActivity.DELETE_NOTE, false));
        }
    }
    public void backPressed(View view){
        onBackPressed();
    }
}