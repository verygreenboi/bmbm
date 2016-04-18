package net.glassstones.bambammusic.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.glassstones.bambammusic.models.Tunes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class GetTunesTask extends AsyncTask<ParseUser, Void, List<Tunes>> {

    Realm realm;
    Context context;

    public GetTunesTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<Tunes> doInBackground(ParseUser... params) {
        ParseUser mUser = params[0];
        List<Tunes> tunesList = new ArrayList<>();

        realm = Realm.getInstance(context);

        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date sda = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));

        ParseQuery<ParseObject> friends = new ParseQuery<>("Activities");
        friends.whereEqualTo("type", "follow");
        friends.whereEqualTo("from", mUser);

        ParseQuery<ParseObject> friendsTunes = new ParseQuery<>("Tunes");
        friendsTunes.whereMatchesKeyInQuery("owner", "to", friends);

        ParseQuery<ParseObject> myTune = new ParseQuery<>("Tunes");
        myTune.whereEqualTo("owner", mUser);

        List<ParseQuery<ParseObject>> queries = new ArrayList<>();

        queries.add(friendsTunes);
        queries.add(myTune);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.setLimit(25);
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

        if (exception == null){
            if (parseTunes.size() > 0 ){
                try {
                    tunesList = save(parseTunes, tunesList);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return tunesList;
    }

    @Override
    protected void onPostExecute(List<Tunes> tunes) {
        super.onPostExecute(tunes);
        realm.close();
    }

    private List<Tunes> save(List<ParseObject> parseTunes, List<Tunes> tunesList) throws ParseException {

        for (ParseObject i : parseTunes){
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

            realm.beginTransaction();
            realm.copyToRealm(track);
            realm.commitTransaction();

            tunesList.add(track);
        }

        return tunesList;
    }

    private Tunes getTunes(ParseUser getUser, ParseObject i, boolean isLiked) {
        return new Tunes();
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
}
