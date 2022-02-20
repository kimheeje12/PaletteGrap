package com.example.palettegrap.item;

import android.net.Uri;

public class PaintingUploadData {

    private String painting_image_path;
    private String painting_text;

    public String getPainting_image_path() {
        return painting_image_path;
    }

    public void setPainting_image_path(String painting_image_path) {
        this.painting_image_path = painting_image_path;
    }

    public String getPainting_text() {
        return painting_text;
    }

    public void setPainting_text(String painting_text) {
        this.painting_text = painting_text;
    }
}
