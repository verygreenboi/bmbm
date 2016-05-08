package net.glassstones.bambammusic.services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.models.IntentServiceResult;
import net.glassstones.bambammusic.models.MediaData;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.utils.helpers.FileHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class UploadTuneService extends Service {

    File tFile, aFile;
    ParseObject mTrack;
    MediaData mediaData;

    public static final int FILE_SIZE_LIMIT = 1024 * 1024 * 10; // 10MB

    public UploadTuneService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle b = intent.getExtras();

        mediaData = b.getParcelable("mediaData");

        if (mediaData != null){
            tFile = new File(mediaData.getPath());

            mTrack = new ParseObject("Tunes");
            mTrack.put("title", mediaData.getTitle());
            if (mediaData.getDesc() != null && !mediaData.getDesc().equals("")){
                mTrack.put("desc", mediaData.getDesc());
            }
            mTrack.put("forSale", false);
            mTrack.put("owner", ParseUser.getCurrentUser());
            mTrack.put("media_type", mediaData.getMediaType());

            int fileSize = 0;
            final InputStream inputStream = null;

            fileSize = getFileSize(Uri.fromFile(tFile), fileSize, inputStream);

            if (fileSize >= FILE_SIZE_LIMIT){
                Toast.makeText(this, R.string.error_file_size_too_large, Toast.LENGTH_LONG).show();
                stopSelf();
            } else {
                final byte[] fByte = FileHelper.getByteArrayFromFile(this.getApplicationContext(), Uri.fromFile(tFile));


                byte[] mArtFileBytes = FileHelper.getByteArrayFromFile(UploadTuneService.this, mediaData.getImageUri());

                Bitmap bitmap = BitmapFactory.decodeByteArray(mArtFileBytes, 0, mArtFileBytes.length);

                Uri artUri = getImageUri(this, bitmap);

                aFile = new File(getRealPathFromURI(artUri));

                mArtFileBytes = FileHelper.getByteArrayFromFile(this.getApplicationContext(), Uri.fromFile(aFile));

                SaveTrack(fByte, mArtFileBytes);

            }

        }

        return START_REDELIVER_INTENT;
    }

    private void SaveTrack(byte[] fByte, final byte[] mArtFileBytes) {
        final ParseFile tParseFile = new ParseFile("track.mp3",fByte);
        tParseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    mTrack.put("track", tParseFile);
                    SaveArt(mArtFileBytes);
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {

            }
        });
    }

    private void SaveArt(byte[] mArtFileBytes) {
        String artName = "art.jpg";

        Log.e("EXT", artName);
        final ParseFile aParseFile = new ParseFile(artName, mArtFileBytes);

        aParseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mTrack.put("art", aParseFile);
                mTrack.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            mTrack.getParseUser("owner").fetchInBackground(new GetCallback<ParseUser>() {
                                @Override
                                public void done(ParseUser user, ParseException e) {
                                    if (e == null){
                                        Tunes t = new Tunes();
                                        t.setTitle(mTrack.getString("title"));
                                        t.setParseId(mTrack.getObjectId());
                                        t.setDesc(mTrack.getString("desc"));
                                        t.setTrackUrl(mTrack.getParseFile("track").getUrl());
                                        t.setArtUrl(mTrack.getParseFile("art").getUrl());
                                        t.setArtistName(user.getString("f_username"));
                                        t.setArtistObjId(user.getObjectId());
                                        t.setForSale(mTrack.getBoolean("forSale"));
                                        // TODO: fix liked logic
                                        t.setLiked(false);
                                        t.setCreatedAt(mTrack.getCreatedAt());
                                        EventBus.getDefault().post(new IntentServiceResult(Activity.RESULT_OK, t));
                                        stopSelf();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {

            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String path = cursor.getString(idx);
        cursor.close();
        return path;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private int getFileSize(Uri tUri, int fileSize, InputStream inputStream) {
        try {
            inputStream = getContentResolver().openInputStream(tUri);

            assert  inputStream != null;

            fileSize = inputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileSize;
    }
}
