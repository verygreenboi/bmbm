package net.glassstones.bambammusic.ui.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.FragmentIdentityListener;
import net.glassstones.bambammusic.intefaces.OnTrackFragmentInteractionListener;
import net.glassstones.bambammusic.intefaces.OnTrackItemClickListener;
import net.glassstones.bambammusic.models.MediaData;
import net.glassstones.bambammusic.ui.adapters.LocalTunesAdapter;
import net.glassstones.bambammusic.utils.MusicHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LocalTuneList extends Fragment implements SearchView.OnQueryTextListener, OnTrackItemClickListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 3;
    @Bind(R.id.rv_tune_list)
    RecyclerView mTLV;

    LocalTunesAdapter adapter;
    List<MediaData> mdl = new ArrayList<>();
    private OnTrackFragmentInteractionListener mListener;
    private FragmentIdentityListener mId;

    public LocalTuneList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int perm = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (perm != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }

        }


        mId.fragmentIdentity(0);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_local_tune_list, container, false);
        ButterKnife.bind(this, v);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        mTLV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mdl = MusicHelper.scanSdcard(getActivity());

        Log.e(LocalTuneList.class.getSimpleName(), String.valueOf(mdl.size()));

        adapter = new LocalTunesAdapter(getActivity(), mdl);

        adapter.setOnTrackItemClickListener(this);

        mTLV.setAdapter(adapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                mListener = (OnTrackFragmentInteractionListener) context;
                mId = (FragmentIdentityListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mId = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.choose_tune_menu, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        List<MediaData> filteredMediaData = filter(mdl, query);
        adapter.animateTo(filteredMediaData);
        mTLV.scrollToPosition(0);
        return true;
    }

    private List<MediaData> filter(List<MediaData> mdl, String query) {
        query = query.toLowerCase();
        List<MediaData> filteredMediaData = new ArrayList<>();

        for (MediaData m : mdl) {
            String title = m.getTitle().toLowerCase();
            String artist = m.getArtist().toLowerCase();
            if (title.contains(query) || artist.contains(query)) {
                filteredMediaData.add(m);
            }
        }

        return filteredMediaData;
    }

    @Override
    public void onTrackClick(MediaData track, int position) {
        mListener.trackFilePath(track);
        adapter.choose(position);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {
                    getActivity().finish();
                }

        }
    }
}
