package com.example.mp3player;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import android.net.Uri;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Mp3> mp3List;
    private ListView mp3View;

    @Override
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
    }

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
            int artistColumn = mp3Cursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            //add songs to list
            do {
                long thisId = mp3Cursor.getLong(idColumn);
                String thisTitle = mp3Cursor.getString(titleColumn);
                String thisArtist = mp3Cursor.getString(artistColumn);
                mp3List.add(new Mp3(thisId, thisTitle, thisArtist));
            }
            while (mp3Cursor.moveToNext());
        }
    }

}

