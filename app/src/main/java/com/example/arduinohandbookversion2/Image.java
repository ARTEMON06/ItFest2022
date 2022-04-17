package com.example.arduinohandbookversion2;

public class Image {
    String label, src;

    public Image(String label, String src) {
        this.label = label;
        this.src = src;
    }

    public Image() {
        this.label = "label";
        this.src = "src";
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }
}
