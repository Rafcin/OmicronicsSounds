package com.omicronrobotics.rafaelszuminski.musicmedia;

/**
 * Created by rafaelszuminski on 3/23/17.
 */

public class Music {

    public Music() {
        // TODO Auto-generated constructor stub
    }

    private String name;
    private String path;
    // Music duration in seconds
    private long duration;
    private String titleKey;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    // Gets the music duration in seconds
    public long getDuration() {
        return duration;
    }

    // Sets the music duration in seconds
    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTitleKey() {
        return titleKey;
    }

    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

}