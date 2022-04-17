package com.example.arduinohandbookversion2;

import java.util.List;

public class Article {
    String title, text;
    List<Image> images;

    public Article(String title, String text, List<Image> images) {
        this.title = title;
        this.text = text;
        this.images = images;
    }

    public Article() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Article{" +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", images=" + images +
                '}';
    }
}
