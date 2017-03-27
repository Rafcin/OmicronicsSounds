package com.omicronrobotics.rafaelszuminski.musicmedia;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MediaStoreTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.omicronrobotics.rafaelszuminski.musicmedia", appContext.getPackageName());
    }

    @Test
    public void mediaTest(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        ContentResolver cR = appContext.getContentResolver();

        Uri music = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = cR.query(music, new String[]{"*"},null,null,null);

        cursor.moveToFirst();

        Log.d("",": "+cursor.getColumnNames());

        int i = 0;

        do{
            Log.d("",": "+i++);
        }while (cursor.moveToNext());

    }
}
