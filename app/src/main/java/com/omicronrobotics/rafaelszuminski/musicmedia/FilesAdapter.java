package com.omicronrobotics.rafaelszuminski.musicmedia;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by rafaelszuminski on 2/4/17.
 */

public class FilesAdapter extends BaseAdapter {



    ArrayList<File> files; //Array
    Context adapContext;
    LayoutInflater layoutInflater;

    public FilesAdapter(ArrayList<File> arr, Context c){
        files = arr;
        adapContext = c;
        layoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if(files == null) {
            return 0;
        }
        return files.size();
    }

    @Override
    public Object getItem(int i) {
        if(files == null) {
            return null;
        }
        return files.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setFileData(ArrayList<File> fileArray){
        files = fileArray;
        //Data set to new Data to update it.
        notifyDataSetChanged();
        //Updates The Current Info

    }
    public ArrayList<File> getFileData(){
        return files;
    }




    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View layout = layoutInflater.inflate(R.layout.layoutview ,null);

        TextView viewFile = (TextView) layout.findViewById(R.id.fileItem);
        ImageView imageFile = (ImageView)layout.findViewById(R.id.listImage);
        TextView subFilePath = (TextView)layout.findViewById(R.id.filePath);
        imageFile.setImageResource(R.drawable.ic_action_name);
        viewFile.setText(files.get(i).getName());
        viewFile.setTextColor(Color.rgb(46,68,159));
        subFilePath.setText("");
        //Set Text 0
        if(files.get(i).getAbsolutePath().endsWith(".mp3")){
            viewFile.setTextColor(Color.rgb(0,150,136));
            viewFile.setTypeface(null, Typeface.BOLD);
            imageFile.setImageResource(R.drawable.ic_music);
            subFilePath.setText(files.get(i).getAbsolutePath().trim());
        }
        if(files.get(i).getAbsolutePath().contains("Music") || files.get(i).getAbsolutePath().contains("music")){
            //viewFile.setTextColor(Color.rgb(30,150,136));
        }
        viewFile.setHeight(220);


        return layout;



    }
}
