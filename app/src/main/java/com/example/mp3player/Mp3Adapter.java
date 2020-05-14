package com.example.mp3player;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Mp3Adapter extends BaseAdapter {
    private ArrayList<Mp3> mp3s;
    private LayoutInflater mp3Inf;

    @Override
    public int getCount() {
        return mp3s.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout mp3Lay = (LinearLayout)mp3Inf.inflate
                (R.layout.mp3, parent, false);
        //get title and artist views
        TextView mp3View = (TextView)mp3Lay.findViewById(R.id.mp3_title);
        TextView mp32View = (TextView)mp3Lay.findViewById(R.id.mp3);
        //get song using position
        Mp3 currMp3 = mp3s.get(position);
        //get title and artist strings
        mp3View.setText(currMp3.getTitle());
        mp32View.setText(currMp3.getArtist());
        //set position as tag
        mp3Lay.setTag(position);
        return mp3Lay;
    }

    public Mp3Adapter(Context c, ArrayList<Mp3> theMp3s){
        mp3s=theMp3s;
        mp3Inf=LayoutInflater.from(c);
    }
}
