package com.example.unlost.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.unlost.entities.NoteEntity;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<NoteEntity> getALlNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(NoteEntity note);

    @Delete
    void deleteNote(NoteEntity note);

}
