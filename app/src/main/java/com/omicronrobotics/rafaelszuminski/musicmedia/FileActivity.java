package com.omicronrobotics.rafaelszuminski.musicmedia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafaelszuminski on 2/2/17.
 */

public class FileActivity extends AppCompatActivity {

    //~~~[File Vars]~~~\\
    // Include the custom made adapter.
    FilesAdapter adapter;
    ListView lv;
    ArrayList<File> mFileArray;
    String currentPath;
    String previousPath;

    //Sets the context, easier than doing this.get...();
    final Context c = this;

    Button mMusicSave;

    String m3uFileName;

    String playlist = "";

    ArrayDeque<String> fileHistory;

    private static final String TAG = "MusicTool";

    //Setup location to access.
    @SuppressLint("sdcard/")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);
        //File History to store.
        fileHistory = new ArrayDeque<String>();
        //Init saveButton
        mMusicSave = (Button) findViewById(R.id.saveMusicBtn);
        mMusicSave.setBackgroundResource(R.drawable.ic_savewrite2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        //MAKE STRING
        m3uFileName = new String();

        //Sets Toolbar/removes Toolbar title.
        Toolbar mToolBar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        mToolBar.setTitleTextColor(Color.WHITE);
        mToolBar.setTitle("Playlist Creator");

        //Sets fullscreen so cant see InfoBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        mFileArray = getFiles(Environment.getExternalStorageDirectory().getPath());
        Log.d(TAG, "currentPath" + currentPath);

        adapter = new FilesAdapter(mFileArray, this);
        lv = (ListView) findViewById(R.id.listviewsd);
        //Add as HOME so no error due to Null
        fileHistory.add("/storage/emulated/0/");
        //lv.setDivider(new ColorDrawable(Color.rgb(46,68,159)));  //hide the divider
        //lv .setClipToPadding(false);   // list items won't clip, so padding stays
        //lv.setDividerHeight(3);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mFileArray.get(i).isDirectory()) {
                    String filePath = mFileArray.get(i).getAbsolutePath();
                    Log.d(TAG, "" + filePath);
                    fileHistory.add(filePath);
                    mFileArray = getFiles(filePath);
                    adapter.setFileData(mFileArray);
                    Log.d(TAG, "Current Path" + currentPath);
                    Log.d(TAG, "Previous Path" + previousPath);
                }


            }
        });
        lv.setAdapter(adapter);

        // http://stackoverflow.com/questions/9116693/how-can-i-tell-when-an-album-is-added-to-the-mediastore

        getContentResolver().registerContentObserver(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
                new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        Log.d("ScratchService","External Media has been added");
                        super.onChange(selfChange);
                    }
                }
        );
        getContentResolver().registerContentObserver(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI, true,
                new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange) {
                        Log.d("ScratchService","Internal Media has been added");
                        super.onChange(selfChange);
                    }
                }
        );


        mMusicSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputText();
                //Testing Recurisve GetFile.
                //getFilesForMusicFolders(Environment.getExternalStorageDirectory().getPath());
                Log.d("getMusicDirs",": "+getFilesForMusicFolders(Environment.getExternalStorageDirectory().getPath()));
            }
        });




    }


    @Override
    public void onBackPressed() {


        //Navigation Back//
        /*

        Add to Queue
        Once back is pressed update view list in Adapter
        Pop file history/remove (same thing)




         */

        String removedItem = fileHistory.pollLast();
        //Log remved removedItem
        Log.d(TAG, "Removed Item " + removedItem);
        String goingTo = fileHistory.getLast();
        //log going to
        Log.d(TAG, "Going to " + goingTo);

        if (fileHistory != null) {
            mFileArray = getFiles(goingTo);
        }
        adapter.setFileData(mFileArray);


    }

    //Gets the files from the SDCARD. Used to display paths etc.
    private ArrayList<File> getFiles(String path) {
        ArrayList<File> directoriesArray = new ArrayList<File>();
        previousPath = currentPath;
        currentPath = path;
        File file = new File(path);
        File[] allfiles = file.listFiles();
        //loops for the ammount of the files.
        for (int i = 0; i < allfiles.length; i++) {
            File fileItem = allfiles[i];
            if (fileItem.isDirectory()) {
                //if its a Dir then add as dir.
                directoriesArray.add(fileItem);
            } else if (fileItem.isFile() && fileItem.getAbsolutePath().endsWith(".mp3")) {
                // if its a file and contains the ending format .mp3 then it is a mp3.
                directoriesArray.add(fileItem);
            }


        }
        //return it.
        return directoriesArray;
    }

    private ArrayList<File> getFilesForMusicFolders(String path) {
        ArrayList<File> directoriesArray = new ArrayList<File>();
        previousPath = currentPath;
        currentPath = path;
        File file = new File(path);
        File[] allfiles = file.listFiles();
        //loops for the ammount of the files.
        for (int i = 0; i < allfiles.length; i++) {
            File fileItem = allfiles[i];
            if (fileItem.isDirectory()) {
                //if its a Dir then add as dir.
                directoriesArray.add(fileItem);
            } else if (fileItem.isFile() && fileItem.getAbsolutePath().endsWith(".mp3")) {
                // if its a file and contains the ending format .mp3 then it is a mp3.
                getFiles(path);
                directoriesArray.add(fileItem);
            }


        }
        //return it.
        return directoriesArray;
    }


    //Function called for onclick to make it easier. Becuase its easier when you make it a function.
    public void inputText() {

        AlertDialog.Builder alertDia = new AlertDialog.Builder(c);
        LayoutInflater layoutInf = LayoutInflater.from(c);
        View mView = layoutInf.inflate(R.layout.input_info, null);
        final EditText inputText = (EditText) mView.findViewById(R.id.edittext);
        alertDia.setView(mView);
        alertDia.setTitle("Playlist Name");


        //Make Sure Usr SEES THIS!
        alertDia.setCancelable(false);
        alertDia.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                m3uFileName = inputText.getText().toString();
                loadPlaylist("/storage/emulated/0/Music/", inputText.getText().toString() + ".m3u");
            }
        });
        AlertDialog alertDialogAll = alertDia.create();
        alertDialogAll.show();

    }



    public void loadPlaylist(String fLoc, String fileName) {
        Log.d(TAG, "ItemsInAdapt " + adapter.getFileData().size());

        //Init Content Res, Music Uri and List that adds ids to playlist.
        ContentResolver cR = this.getContentResolver();
        Uri music = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        List<Integer> listOfMusicIds = new ArrayList<>();


        // Loop through files of current dir.
        for(File f : mFileArray){
            //Get Abs path of file. Then query content provider to check if file is in mediastore.
            int mediaId = 1;

            Cursor cursor = cR.query(music, new String[]{"*"},"_data=?",new String[]{f.getAbsolutePath()},null);
            cursor.moveToFirst();
            if(!cursor.isAfterLast()){
                listOfMusicIds.add(Integer.valueOf(cursor.getString(0)));
                Log.d("CursorInfo",": "+cursor.getString(0));

            } else {
                //AddFileToMediaStoreHere
                //Get ID of added file.
            }

        }
        //Saves the playlist.
        SavePlaylist(this,listOfMusicIds,m3uFileName);






       //Gets File MP3 Name.
        for (File file : adapter.getFileData()) {
            if (file.isFile()) {
                //playlist += "\"" + file.getName() + "\"" + "\n";
                playlist = file.getName();
                Log.d("Music File", ": " + file.getName());
                //Log.d("CursorName",": "+compareMusicName);
                String playlistNameAndLoc = fLoc+playlist;

            }

        }



        // "/storage/emulated/0/Music/"

        Log.d("PlayListInfo", ": " + playlist);
        String fileLoc = fLoc;
        File m3uFile = new File(fileLoc, fileName);
        Log.d("FileType",": "+m3uFile);
        Log.d("IntentLog","Intent Sent");
    }




    //Check File Path, used when Debuging, not worth having in code but good to use to test file paths.
    public String fileFilePath(Uri uri) {
        String uriFilePath;
        String[] fileData = { MediaStore.Images.Media.DATA };
        Cursor cursor = this.getContentResolver().query(uri, fileData, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    //Save Playlist

    //Credits to Songdro and edited by me.
    //Adds to playlist
    public static void SavePlaylist(Context context, List<Integer> playList, String newPlaylistName) {

        // Gets the content resolver
        ContentResolver contentResolver = context.getContentResolver();

        Uri playlistsUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(playlistsUri, new String[] { "*" }, null, null, null);

        long playlistId = 0;

        cursor.moveToFirst();
        do {
            //Gets Name
            String playlistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
            if (playlistName.equalsIgnoreCase(newPlaylistName)) {
                playlistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                break;
            }
        } while (cursor.moveToNext());
        // Do this while cursor moves to next item in the Database.

        cursor.close();
        // end-it curosr

        if (playlistId != 0) {
            Uri deleteUri = ContentUris.withAppendedId(playlistsUri, playlistId);

            contentResolver.delete(deleteUri, null, null);
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, newPlaylistName);
        values.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());

        Uri newPlaylistUri = contentResolver.insert(playlistsUri, values);

        //Insert with the dir of the mp3
        Uri insertUri = Uri.withAppendedPath(newPlaylistUri, MediaStore.Audio.Playlists.Members.CONTENT_DIRECTORY);

        int order = 1;

        for (int id : playList) {
            //Set Order,AudioID and insert the mp3.
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, order++);
            contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, id);
            contentResolver.insert(insertUri, contentValues);
        }
    }








}
