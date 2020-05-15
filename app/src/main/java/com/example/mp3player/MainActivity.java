package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import com.example.mp3player.Mp3Service.Mp3Binder;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

public class MainActivity extends Activity implements MediaPlayerControl {

    private ArrayList<Mp3> mp3List;
    private ListView mp3View;
    private Mp3Service mp3Serv;
    private Intent playIntent;
    private boolean mp3Bound=false;
    private Mp3Controller controller;
    private boolean paused=false, playbackPaused=false;

    @Override
    protected void onPause(){
        super.onPause();
        paused=true;
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(paused){
            setController();
            paused=false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, Mp3Service.class);
            bindService(playIntent, mp3Connection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mp3View = (ListView)findViewById(R.id.mp3_list);
        mp3List = new ArrayList<Mp3>();

        getMp3List();

        Collections.sort(mp3List, new Comparator<Mp3>(){
            public int compare(Mp3 a, Mp3 b){
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        Mp3Adapter mp3Adt = new Mp3Adapter(this, mp3List);
        mp3View.setAdapter(mp3Adt);
        setController();
    }

    private void setController(){
        controller = new Mp3Controller(this);
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.mp3_list));
        controller.setEnabled(true);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
    }

    private void playNext(){
        mp3Serv.playNext();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    private void playPrev(){
        mp3Serv.playPrev();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    //connect to the service
    private ServiceConnection mp3Connection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Mp3Service.Mp3Binder binder = (Mp3Service.Mp3Binder) service;
            //get service
            mp3Serv = binder.getService();
            //pass list
            mp3Serv.setList(mp3List);
            mp3Bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mp3Bound = false;
        }
    };

    public void getMp3List() {
        ContentResolver mp3Resolver = getContentResolver();
        Uri mp3Uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor mp3Cursor = mp3Resolver.query(mp3Uri, null, null, null, null);

        if(mp3Cursor!=null && mp3Cursor.moveToFirst()){
            //get columns
            int titleColumn = mp3Cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = mp3Cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int mp3Column = mp3Cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add mp3s to list
            do {
                long thisId = mp3Cursor.getLong(idColumn);
                String thisTitle = mp3Cursor.getString(titleColumn);
                String thisMp3 = mp3Cursor.getString(mp3Column);
                mp3List.add(new Mp3(thisId, thisTitle, thisMp3));
            }
            while (mp3Cursor.moveToNext());
        }
    }

    public void mp3Picked(View view){
        mp3Serv.setMp3(Integer.parseInt(view.getTag().toString()));
        mp3Serv.playMp3();
        if(playbackPaused){
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                mp3Serv.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                mp3Serv=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        mp3Serv=null;
        super.onDestroy();
    }

    @Override
    public void start() {
        mp3Serv.go();
    }

    @Override
    public void pause() {
        playbackPaused=true;
        mp3Serv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if(mp3Serv!=null && mp3Bound && mp3Serv.isPng())
        return mp3Serv.getDur();
    else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if(mp3Serv!=null && mp3Bound && mp3Serv.isPng())
        return mp3Serv.getPosn();
    else return 0;
    }

    @Override
    public void seekTo(int pos) {
        mp3Serv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if(mp3Serv!=null && mp3Bound)
        return mp3Serv.isPng();
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public class Mp3Controller extends MediaController{
        public Mp3Controller(Context c){
            super(c);
        }
        public void hide(){

        }
    }
}

