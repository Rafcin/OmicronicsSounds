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
 * Created by rafaelszuminski on 3/21/17.
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


    //Adapter Code\\

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
        View layout = layoutInflater.inflate(R.layout.item_view,null);

        TextView viewFile = (TextView) layout.findViewById(R.id.fileItem);
        ImageView imageFile = (ImageView)layout.findViewById(R.id.listImage);
        TextView subFilePath = (TextView)layout.findViewById(R.id.filePath);
        imageFile.setImageResource(R.drawable.ic_action_name);
        imageFile.setColorFilter(Color.rgb(255,193,7));
        viewFile.setText(files.get(i).getName());
        viewFile.setTextColor(Color.rgb(33,33,33));   //BlueColor -- Color.rgb(46,68,159)
        subFilePath.setText("");
        //Set Text 0
        if(files.get(i).getAbsolutePath().endsWith(".mp3")){
            viewFile.setTextSize(15);
            viewFile.setTextColor(Color.BLACK);
            viewFile.setTypeface(null, Typeface.BOLD);
            imageFile.setImageResource(R.drawable.ic_music_symbol);
            imageFile.setColorFilter(Color.BLACK);
            subFilePath.setText(files.get(i).getAbsolutePath().trim());
        }

        //Sets size to files that have long names so they dont exceed the listview.
        if(files.get(i).getAbsolutePath().endsWith(".mp3") && files.get(i).getAbsolutePath().contains("_") || files.get(i).getAbsolutePath().contains(")") || files.get(i).getAbsolutePath().contains("-")){
            viewFile.setTextSize(12);
        }

        viewFile.setHeight(220);


        return layout;



    }
}
