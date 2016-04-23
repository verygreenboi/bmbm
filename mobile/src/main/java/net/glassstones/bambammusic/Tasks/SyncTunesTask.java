package net.glassstones.bambammusic.tasks;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.intefaces.OnTuneSync;
import net.glassstones.bambammusic.models.IntentServiceResult;
import net.glassstones.bambammusic.services.UpdateLocalTunesService;
import net.glassstones.bambammusic.utils.MusicHelper;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.realm.Realm;
import me.tatarka.support.job.JobParameters;

/**
 * Created by Thompson on 20/04/2016.
 * For BambamMusic
 */
public class SyncTunesTask extends AsyncTask<JobParameters, Void, JobParameters> {
    JSONArray jsonTunes;
    private RequestQueue requestQueue;
    private UpdateLocalTunesService mJobService;
    private Context mContext;
    private OnTuneSync mListener;

    public SyncTunesTask(OnTuneSync l) {
        this.mListener = l;
    }

    public SyncTunesTask(Context context, RequestQueue requestQueue, UpdateLocalTunesService Jobservice) {
        this.mContext = context;
        this.requestQueue = requestQueue;
        this.mJobService = Jobservice;
    }

    public void setListener(OnTuneSync l) {
        this.mListener = l;
    }

    @Override
    protected JobParameters doInBackground(JobParameters... params) {
        Realm r = Realm.getInstance(mContext);
        JSONObject response;
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest j = MusicHelper.getTuneline(future);
        requestQueue.add(j);
        try {
            response = future.get(30000, TimeUnit.MILLISECONDS);
            jsonTunes = response.getJSONArray(Constants.KEY_TUNELINE_ARRAY_NAME);
            EventBus.getDefault().post(new IntentServiceResult(Activity.RESULT_OK, jsonTunes.toString()));
        } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
            e.printStackTrace();
        }
        return params[0];
    }

    @Override
    protected void onPostExecute(JobParameters jobParameters) {
        mJobService.jobFinished(jobParameters, false);
    }
}
