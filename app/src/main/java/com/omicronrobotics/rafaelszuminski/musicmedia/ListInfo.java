package com.omicronrobotics.rafaelszuminski.musicmedia;

/**
 * Created by rafaelszuminski on 3/28/17.
 */

//Called in Adapter

public class ListInfo {
    String title;
    int imageId;

    public ListInfo(String title, int imageId) {
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}