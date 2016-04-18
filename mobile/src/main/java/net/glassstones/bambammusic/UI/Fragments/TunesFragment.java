package net.glassstones.bambammusic.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.api.Tune;
import net.glassstones.bambammusic.intefaces.OnCommentInteraction;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.ui.adapters.TuneAdapter;
import net.glassstones.bambammusic.utils.MusicHelper;
import net.glassstones.bambammusic.utils.helpers.TuneHelper;
import net.glassstones.library.utils.LogHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;


public class TunesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        RealmChangeListener, Response.Listener<JSONObject>, Response.ErrorListener, OnCommentInteraction {
    private static final String PARSE_ID = "parse_id";
    private static final String IS_CURRENT_USER = "is_current_user";
    private static final String TAG = TunesFragment.class.getSimpleName();
    @Bind(R.id.rv_tunes)
    RecyclerView mRecycler;
    @Bind(R.id.srv_refresh)
    SwipeRefreshLayout srvRefresh;
    private String mParseId;
    private boolean mCurrentUser;
    private Realm r;
    private TuneAdapter adapter;
    private List<Tunes> tunesList = new ArrayList<>();

    public TunesFragment() {
        // Required empty public constructor
    }

    public static TunesFragment newInstance(String parseId, boolean isCurrentUser) {
        TunesFragment fragment = new TunesFragment();
        Bundle args = new Bundle();
        args.putString(PARSE_ID, parseId);
        args.putBoolean(IS_CURRENT_USER, isCurrentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParseId = getArguments().getString(PARSE_ID);
            mCurrentUser = getArguments().getBoolean(IS_CURRENT_USER);
        } else {
            getActivity().finish();
        }

        boolean isCurrentUser = mCurrentUser;
        r = Common.getRealm();
        r.addChangeListener(this);

        tunesList = r.where(Tunes.class).findAll();

        adapter = new TuneAdapter(tunesList, getActivity(), isCurrentUser);

        adapter.setListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tunes, container, false);
        ButterKnife.bind(this, view);

        srvRefresh.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (tunesList.size() == 0) {
//            srvRefresh.setRefreshing(true);
//        }
//        srvRefresh.post(
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        srvRefresh.setRefreshing(true);
//                        fetchTunes();
//                    }
//                }
//        );

        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecycler.setAdapter(adapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        r.removeAllChangeListeners();
    }

    @Override
    public void onRefresh() {
        fetchTunes();
    }

    private void fetchTunes() {
        srvRefresh.setRefreshing(true);
        Common.mInstance.getRequestQueue().add(MusicHelper.getTuneline(this, this));

    }

    @Override
    public void onChange() {
        tunesList = r.where(Tunes.class).findAll();
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray jsonTunes;
        if (srvRefresh.isRefreshing())
            srvRefresh.setRefreshing(false);
        try {
            jsonTunes = response.getJSONArray(Constants.KEY_TUNELINE_ARRAY_NAME);
            tunesList = TuneHelper.array2tune(jsonTunes);
            adapter.updateAll(tunesList);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, error.getMessage());
    }

    @Override
    public void userProfileClick(String username) {

    }

    @Override
    public void onHashTagClick(String hashtag) {

    }

    @Override
    public void onCalloutClick(String callout) {

    }

    @Override
    public void onNewComment(Tunes tune) {
        LogHelper.e(TAG, tune.getTitle());
    }
}
