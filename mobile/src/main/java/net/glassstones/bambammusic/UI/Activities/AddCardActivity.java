package net.glassstones.bambammusic.ui.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.FragmentIdentityListener;
import net.glassstones.bambammusic.ui.fragments.ListCardsFragment;

import butterknife.Bind;


public class AddCardActivity extends BaseActivity implements ListCardsFragment.OnListCardsFragmentInteractionListener, FragmentIdentityListener {

    private static final int LIST_FRAGMENT = 0;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    private int currentFragment;

    @Override
    public int contentResource() {
        return R.layout.activity_add_card;
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
        return true;
    }

    @Override
    public FloatingActionButton fab() {
        return mFab;
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

        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.container, new ListCardsFragment()).commit();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void fragmentIdentity(int id) {
        currentFragment = id;
        if (currentFragment == LIST_FRAGMENT) {
            if (mFab.getScaleX() == 0f || mFab.getScaleY() == 0f) {
                mFab.animate().rotation(360).scaleX(1f).scaleY(1f).setDuration(500).start();
            }
        } else {
            if (mFab.getScaleX() == 1f || mFab.getScaleY() == 1f) {
                mFab.animate().rotation(-360).scaleX(0f).scaleY(0f).setDuration(500).start();
            }
        }
    }
}
