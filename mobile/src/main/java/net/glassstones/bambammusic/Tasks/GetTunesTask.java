package net.glassstones.bambammusic.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.models.IntentServiceResult;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.library.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetTunesTask extends AsyncTask<ParseUser, Void, List<Tunes>> {
    private static final String TAG = GetTunesTask.class.getSimpleName();
    Context context;
    private OnTunesFetched mListener;
    private boolean streamToTop;
    private int mSkip;

    public GetTunesTask(Context context, boolean isStreamToTop, int skip) {
        this.streamToTop = isStreamToTop;
        this.context = context;
        this.mSkip = skip;
    }

    public void setListener(OnTunesFetched l) {
        this.mListener = l;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mListener.tunesFetchStarted();
        LogHelper.e(TAG, "Fetch started");
    }

    @Override
    protected List<Tunes> doInBackground(ParseUser... params) {
        return getTunes(params[0]);
    }

    @Override
    protected void onPostExecute(List<Tunes> tunes) {
        super.onPostExecute(tunes);
        mListener.tunesFetched(tunes, streamToTop);
    }

    @Nullable
    private List<Tunes> getTunes(ParseUser param) {
        List<Tunes> tunesList = new ArrayList<>();

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date sda = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));

        ParseQuery<ParseObject> friends = new ParseQuery<>("Activities");
        friends.whereEqualTo("type", "follow");
        friends.whereEqualTo("from", param);

        ParseQuery<ParseObject> friendsTunes = new ParseQuery<>("Tunes");
        friendsTunes.whereMatchesKeyInQuery("owner", "to", friends);

        ParseQuery<ParseObject> myTune = new ParseQuery<>("Tunes");
        myTune.whereEqualTo("owner", param);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();

        queries.add(friendsTunes);
        queries.add(myTune);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.setLimit(25);
        mainQuery.setSkip(mSkip);
        mainQuery.whereGreaterThanOrEqualTo("createdAt", sda);
        mainQuery.orderByDescending("createdAt");

        List<ParseObject> parseTunes = new ArrayList<>();

        ParseException exception = null;

        try {
            parseTunes = mainQuery.find();
        } catch (ParseException e) {
            exception = e;
            e.printStackTrace();
        }

        if (exception == null) {
            if (parseTunes.size() > 0) {
                try {
                    tunesList = save(parseTunes, tunesList);
                    /*
                    * TODO: 11/05/2016
                    * Synchronize tunes to remote like status
                    */
                    tunesList = syncLikes(tunesList);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_GET_FAILURE, exception));
        }
        return tunesList;
    }

    @Contract(value = "_ -> null", pure = true)
    private List<Tunes> syncLikes(List<Tunes> tunesList) {
        // FIXME: 11/05/2016
        return tunesList;
    }

    private List<Tunes> save(List<ParseObject> parseTunes, List<Tunes> tunesList) throws ParseException {

        for (ParseObject i : parseTunes) {
            ParseUser getUser = i.getParseUser("owner").fetch();
            boolean isLiked = getTuneLike(i.getObjectId());
            Tunes track = new Tunes();
            track.setTitle(i.getString("title"));
            track.setParseId(i.getObjectId());
            track.setDesc(i.getString("desc"));
            track.setTrackUrl(i.getParseFile("track").getUrl());
            track.setArtUrl(i.getParseFile("art").getUrl());
            track.setArtistName(getUser.getString("f_username"));
            track.setArtistObjId(getUser.getObjectId());
            track.setForSale(i.getBoolean("forSale"));
            track.setLiked(isLiked);
            track.setCreatedAt(i.getCreatedAt());

            tunesList.add(track);
        }

        return tunesList;
    }

    private boolean getTuneLike(String objectId) throws ParseException {
        ParseObject tune = getTuneParseObject(objectId);
        ParseQuery<ParseObject> like = ParseTuneQuery(tune, "like", ParseUser.getCurrentUser());
        return false;
    }

    private ParseQuery<ParseObject> ParseTuneQuery(ParseObject tune, String type, ParseUser user) {
        ParseQuery<ParseObject> like = new ParseQuery<>("Activities");
        like.whereEqualTo("tune", tune);
        like.whereEqualTo("type", type);
        if (user != null) {
            like.whereEqualTo("from", user);
        }
        return like;
    }

    private ParseObject getTuneParseObject(String t) throws ParseException {
        ParseQuery<ParseObject> getTune = new ParseQuery<>("Tunes");
        getTune.whereEqualTo("objectId", t);
        return getTune.getFirst();
    }

    public interface OnTunesFetched {
        void tunesFetched(List<Tunes> tunes, boolean streamToTop);

        void tunesFetchStarted();
    }
}
