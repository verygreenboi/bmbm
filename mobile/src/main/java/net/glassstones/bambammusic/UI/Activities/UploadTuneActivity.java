package net.glassstones.bambammusic.ui.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.LinearInterpolator;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.services.UploadTuneService;
import net.glassstones.bambammusic.intefaces.FragmentIdentityListener;
import net.glassstones.bambammusic.intefaces.FragmentInteraction;
import net.glassstones.bambammusic.intefaces.OnTrackFragmentInteractionListener;
import net.glassstones.bambammusic.models.MediaData;
import net.glassstones.bambammusic.ui.fragments.ArtFragment;
import net.glassstones.bambammusic.ui.fragments.LocalTuneList;

import butterknife.Bind;

public class UploadTuneActivity extends BaseActivity implements OnTrackFragmentInteractionListener, FragmentIdentityListener, FragmentInteraction {

    public static final int PICK_ART_REQUEST = 0;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fab) FloatingActionButton mFab;

    private MediaData mediaData;
    private int currentFragment;

    @Override
    public int contentResource() {
        return R.layout.activity_upload_tune;
    }

    @Override
    public boolean hasToolBar() {
        return true;
    }

    @Override
    public Toolbar toolbar() {
        return mToolbar;
    }

    @Override
    public boolean hasTitle() {
        return false;
    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public String title() {
        return "";
    }

    @Override
    public Drawable icon() {
        return null;
    }

    @Override
    public boolean hasFab() {
        return false;
    }

    @Override
    public FloatingActionButton fab() {
        return null;
    }

    @Override
    public View.OnClickListener onClickListener() {
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null){
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.container, new LocalTuneList()).commit();
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentFragment == 0){
                    getImageUri();
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case PICK_ART_REQUEST:
                    mediaData.setImageUri(data.getData());
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().replace(R.id.container, ArtFragment.mArtFragment(mediaData)).addToBackStack(null).commit();
                    mFab.animate().rotation(-360).scaleX(0f).scaleY(0f).setInterpolator(new LinearInterpolator()).start();
                    break;
            }
        }
    }

    @Override
    public void trackFilePath(MediaData track) {
        mediaData = track;
//        mFab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_white));
        if (mFab.getScaleX() == 0f || mFab.getScaleY() == 0f) {
            mFab.animate().rotation(360).scaleX(1f).scaleY(1f).setDuration(500).start();
        }

    }

    @Override
    public void fragmentIdentity(int id) {
        currentFragment = id;
    }

    private void getImageUri() {
        Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setType("image/*");
        startActivityForResult(i, PICK_ART_REQUEST);
    }

    @Override
    public void sendMediaData(MediaData mediaData) {
        Intent i = new Intent(this, UploadTuneService.class);
        i.putExtra("mediaData", mediaData);
        startService(i);
        finish();
    }
}
