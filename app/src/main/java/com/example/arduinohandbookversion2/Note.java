package com.example.arduinohandbookversion2;

import androidx.annotation.NonNull;

public class Note {
    public int id;
    public String title, text, picture;

    public Note(int id, String title, String text, String picture) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.picture = picture;
    }

    public Note(String title, String text, String picture) {
        this(-1, title, text, picture);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", title='" + title + '}';
    }
}
