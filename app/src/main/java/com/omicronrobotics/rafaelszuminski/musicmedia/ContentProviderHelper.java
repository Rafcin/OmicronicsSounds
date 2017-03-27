package com.omicronrobotics.rafaelszuminski.musicmedia;

/**
 * Created by rafaelszuminski on 3/23/17.
 */

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ContentProviderHelper {

    public static List<Music> getMusics(Context context, Long duration, int DurationRange, List<String> newPlaylistMusics) {
        // Defining the minimum and maximum duration of the music
        Long maxDuration = TimeUnit.SECONDS.toMillis(duration) + DurationRange;

        // Which columns to return
        String columns[] = new String[] { MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.TITLE_KEY, MediaStore.Audio.Media._ID };

        // URI to the external storage
        Uri mAudio = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        // Define the WHERE clause
        String whereClause = MediaStore.Audio.Media.IS_MUSIC + " <> 0";
        whereClause += duration == null ? "" : " AND " + MediaStore.Audio.Media.DURATION + " < " + maxDuration;

        // Prepare the TITLE_KEY list
        String titleKey = new String();
        for (String string : newPlaylistMusics) {
            titleKey.concat('"' + string + ',');
        }
        whereClause += newPlaylistMusics.size() <= 0 ? "" : " AND " + MediaStore.Audio.Media.TITLE_KEY + " NOT IN (" + titleKey + ")";

        // Gets the cursor with the musics found
        Cursor cursor = context.getContentResolver().query(mAudio, columns, whereClause, null, null);

        // Run through the cursor and fill a list with the music
        List<Music> musicList = null;
        if (cursor.moveToFirst()) {
            musicList = new ArrayList<Music>();
            do {
                // Create a new instance of Music class and fill it with the
                // information from Content Provider
                Music music = new Music();
                music.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
                music.setDuration(TimeUnit.MILLISECONDS.toSeconds(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))));
                music.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
                music.setTitleKey(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY)));
                music.setId(cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));

                musicList.add(music);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return musicList;
    }

    public static void SavePlaylist(Context context, List<Music> playList, String newPlaylistName) {

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

        for (Music music : playList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, order++);
            contentValues.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, music.getId());
            contentResolver.insert(insertUri, contentValues);
        }
    }
}
