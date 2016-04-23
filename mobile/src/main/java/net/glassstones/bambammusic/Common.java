package net.glassstones.bambammusic;

import android.app.Application;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

import net.glassstones.library.utils.LogHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import co.paystack.android.PaystackSdk;
import io.realm.Realm;


public class Common extends Application {

    public static final String TAG = LogHelper.makeLogTag(Common.class);
    public static Common mInstance;
    private static Realm realm;
    private static JSONObject user;
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
        PaystackSdk.initialize(getApplicationContext());
        PaystackSdk.setPublishableKey(Constants.PUBLISHABLE_KEY);
        Fresco.initialize(getApplicationContext());

//        Parse.initialize(this, Constants.PARSE_APP_KEY, Constants.PARSE_CLIENT_KEY);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(Constants.PARSE_APP_KEY)
                .clientKey(Constants.PARSE_CLIENT_KEY)
                .server(Constants.SERVER_URL)   // '/' important after 'parse'
                .build());

        ParseFacebookUtils.initialize(this);

        ParseInstallation.getCurrentInstallation().saveInBackground();

        realm = Realm.getInstance(this);


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

    public class GsonRequest<T> extends Request<T> {
        private final Gson gson = new Gson();
        private final Class<T> clazz;
        private final Map<String, String> headers;
        private final Listener<T> listener;

        /**
         * Make a GET request and return a parsed object from JSON.
         *
         * @param url     URL of the request to make
         * @param clazz   Relevant class object, for Gson's reflection
         * @param headers Map of request headers
         */
        public GsonRequest(String url, Class<T> clazz, Map<String, String> headers,
                           Listener<T> listener, Response.ErrorListener errorListener) {
            super(Method.GET, url, errorListener);
            this.clazz = clazz;
            this.headers = headers;
            this.listener = listener;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return headers != null ? headers : super.getHeaders();
        }

        @Override
        protected void deliverResponse(T response) {
            listener.onResponse(response);
        }

        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            try {
                String json = new String(
                        response.data,
                        HttpHeaderParser.parseCharset(response.headers));
                return Response.success(
                        gson.fromJson(json, clazz),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JsonSyntaxException e) {
                return Response.error(new ParseError(e));
            }
        }
    }

}
