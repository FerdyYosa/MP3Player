package com.example.mp3player;

public class Mp3 {
    private long id;
    private String title;
    private String artist;

    public Mp3(long songID, String songTitle, String songArtist) {
        id=songID;
        title=songTitle;
        artist=songArtist;
    }

    public long getID(){return id;}
    public String getTitle(){return title;}
    public String getArtist(){return artist;}

}
