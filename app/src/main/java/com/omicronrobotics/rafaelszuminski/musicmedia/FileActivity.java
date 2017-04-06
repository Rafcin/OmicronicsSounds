package com.omicronrobotics.rafaelszuminski.musicmedia;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafaelszuminski on 2/2/17.
 *
 *
 * @TODO Fix Index 0 Error on Samsung
 * @TODO Create All rescan mediaserver
 * @TODO Redesign UI Later, make a bit more like Playmuisc?
 *
 *
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

    public List<String> playlistNames = new ArrayList<>();

    private static final String TAG = "MusicTool";

    Boolean doSave = false;

    //Setup location to access.
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
        mToolBar.setBackgroundColor(Color.rgb(255,87,34));
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
        lv.setBackgroundColor(Color.WHITE);
        lv.setDividerHeight(0);





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
                //Recurisive Playlist Maker
                listFinish();

            }
        });

        //Displays Custom Dialog box on start.
        introDialog();

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

    //Save Playlist
    //Credits to Songdro and edited by me.
    //Adds to playlist
    public void SavePlaylist(Context context, List<Integer> playList, String newPlaylistName) {

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


    //Custom intro dialog.
    public void introDialog(){
        new MaterialDialog.Builder(this)
                .theme(Theme.DARK)
                //.iconRes(R.mipmap.ic_launcher)
                .positiveColor(Color.WHITE)
                .title(R.string.app_name)
                .titleGravity(GravityEnum.CENTER)
                .titleColor(Color.WHITE)
                .content("Created by Rafael Szuminski")
                .positiveText("Continue")
                .backgroundColor(Color.rgb(48,48,48))
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .contentColor(Color.WHITE)
                .show();
    }

    public void listFinish(){
        new MaterialDialog.Builder(this)
                .theme(Theme.DARK)
                .title("Playlists Created")
                .titleGravity(GravityEnum.CENTER)
                .titleColor(Color.WHITE)
                .content("Are you sure you want to add all the playlists?")
                .positiveText("Yes")
                .positiveColor(Color.WHITE)
                .negativeColor(Color.WHITE)
                .negativeText("No")
                .backgroundColor(Color.rgb(48,48,48))
                .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                .contentColor(Color.WHITE)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        createPlaylistAuto(currentPath);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.d("DidNotSave","Playlist");
                    }
                })
                .show();
    }


    //Creates the playlists automatically (Is a recursive func)
    public void createPlaylistAuto(String path){
        ContentResolver cR = this.getContentResolver();
        Uri music = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        //No need to have New Array list each time in the for loop.
        List<Integer> listOfMusicIds = new ArrayList<>();

        //Starting point DIR
        File startingPoint = new File(path);
        //Get all the files! getFiles() is now irrelevant.
        File[] allfiles = startingPoint.listFiles();
        //Gets Folder
        //For each file found in the dir. if its a dir run it again and keep going into diffrent folders and getting Dirs or MP3s
        for(File file : allfiles){
            if(file.isDirectory()){
                createPlaylistAuto(file.getAbsolutePath());
                //If contains extension mp3 then do MP3 stuff like save to playlist.
            } else if(file.getAbsolutePath().toLowerCase().contains(".mp3")){
                Log.d("FilePathElse",": "+file.getAbsolutePath());
                // Loop through files of current dir.
                //Get Abs path of file. Then query content provider to check if file is in mediastore.
                //Loops Cursor getting all IDS
                Cursor cursor = cR.query(music, new String[]{"*"},"_data=?",new String[]{file.getAbsolutePath()},null);
                cursor.moveToFirst();
                if(!cursor.isAfterLast()){
                    listOfMusicIds.add(Integer.valueOf(cursor.getString(0)));
                    Log.d("CursorInfo",": "+cursor.getString(0));

                } else {
                    //this is just here. I leave this else statment for something....
                }
            }
        }
        //Once Done SAVE!
        SavePlaylist(this, listOfMusicIds, startingPoint.getName());

    }

}
