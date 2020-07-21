package com.example.unlost;

public class Note {
    private String noteTitle;
    private String noteDetails;

    public Note(String noteTitle, String noteDetails) {
        this.noteTitle = noteTitle;
        this.noteDetails = noteDetails;
    }

    public String getNoteTitle() {
        return noteTitle;
    }

    public void setNoteTitle(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public String getNoteDetails() {
        return noteDetails;
    }

    public void setNoteDetails(String noteDetails) {
        this.noteDetails = noteDetails;
    }
}
