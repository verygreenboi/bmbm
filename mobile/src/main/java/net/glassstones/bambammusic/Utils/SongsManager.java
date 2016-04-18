package net.glassstones.bambammusic.utils;

import android.database.Cursor;

import net.glassstones.bambammusic.models.MediaData;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Thompson on 15/12/2015.
 */
public class SongsManager {
    // SDCard Path
    final String MEDIA_PATH = System.getenv("EXTERNAL_STORAGE");
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<>();

    // Constructor
    public SongsManager() {

    }

    /**
     * Function to read all mp3 files from sdcard
     * and store the details in ArrayList
     */
    public ArrayList<HashMap<String, String>> getPlayList() {
        File home = new File(MEDIA_PATH);

        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> song = new HashMap<>();
                song.put("songTitle", file.getName().substring(0, (file.getName().length() - 4)));
                song.put("songPath", file.getPath());

                // Adding each song to SongList
                songsList.add(song);
            }
        }

        // return songs list array
        return songsList;

    }

    public List<MediaData> getMusicList(Cursor c) {
        List<MediaData> mediaDataList = new ArrayList<>();
        for (int i = 0; i < c.getCount(); i++) {
            c.moveToPosition(i);

            String uri = c.getString(3);

            if (uri.contains(".mp3")) {
                MediaData mediaData = new MediaData();

                mediaData.setTitle(c.getString(0));
                mediaData.setArtist(c.getString(1));
                mediaData.setDuration(c.getString(2));
                mediaData.setPath(c.getString(3));
                mediaData.setChecked(false);

                mediaDataList.add(mediaData);
            }


        }
        return mediaDataList;
    }

    /**
     * Class to filter files which are having .mp3 extension
     */

    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }

}
