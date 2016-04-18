package net.glassstones.bambammusic.utils.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Thompson on 03/04/2016.
 * For BambamMusic
 */
public class AppPreferences {
    private static final String APP_SHARED_PREFS = AppPreferences.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public AppPreferences(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }

    public String getSomeString(String key) {
        return _sharedPrefs.getString(key, ""); // Get our string from prefs or return an empty string
    }

    public void saveSomeString(String key, String text) {
        _prefsEditor.putString(key, text);
        _prefsEditor.commit();
    }
}
