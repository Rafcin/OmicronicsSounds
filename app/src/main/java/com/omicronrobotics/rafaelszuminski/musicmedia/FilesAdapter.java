package com.omicronrobotics.rafaelszuminski.musicmedia;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by rafaelszuminski on 2/4/17.
 */

public class FilesAdapter extends BaseAdapter {

    ArrayList<File> files; //Array
    Context ctxt;
    LayoutInflater layoutInflater;

    public FilesAdapter(ArrayList<File> arr, Context c){
        files = arr;
        ctxt =c;
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

        TextView viewFile = null;
        viewFile = (TextView) layoutInflater.inflate(R.layout.layoutview ,null);
        viewFile.setText(files.get(i).getName());
        viewFile.setTextColor(Color.WHITE);
        viewFile.setHeight(230);

        return viewFile;

    }
}
