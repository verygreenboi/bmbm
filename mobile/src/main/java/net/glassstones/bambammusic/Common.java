package net.glassstones.bambammusic;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import net.glassstones.library.utils.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;


public class Common extends Application {

    public static final String TAG = LogHelper.makeLogTag(Common.class);
    public static Common mInstance;
    private static Realm realm;
    private static JSONObject user;
    RealmMigration migration = new RealmMigration() {
        @Override
        public void migrate(DynamicRealm dynamicRealm, long oldVersion, long l1) {
//            // DynamicRealm exposes an editable schema
//            RealmSchema schema = dynamicRealm.getSchema();
//            if (oldVersion == 0) {
////                schema.create("Person")
////                        .addField("name", String.class)
////                        .addField("age", int.class);
//                oldVersion++;
//            }
//            // Migrate to version 2: Add a primary key + object references
//            if (oldVersion == 1) {
//                RealmObjectSchema commentSchema = schema.get("Comment");
//                commentSchema.addField("status", int.class);
//                oldVersion++;
//            }
//            if (oldVersion == 2) {
//                RealmObjectSchema commentSchema = schema.get("Comment");
//                commentSchema
//                        .addField("c_index", long.class, FieldAttribute.PRIMARY_KEY)
//                        .transform(new RealmObjectSchema.Function() {
//                            @Override
//                            public void apply(DynamicRealmObject obj) {
//                                obj.set("c_index", new Date().getTime());
//                            }
//                        });
//                oldVersion++;
//            }
        }
    };
    private RequestQueue mRequestQueue;

    public synchronized static Common getsInstance() {
        assert mInstance != null;
        return mInstance;
    }

    public static Realm getRealm() {
        return realm;
    }

    public static JSONObject getUser() {
        return user;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogHelper.d(TAG, "Application created");

        mInstance = this;
//        PaystackSdk.initialize(getApplicationContext());
//        PaystackSdk.setPublishableKey(Constants.PUBLISHABLE_KEY);
        Fresco.initialize(getApplicationContext());

//        Parse.initialize(this, Constants.PARSE_APP_KEY, Constants.PARSE_CLIENT_KEY);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(Constants.PARSE_APP_KEY)
                .clientKey(null)
                .server(Constants.SERVER_URL)   // '/' important after 'parse'
                .build());

        ParseFacebookUtils.initialize(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        RealmConfiguration config = new RealmConfiguration.Builder(this)
                .schemaVersion(2) // Must be bumped when the schema changes
                .migration(migration) // Migration to run instead of throwing an exception
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        realm = Realm.getDefaultInstance();

        mRequestQueue = Volley.newRequestQueue(this);

        if (ParseUser.getCurrentUser() != null) {
            JSONObject userJson = new JSONObject();
            try {
                userJson.put("__type", "Pointer");
                userJson.put("className", "_User");
                userJson.put("objectId", ParseUser.getCurrentUser().getObjectId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            user = new JSONObject();
            try {
                user.put("user", userJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
