package net.glassstones.bambammusic.ui.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.glassstones.bambammusic.BuildConfig;
import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.FragmentInteraction;
import net.glassstones.bambammusic.intefaces.OnCommentInteraction;
import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.models.IntentServiceResult;
import net.glassstones.bambammusic.models.TuneFetchModel;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.services.PlayTuneService;
import net.glassstones.bambammusic.services.PlayTuneService.PlaySongBinder;
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
    private PlayTuneService mService;
    private FragmentInteraction mInteractionListener;
    private Handler h;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlaySongBinder binder = (PlaySongBinder) service;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

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
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mInteractionListener = (FragmentInteraction) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement FragmentInteraction");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(getActivity(), PlayTuneService.class);

        // Start Play service if it isn't already started
        if (!Common.getsInstance().isMyServiceRunning(PlayTuneService.class)) {
            getActivity().startService(intent);
        }

        // Bind service
        getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

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

        setTunes(isCurrentUser);

        setHasOptionsMenu(true);
    }

    private void setTunes(boolean isCurrentUser) {
        List<Tunes> tx1 = getTunesFromRealm();

        adapter = new TuneAdapter(tx1, getActivity(), isCurrentUser);

        adapter.setListener(this);
    }

    private List<Tunes> getTunesFromRealm() {
        RealmResults<Tunes> tx = r.where(Tunes.class).findAll();
        tx.sort("createdAt", Sort.DESCENDING);

        for (Tunes t : tx) {
            tunesList.add(t);
        }
        return tx;
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
            GetTunesTask task = new GetTunesTask(getActivity(), false, 0);
            task.setListener(this);
            task.execute(ParseUser.getCurrentUser());
        }
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecycler.setAdapter(adapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tuneline_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_clear) {
            if (BuildConfig.DEBUG) {
                RealmResults<Tunes> tunes = r.where(Tunes.class).findAll();
                r.beginTransaction();
                tunes.clear();
                r.commitTransaction();
                ParseQuery<ParseObject> tuneList = ParseQuery.getQuery("Tunes");
                tuneList.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        for (ParseObject o : objects) {
                            o.deleteInBackground();
                        }
                    }
                });
                ParseQuery<ParseObject> comments = ParseQuery.getQuery("Comment");
                comments.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        for (ParseObject o : objects) {
                            o.deleteInBackground();
                        }
                    }
                });
                ParseQuery<ParseObject> ac = ParseQuery.getQuery("Activities");
                ac.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, com.parse.ParseException e) {
                        for (ParseObject o : objects) {
                            o.deleteInBackground();
                        }
                    }
                });
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(mConnection);
        h = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        r.removeAllChangeListeners();
    }

    @Override
    public void onRefresh() {
        GetTunesTask task = new GetTunesTask(getActivity(), true, 0);
        task.setListener(this);
        task.execute(ParseUser.getCurrentUser());
    }


    @Override
    public void onChange() {
        adapter.updateAll(new ArrayList<Tunes>(), false);
        adapter.notifyDataSetChanged();
        adapter.updateAll(getTunesFromRealm(), false);
        adapter.notifyDataSetChanged();
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

    @Override
    public void playTune(Tunes t) {
        if (mService != null) {
            LogHelper.e(TAG, "Play");
            mService.setTune(t);
        }
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
