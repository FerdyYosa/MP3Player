package com.example.mp3player;

public class Mp3 {
    private long id;
    private String title;
    private String mp32;

    public Mp3(long mp3ID, String mp3Title, String mp3) {
        id=mp3ID;
        title=mp3Title;
        mp32=mp3;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return mp32;}

}
