package net.glassstones.bambammusic.services;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.tasks.SyncTunesTask;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;

/**
 * Created by Thompson on 18/04/2016.
 * For BambamMusic
 */
public class UpdateLocalTunesService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        new SyncTunesTask(getApplicationContext(), Common.getsInstance().getRequestQueue(), this).execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
