package net.glassstones.bambammusic.services;

import com.parse.ParseUser;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.models.TuneFetchModel;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.tasks.GetTunesTask;
import net.glassstones.bambammusic.tasks.SyncTunesTask;
import net.glassstones.bambammusic.utils.helpers.AppPreferences;
import net.glassstones.library.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;
import me.tatarka.support.os.PersistableBundle;

/**
 * Created by Thompson on 18/04/2016.
 * For BambamMusic
 */
public class UpdateLocalTunesService extends JobService implements GetTunesTask.OnTunesFetched {
    private static final String TAG = UpdateLocalTunesService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters params) {
        LogHelper.e(TAG, "Job started");
        AppPreferences ap = new AppPreferences(this);
        PersistableBundle bundle = params.getExtras();
        int skip = bundle.getInt(Constants.KEY_SKIP);
        GetTunesTask task = new GetTunesTask(this, ap.getTunelineIsFresh(Constants.KEY_TUNELINE_STATUS), skip);
        task.setListener(this);
        task.execute(ParseUser.getCurrentUser());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @Override
    public void tunesFetched(List<Tunes> tunes, boolean streamToTop) {
        EventBus.getDefault().post(new TuneFetchModel(tunes, streamToTop));
    }

    @Override
    public void tunesFetchStarted() {

    }
}
