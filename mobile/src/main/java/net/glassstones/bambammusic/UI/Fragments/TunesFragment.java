package net.glassstones.bambammusic.ui.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseUser;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.OnCommentInteraction;
import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.models.IntentServiceResult;
import net.glassstones.bambammusic.models.TuneFetchModel;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.tasks.GetTunesTask;
import net.glassstones.bambammusic.tasks.GetTunesTask.OnTunesFetched;
import net.glassstones.bambammusic.ui.activities.AddCommentActivity;
import net.glassstones.bambammusic.ui.adapters.TuneAdapter;
import net.glassstones.bambammusic.utils.helpers.AppPreferences;
import net.glassstones.bambammusic.utils.helpers.TuneHelper;
import net.glassstones.library.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class TunesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        RealmChangeListener, OnCommentInteraction, OnTunesFetched {
    private static final String PARSE_ID = "parse_id";
    private static final String IS_CURRENT_USER = "is_current_user";
    private static final String TAG = TunesFragment.class.getSimpleName();
    @Bind(R.id.rv_tunes)
    RecyclerView mRecycler;
    @Bind(R.id.srv_refresh)
    SwipeRefreshLayout srvRefresh;
    boolean isFresh;
    private String mParseId;
    private boolean mCurrentUser;
    private Realm r;
    private TuneAdapter adapter;
    private List<Tunes> tunesList = new ArrayList<>();
    private AppPreferences ap;

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

        ap = new AppPreferences(getActivity());

        RealmResults<Tunes> tx = r.where(Tunes.class).findAll();
        tx.sort("createdAt", Sort.DESCENDING);

        for (Tunes t : tx) {
            tunesList.add(t);
        }

        Log.e(TAG, String.valueOf(tunesList.size()));

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

        if (tunesList.size() < 1) {
            GetTunesTask task = new GetTunesTask(getActivity(), false);
            task.setListener(this);
            task.execute(ParseUser.getCurrentUser());
        }
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
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onRefresh() {
        GetTunesTask task = new GetTunesTask(getActivity(), true);
        task.setListener(this);
        task.execute(ParseUser.getCurrentUser());
    }

    @Override
    public void onChange() {
        adapter.notifyDataSetChanged();
        mRecycler.smoothScrollToPosition(0);
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
        Intent nn = new Intent(getActivity(), AddCommentActivity.class);
        nn.putExtra("tune_extra", tune.getParseId());
        getActivity().startActivity(nn);
    }

    @Override
    public void onCreateComment(Comment comment, Tunes tunes) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void doThis(IntentServiceResult intentServiceResult) {
        switch (intentServiceResult.getmResult()) {
            case Activity.RESULT_OK:
                streamToTop(intentServiceResult);
                break;
            case Constants.TUNE_GET_FAILURE:
                Snackbar.make(mRecycler, "Cannot get tunes", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTunesFetched(TuneFetchModel tfm) {
        List<Tunes> t = tfm.getTunes();
        boolean top = tfm.isStreamToTop();
        if (adapter != null) {
            LogHelper.e(TAG, t.size() + " tune(s) fetched.");
            adapter.updateAll(t, top);
            ap.setTunelineIsFresh(Constants.KEY_TUNELINE_STATUS, t.size() <= 0);
        }
    }

    @Override
    public void tunesFetched(List<Tunes> tunes, boolean streamToTop) {
        srvRefresh.setRefreshing(false);
        LogHelper.e(TAG, tunes.size() + " tunes fetched");
        adapter.updateAll(tunes, streamToTop);
    }

    private void streamToTop(IntentServiceResult intentServiceResult) {
        if (intentServiceResult.getTunes() == null) {
            try {
                List<Tunes> t = getTune(intentServiceResult);
                for (Tunes tt : t) {
                    adapter.add(tt, true);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }
        } else {
            adapter.add(intentServiceResult.getTunes(), true);
        }
    }

    private List<Tunes> getTune(IntentServiceResult intentServiceResult) throws JSONException, ParseException {
        JSONArray tunes = new JSONArray(intentServiceResult.getmTunes());
        List<Tunes> tx = new ArrayList<>();
        for (int i = 0; i < tunes.length(); i++) {
            Tunes t = TuneHelper.addTune(tunes.getJSONObject(i));
            Tunes c = r.where(Tunes.class).equalTo("parseId", t.getParseId()).findFirst();
            if (c != null)
                tx.add(t);
        }
        return tx;
    }

    @Override
    public void tunesFetchStarted() {
        srvRefresh.setRefreshing(true);
    }
}
