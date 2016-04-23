package net.glassstones.bambammusic.utils;

import android.content.Context;
import android.os.AsyncTask;

import net.glassstones.bambammusic.models.Tunes;

import java.util.Date;

/**
 * Created by Thompson on 18/04/2016.
 * For BambamMusic
 */
public class TuneSyncUtil {
    public static final String TASK_ID = "tuneSyncTaskId";
    public static final String TASK_STATUS = "tuneSyncStatus";

    public class TuneSyncTask extends AsyncTask<Tunes, Tunes, Void>{

        private Context mContext;

        public TuneSyncTask(Context c) {
            this.mContext = c;
        }

        @Override
        protected Void doInBackground(Tunes... params) {
            Date createdAt = getTune(params[0]);
            return null;
        }
    }

    private Date getTune(Tunes tune) {
        return tune.getCreatedAt();
    }

}
