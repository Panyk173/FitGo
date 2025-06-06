package com.example.fitgo;

public class Exercise {
    String name;
    int imageResId;

    public Exercise(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getIconRes() {
        return imageResId;
    }
}
