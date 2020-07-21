package com.example.unlost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class AllNotesActivity extends AppCompatActivity implements NotesAdapter.ItemClick {

    ImageButton addNote;
    RecyclerView recNotes;
    RecyclerView.LayoutManager layoutManager;
    NotesAdapter notesAdapter;
    TextView noteTitle, noteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_notes);
        addNote= findViewById(R.id.addNote);
        noteContent= findViewById(R.id.note_content);
        noteTitle=findViewById(R.id.note_title);
        recNotes=findViewById(R.id.recNotes);

        notesAdapter= new NotesAdapter(NoteDetails.notesList, AllNotesActivity.this);
        layoutManager=new GridLayoutManager(AllNotesActivity.this, 2, GridLayoutManager.VERTICAL, false);
        recNotes.setLayoutManager(layoutManager);
        recNotes.setAdapter(notesAdapter);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(AllNotesActivity.this,EditNoteActivity.class));

            }
        });
    }

    public void updateAdapter(){
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(int index) {

    }
}