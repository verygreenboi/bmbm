package net.glassstones.bambammusic.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.FragmentIdentityListener;
import net.glassstones.bambammusic.intefaces.FragmentInteraction;
import net.glassstones.bambammusic.models.MediaData;
import net.glassstones.bambammusic.utils.helpers.FileHelper;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ArtFragment extends Fragment {

    private static MediaData mMediaData;
    @Bind(R.id.title)
    EditText title;
    @Bind(R.id.desc)
    EditText desc;

    private FragmentIdentityListener mId;
    private FragmentInteraction mInteract;


    public ArtFragment() {
        // Required empty public constructor
    }


    public static ArtFragment mArtFragment(MediaData mediaData) {
        mMediaData = mediaData;
        return new ArtFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId.fragmentIdentity(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_art, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setHasOptionsMenu(true);

        byte[] mArtFileBytes = FileHelper.getByteArrayFromFile(getActivity(), mMediaData.getImageUri());
        BitmapDrawable bd = new BitmapDrawable(getActivity().getResources(), Bitmap.createScaledBitmap(FileHelper.resizeImageMaintainAspectRatio(
                mArtFileBytes, FileHelper.SHORT_SIDE_TARGET), 196, 196, true));

        title.setCompoundDrawablesWithIntrinsicBounds(bd, null, null, null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_done:
                if (!desc.getText().toString().equals("")){
                    mMediaData.setDesc(desc.getText().toString());
                }
                if (!title.getText().toString().equals("")) {
                    mMediaData.setTitle(title.getText().toString());
                    mInteract.sendMediaData(mMediaData);
                } else {
                    Snackbar.make(title, "Title cannot be empty.", Snackbar.LENGTH_LONG).show();
                    title.requestFocus();
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            try {
                mId = (FragmentIdentityListener) context;
                mInteract = (FragmentInteraction) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString()
                        + " must implement OnFragmentInteractionListener");
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mId = null;
        mInteract = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
