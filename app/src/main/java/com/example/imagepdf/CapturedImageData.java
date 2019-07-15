package com.example.imagepdf;

import android.graphics.Bitmap;

public class CapturedImageData {
    private Bitmap imageBitmap;
    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }
    public String getImagePath() {
        return ImagePath;
    }
    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }
    private String ImagePath;
}
