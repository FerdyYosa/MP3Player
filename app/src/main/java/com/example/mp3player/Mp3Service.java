package com.example.mp3player;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import java.util.ArrayList;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;
import java.util.Random;
import android.app.Notification;
import android.app.PendingIntent;

import androidx.annotation.RequiresApi;

public class Mp3Service extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener{

    private String mp3Title="";
    private static final int NOTIFY_ID=1;
    private boolean shuffle=false;
    private Random rand;
    //media player
    private MediaPlayer player;
    //mp3 list
    private ArrayList<Mp3> mp3s;
    //current position
    private int mp3Posn;
    private final IBinder mp3Bind = new Mp3Binder();

    public void onCreate(){
        super.onCreate();
        mp3Posn=0;
        player = new MediaPlayer();
        initMp3Player();
        rand=new Random();
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    public void initMp3Player(){
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void playMp3(){
        player.reset();
        Mp3 playMp3 = mp3s.get(mp3Posn);
        mp3Title=playMp3.getTitle();
        long currMp3 = playMp3.getID();
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currMp3);
        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MP3 SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mp3Bind;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void setList(ArrayList<Mp3> theMp3s){
        mp3s=theMp3s;
    }

    public void setMp3(int mp3Index){
        mp3Posn=mp3Index;
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        mp3Posn--;
        if(mp3Posn<=0) mp3Posn=mp3s.size()-1;
        playMp3();
    }

    public void playNext(){
        if(shuffle){
            int newMp3 = mp3Posn;
            while(newMp3==mp3Posn){
                newMp3=rand.nextInt(mp3s.size());
            }
            mp3Posn=newMp3;
        }
        else{
            mp3Posn++;
            if(mp3Posn>=mp3s.size()) mp3Posn=0;
        }
        playMp3();
    }

    public class Mp3Binder extends Binder {
        Mp3Service getService() {
            return Mp3Service.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }



}
