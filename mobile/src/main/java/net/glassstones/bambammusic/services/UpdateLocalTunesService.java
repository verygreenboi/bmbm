package net.glassstones.bambammusic.services;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by Thompson on 18/04/2016.
 * For BambamMusic
 */
public class UpdateLocalTunesService extends GcmTaskService {
    @Override
    public int onRunTask(TaskParams taskParams) {
        return 0;
    }
}
