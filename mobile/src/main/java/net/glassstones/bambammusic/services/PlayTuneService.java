package net.glassstones.bambammusic.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.h6ah4i.android.media.IBasicMediaPlayer;
import com.h6ah4i.android.media.IMediaPlayerFactory;
import com.h6ah4i.android.media.opensl.OpenSLMediaPlayerFactory;

import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.models.IntentServiceResult;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.library.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class PlayTuneService extends Service implements IBasicMediaPlayer.OnCompletionListener,
        IBasicMediaPlayer.OnPreparedListener,
        IBasicMediaPlayer.OnErrorListener,
        IBasicMediaPlayer.OnSeekCompleteListener,
        IBasicMediaPlayer.OnInfoListener,
        IBasicMediaPlayer.OnBufferingUpdateListener {


    private static final String TAG = PlayTuneService.class.getSimpleName();
    IMediaPlayerFactory factory;
    private IBasicMediaPlayer mp;
    private boolean isPausedInCall = false;
    private Tunes currentTune;

    private PlaySongBinder mBinder = new PlaySongBinder();

    public PlayTuneService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        factory = new OpenSLMediaPlayerFactory(getApplicationContext());
        mp = factory.createMediaPlayer();
        mp.setOnCompletionListener(this);
        mp.setOnBufferingUpdateListener(this);
        mp.setOnPreparedListener(this);
        mp.setOnErrorListener(this);
        mp.setOnSeekCompleteListener(this);
        mp.setOnBufferingUpdateListener(this);
        mp.reset();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Handle calls

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener psl = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mp != null) {
                            pauseMedia();
                            isPausedInCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mp != null && currentTune != null) {
                            if (isPausedInCall) {
                                isPausedInCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };

        tm.listen(psl, PhoneStateListener.LISTEN_CALL_STATE);

        //Reset MediaPlayer
        mp.reset();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        factory.release();
        mp.release();
    }

    private void prepareMediaPlayer(Tunes tune) throws IOException {
        if (mp.isPlaying()) {
            stopMedia();
        }
        LogHelper.e(TAG, tune.getTrackUrl());
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setDataSource(this, Uri.parse(tune.getTrackUrl()));
        mp.prepareAsync();
    }

    public void playMedia() {
        LogHelper.e(TAG, "Playing");
        if (!mp.isPlaying()) {
            mp.start();
            EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_STATUS_PLAY, currentTune));
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int ct = mp.getCurrentPosition() / 1000;
                    LogHelper.e(TAG, String.valueOf(ct));
                }
            }, 1000);
        }
    }

    public void pauseMedia() {
        if (mp.isPlaying()) {
            mp.pause();
            EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_STATUS_PAUSE, currentTune));
        }
    }

    public void setTune(Tunes tune) {
        this.currentTune = tune;
        try {
            prepareMediaPlayer(currentTune);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onBufferingUpdate(IBasicMediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(IBasicMediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    private void stopMedia() {
        EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_STATUS_STOP, currentTune));
    }

    @Override
    public boolean onError(IBasicMediaPlayer mp, int what, int extra) {
        switch (what) {
            case IBasicMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                LogHelper.e(TAG, "Unsupported");
                break;
            case IBasicMediaPlayer.MEDIA_ERROR_SERVER_DIED:
            case IBasicMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                LogHelper.e(TAG, "Server died or something timed out");
                break;

        }
        return false;
    }

    @Override
    public boolean onInfo(IBasicMediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(IBasicMediaPlayer mp) {
        LogHelper.e(TAG, "Prepared");
        playMedia();
    }

    @Override
    public void onSeekComplete(IBasicMediaPlayer mp) {

    }

    public int getCurrentPosition() {
        int pos = 0;
        if (mp != null && mp.isPlaying()) {
            pos = mp.getCurrentPosition();
        }
        return pos;
    }

    public class PlaySongBinder extends Binder {
        public PlayTuneService getService() {
            return PlayTuneService.this;
        }
    }

}
