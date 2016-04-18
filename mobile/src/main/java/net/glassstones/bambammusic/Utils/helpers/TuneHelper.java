package net.glassstones.bambammusic.utils.helpers;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.models.Tunes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

public class TuneHelper {

    public static List<ParseObject> getMyTunes(int offset, int tuneCount) throws ParseException {
//        long DAY_IN_MS = 1000 * 60 * 60 * 24;
//        Date sda = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS));
//
//        ParseQuery<ParseObject> friends = new ParseQuery<>("Activities");
//        friends.whereEqualTo("type", "follow");
//        friends.whereEqualTo("from", ParseUser.getCurrentUser());
//
//        ParseQuery<ParseObject> friendsTunes = new ParseQuery<>("Tunes");
//        friendsTunes.whereMatchesKeyInQuery("owner", "to", friends);
//
//        ParseQuery<ParseObject> myTune = new ParseQuery<>("Tunes");
//        myTune.whereEqualTo("owner", ParseUser.getCurrentUser());
//
//        List<ParseQuery<ParseObject>> queries = new ArrayList<>();
//
//        queries.add(friendsTunes);
//        queries.add(myTune);
//
//        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
//        mainQuery.setLimit(25);
//        if (tuneCount > 0) {
//            mainQuery.setSkip(offset);
//            mainQuery.whereGreaterThanOrEqualTo("createdAt", sda);
//        }
//        mainQuery.orderByDescending("createdAt");
//
//        Log.e("TAG", String.valueOf(mainQuery.count()));
//
//        return mainQuery.find();

        ParseQuery<ParseObject> q = new ParseQuery<ParseObject>("Tunes");

        return q.find();

    }

    public static List<Tunes> tunes2Realm(List<ParseObject> parseObjects) throws ParseException {
        List<Tunes> tunes = new ArrayList<>();
        for (ParseObject p : parseObjects) {
            ParseUser pUser = p.getParseUser("owner").fetch();
            Tunes t = new Tunes();
            t.setArtistName(pUser.getString("f_username"));
            t.setArtistObjId(pUser.getObjectId());
            t.setTitle(p.getString("title"));
            t.setParseId(p.getObjectId());
            t.setDesc(p.getString("desc"));
            t.setTrackUrl(p.getParseFile("track").getUrl());
            t.setArtUrl(p.getParseFile("art").getUrl());
            t.setForSale(p.getBoolean("forSale"));
            t.setIsLiked(false);
            t.setCreatedAt(p.getCreatedAt());
            t.setMediaType(p.getInt("media_type"));

            tunes.add(t);

        }

        return tunes;
    }

    public static List<Tunes> array2tune(JSONArray jsonTunes) throws JSONException, java.text.ParseException {
        List<Tunes> tunes = new ArrayList<>();
        for (int i = 0; i < jsonTunes.length(); i++) {
            tunes.add(addTune(jsonTunes.getJSONObject(i)));
        }
        return tunes;
    }

    private static Tunes addTune(JSONObject tune) throws JSONException, java.text.ParseException {
        Realm r = Common.getRealm();

        Tunes t = new Tunes();
        r.beginTransaction();
        t.setTitle(tune.getString(Tunes.TRACK_TITLE));
        t.setParseId(tune.getString(Tunes.TRACK_ID));
        t.setDesc(tune.getString(Tunes.TRACK_DESC));
        if (tune.has("track"))
            t.setTrackUrl(tune.getJSONObject("track").getString(Tunes.TRACK_URL));
        if (tune.has("art"))
            t.setArtUrl(tune.getJSONObject("art").getString(Tunes.ART_URL));
        t.setArtistName(tune.getJSONObject("owner").getString("f_username"));
        t.setArtistObjId(tune.getJSONObject("owner").getString("objectId"));
        t.setForSale(tune.getBoolean("forSale"));
        t.setIsLiked(false);
        t.setMediaType(tune.getInt("media_type"));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        t.setCreatedAt(format.parse(tune.getString("createdAt")));
        r.commitTransaction();

        return t;
    }

}
