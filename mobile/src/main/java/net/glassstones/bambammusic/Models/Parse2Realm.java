package net.glassstones.bambammusic.models;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.glassstones.bambammusic.Common;

import io.realm.Realm;

@SuppressWarnings("unused")
public class Parse2Realm {

    int realmType;
    private Context mContext;
    private ParseObject mParseObject;
    private Object mRealm;
    private ParseUser user;
    private Realm realm;

    private Tunes t;
    private boolean like;

    public Parse2Realm(Context context, ParseObject parseObject, Object realm) {
        this.mContext = context;
        this.mParseObject = parseObject;
        this.mRealm = realm;
        this.realm = Common.getRealm();
    }

    public boolean parseToRealm() {
        boolean isRealm = false;

        if (mRealm instanceof Tunes || mRealm instanceof Comment || mRealm instanceof Like) {
            isRealm = true;

            if (mRealm instanceof Tunes)
                isRealm = parseTune();

        }

        return isRealm;
    }

    private boolean parseTune() {

        realmType = 0;
        final boolean[] done = {false};

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                user = mParseObject.getParseUser("owner");
                getLike();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (mParseObject != null && mRealm != null) {
                    t = (Tunes) mRealm;
                    t.setArtistName(user.getString("f_username"));
                    t.setArtistObjId(user.getObjectId());
                    t.setTitle(mParseObject.getString("title"));
                    t.setDesc(mParseObject.getString("desc"));
                    t.setTrackUrl(mParseObject.getParseFile("track").getUrl());
                    t.setArtUrl(mParseObject.getParseFile("art").getUrl());
                    t.setForSale(mParseObject.getBoolean("forSale"));
                    t.setCreatedAt(mParseObject.getCreatedAt());
                    if (TextUtils.equals(mParseObject.getString("media_type"),"tune")){
                        t.setMediaType(0);
                    } else {
                        t.setMediaType(1);
                    }
                    done[0] = commitToRealm();
                }

            }
        };

        task.execute();

        return done[0];

    }

    private boolean commitToRealm() {
        switch (realmType){
            case 0:
                if (t != null){
                    realm.beginTransaction();
                    realm.copyToRealm(t);
                    realm.commitTransaction();
                }
                break;
        }
        return true;
    }

    private void getLike() {
        ParseQuery<ParseObject> lk = ParseTuneQuery(mParseObject, "like", ParseUser.getCurrentUser());
        try {
            like = lk.getFirst() != null;
        } catch (ParseException e) {
            e.printStackTrace();
            like = false;
        }
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

}
