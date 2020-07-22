package com.example.unlost.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.unlost.entities.NoteEntity;

@Database(entities = NoteEntity.class, version = 1, exportSchema = false)
public abstract class NotesDataBase extends RoomDatabase {

    private static NotesDataBase notesDataBase;

    public static synchronized NotesDataBase getDatabase(Context context)
    {
        if (notesDataBase == null){
            notesDataBase= Room.databaseBuilder(context, NotesDataBase.class, "notes_db").build();
        }
        return notesDataBase;
    }

    public abstract NoteDao getNoteDao();
}
