package com.example.unlost;

import android.app.Application;

import java.util.ArrayList;

public class NoteDetails extends Application {
    public static ArrayList<Note> notesList;

    @Override
    public void onCreate() {
        super.onCreate();

        notesList.add(new Note("Demo Note", "This is a Demo Note here you can note anything you like, their photos," +
                "videos and you can even set a Reminder for that thing using the Reminder Button so that you won't forget.Thanks!"));
    }
}
