package com.example.unlost.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.unlost.R;

import java.util.ArrayList;

public class AllNotesActivity extends AppCompatActivity implements NotesAdapter.ItemClick {

    ImageButton addNote, backBtn;
    RecyclerView recNotes;
    RecyclerView.LayoutManager layoutManager;
    static NotesAdapter notesAdapter;
    static ArrayList<Note> AllNotes;
    TextView noteTitle, noteContent;
    public static final int NOTE_REQUEST=1;
    public static final String NOTE_ID= "noteId";

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SharedPreferences sharedPreferences= getApplicationContext().getSharedPreferences("com.example.unlost", MODE_PRIVATE);


        AllNotes.add(new Note("Demo Note", "This is a Demo Note here you can note anything you are likely to forget, their photos," +
                "videos and you can even set a Reminder for that thing using the Reminder Button so that you won't forget it.Thanks!"));

        notesAdapter= new NotesAdapter(AllNotes, AllNotesActivity.this);
        layoutManager=new GridLayoutManager(AllNotesActivity.this, 2, GridLayoutManager.VERTICAL, true);
        recNotes.setLayoutManager(layoutManager);
        recNotes.setAdapter(notesAdapter);

        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivityForResult(new Intent(AllNotesActivity.this, EditNoteActivity.class), NOTE_REQUEST);
            }
        });
    }

    public void updateAdapter(){
        notesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(int index) {
        Intent intent= new Intent(getApplicationContext(), EditNoteActivity.class);
        intent.putExtra(NOTE_ID, index);
        startActivity(intent);
    }
}