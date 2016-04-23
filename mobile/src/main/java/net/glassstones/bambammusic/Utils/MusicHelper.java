package net.glassstones.bambammusic.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.models.MediaData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MusicHelper {

    public static List<MediaData> scanSdcard(Context context) {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";

        Cursor cursor = null;
        List<MediaData> md = new ArrayList<>();
        try {
            Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            cursor = context.getContentResolver().query(uri, projection, selection, null, sortOrder);
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    MediaData media = new MediaData();
                    String title = cursor.getString(0);
                    String artist = cursor.getString(1);
                    String path = cursor.getString(2);
                    String displayName = cursor.getString(3);
                    String songDuration = cursor.getString(4);
                    media.setTitle(title);
                    media.setArtist(artist);
                    media.setPath(path);
                    media.setDuration(songDuration);
                    media.setChecked(false);
                    md.add(media);
                    cursor.moveToNext();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return md;
    }

    public static JsonObjectRequest getTuneline(RequestFuture<JSONObject> future) {
        return new JsonObjectRequest(
                Request.Method.POST,
                Constants.KEY_TUNELINE_URL,
                Common.getUser(),
                future,
                future
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("X-Parse-Application-Id", Constants.PARSE_APP_KEY);
                params.put("X-Parse-REST-API-Key", Constants.PARSE_REST_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
    }

}
