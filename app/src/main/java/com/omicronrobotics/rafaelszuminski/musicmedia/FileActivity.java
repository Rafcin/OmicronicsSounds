package com.omicronrobotics.rafaelszuminski.musicmedia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
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

import wseemann.media.jplaylistparser.parser.m3u.M3UPlaylistParser;

/**
 * Created by rafaelszuminski on 2/2/17.
 */

public class FileActivity extends AppCompatActivity {

    //~~~[File Vars]~~~\\
    // Include the custom made adapter.
    FilesAdapter adapter;
    ListView lv;
    ArrayList<File> fileArray;
    String currentPath;
    String previousPath;

    //Sets the context, easier than doing this.get...();
    final Context c = this;

    Button mMusicSave;

    MyMediaScannerConnectionClient mMediaScanner;

    wseemann.media.jplaylistparser.parser.m3u.M3UPlaylistParser mM3Parser;

    String m3uFileName;


    ArrayDeque<String> fileHistory;

    private static final String TAG = "MusicTool";

    //Setup location to access.
    @SuppressLint("sdcard/")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);
        final Intent int1 = getIntent();
        //File History to store.
        fileHistory = new ArrayDeque<String>();
        //Init saveButton
        mMusicSave = (Button) findViewById(R.id.saveMusicBtn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

        mM3Parser = new M3UPlaylistParser();

        m3uFileName = new String();

        mediastoreTest();




        //Sets Toolbar/removes Toolbar title.
        Toolbar mToolBar = (Toolbar)findViewById(R.id.toolbar2);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Sets fullscreen so cant see InfoBar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        fileArray = getFiles(Environment.getExternalStorageDirectory().getPath());
        Log.d(TAG, "currentPath" + currentPath);

        adapter = new FilesAdapter(fileArray, this);
        lv = (ListView) findViewById(R.id.listviewsd);
        //Add as HOME so no error due to Null
        fileHistory.add("/storage/emulated/0/");
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (fileArray.get(i).isDirectory()) {
                    String filePath = fileArray.get(i).getAbsolutePath();
                    Log.d(TAG, "" + filePath);
                    fileHistory.add(filePath);
                    fileArray = getFiles(filePath);
                    adapter.setFileData(fileArray);
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
            fileArray = getFiles(goingTo);
        }
        adapter.setFileData(fileArray);


    }

    private ArrayList<File> getFiles(String path) {
        ArrayList<File> directoriesArray = new ArrayList<File>();
        previousPath = currentPath;
        currentPath = path;
        File file = new File(path);
        File[] allfiles = file.listFiles();
        for (int i = 0; i < allfiles.length; i++) {
            File fileItem = allfiles[i];
            if (fileItem.isDirectory()) {
                directoriesArray.add(fileItem);
            } else if (fileItem.isFile() && fileItem.getAbsolutePath().endsWith(".mp3")) {
                directoriesArray.add(fileItem);
            }


        }
        return directoriesArray;
    }


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
                savePlaylist("/storage/emulated/0/Music/", inputText.getText().toString() + ".m3u");
                m3uFileName = inputText.getText().toString();
            }
        });
        AlertDialog alertDialogAll = alertDia.create();
        alertDialogAll.show();

    }



    public void savePlaylist(String fLoc, String fileName) {
        Log.d(TAG, "ItemsInAdapt " + adapter.getFileData().size());
        String playlist = "";
        for (File file : adapter.getFileData()) {
            if (file.isFile()) {
                playlist += "\"" + file.getName() + "\"" + "\n";
                Log.d("Music File", ": " + file.getName());
            }
        }

        // "/storage/emulated/0/Music/"

        Log.d("PlayListInfo", ": " + playlist);
        String fileLoc = fLoc;
        File m3uFile = new File(fileLoc, fileName);
        Log.d("FileType",": "+m3uFile);

        List<Integer> listOfMusicIds = new ArrayList<>();
        listOfMusicIds.add(25743);
        listOfMusicIds.add(25719);
        listOfMusicIds.add(14385);

        //Adds the music/makes the playlist
        SavePlaylist(this,listOfMusicIds,m3uFileName);
        //M3U filename is the promt text you write/input. Name of playlist saved.


        Log.d("IntentLog","Intent Sent");
    }

    public void mediastoreTest(){

        ContentResolver cR = this.getContentResolver();

        Uri music = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] fileData = { MediaStore.Audio.Media.DATA };


        Cursor cursor = cR.query(music, new String[]{"*"},null,null,null);

        cursor.moveToFirst();

        Log.d("MediaStoreTest",": "+cursor.getColumnNames());

        int i = 0;

        //String[] columnFilePath = {MediaStore.Audio.Media.DATA};
        //int columnIndex = cursor.getColumnIndexOrThrow(String.valueOf(columnFilePath));

        for(String columnName : cursor.getColumnNames()){
            Log.d("MediaStoreTest",": "+columnName);
            Log.d("MediaStoreFilePath",": "+fileData);
        }

        do{
            Log.d("MediaStoreTest",": " + cursor.getString(cursor.getColumnIndex("_id"))
                + " - " + cursor.getString(cursor.getColumnIndex("_display_name"))
                    + " - " + cursor.getString(cursor.getColumnIndex("_data"))

            );

        }while (cursor.moveToNext());
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
    public static void SavePlaylist(Context context, List<Integer> playList, String newPlaylistName) {

        // Gets the content resolver
        ContentResolver contentResolver = context.getContentResolver();

        Uri playlistsUri = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;

        Cursor cursor = contentResolver.query(playlistsUri, new String[] { "*" }, null, null, null);

        long playlistId = 0;

        cursor.moveToFirst();
        do {
            String playlistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
            if (playlistName.equalsIgnoreCase(newPlaylistName)) {
                playlistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID));
                break;
            }
        } while (cursor.moveToNext());

        cursor.close();

        if (playlistId != 0) {
            Uri deleteUri = ContentUris.withAppendedId(playlistsUri, playlistId);

            contentResolver.delete(deleteUri, null, null);
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.NAME, newPlaylistName);
        values.put(MediaStore.Audio.Playlists.DATE_MODIFIED, System.currentTimeMillis());

        Uri newPlaylistUri = contentResolver.insert(playlistsUri, values);

        Uri insertUri = Uri.withAppendedPath(newPlaylistUri, MediaStore.Audio.Playlists.Members.CONTENT_DIRECTORY);

        int order = 1;

        for (int id : playList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, order++);
            contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, id);
            contentResolver.insert(insertUri, contentValues);
        }
    }


}
