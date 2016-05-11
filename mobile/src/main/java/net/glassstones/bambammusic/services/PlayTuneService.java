package net.glassstones.bambammusic.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PlayTuneService extends Service {
    public PlayTuneService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
