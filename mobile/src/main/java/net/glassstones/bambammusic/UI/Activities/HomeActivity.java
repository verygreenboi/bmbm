package net.glassstones.bambammusic.ui.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.drawee.view.SimpleDraweeView;
import com.konifar.fab_transformation.FabTransformation;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.services.UpdateLocalTunesService;
import net.glassstones.bambammusic.ui.fragments.TunesFragment;
import net.glassstones.library.utils.LogHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;


public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int JOB_ID = 100;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @Bind(R.id.nvView)
    NavigationView nvDrawer;
    @Bind(R.id.rv_voice_overlay)
    RelativeLayout mVoiceOverlay;
    @Bind({R.id.menu_item_3, R.id.menu_item_4})
    List<com.github.clans.fab.FloatingActionButton> mVoiceMenus;
    @Bind({R.id.tv_choose_label, R.id.tv_record_label})
    List<TextView> mFabLabels;
    @Nullable
    @Bind(R.id.fab)
    com.github.clans.fab.FloatingActionMenu mFab;
    @Bind(R.id.container)
    FrameLayout mContainer;
    private JobScheduler jobScheduler;
    private ActionBarDrawerToggle drawerToggle;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @OnClick({R.id.menu_item, R.id.menu_item_2, R.id.menu_item_3, R.id.menu_item_4, R.id.rv_voice_overlay})
    void onFabClick(View v) {

        assert mFab != null;
        mFab.close(true);

        switch (v.getId()) {
            case R.id.menu_item:
                startActivity(new Intent(this, UploadTuneActivity.class));
                break;
            case R.id.menu_item_2:
                transformTo();
                break;
            case R.id.menu_item_3:
            case R.id.menu_item_4:
                transformFrom();
                break;
            case R.id.rv_voice_overlay:
                if (v.getVisibility() == View.VISIBLE) {
                    transformFrom();
                }
                break;

        }

    }

    private void transformFrom() {
        FabTransformation.with(mFab).setListener(new FabTransformation.OnTransformListener() {
            @Override
            public void onStartTransform() {
                doAnim(0f);
            }

            @Override
            public void onEndTransform() {

            }
        }).transformFrom(mVoiceOverlay);
    }

    private void transformTo() {
        FabTransformation.with(mFab).setListener(new FabTransformation.OnTransformListener() {
            @Override
            public void onStartTransform() {

            }

            @Override
            public void onEndTransform() {
                doAnim(1f);
            }
        }).transformTo(mVoiceOverlay);
    }

    private void doAnim(float v) {
        ObjectAnimator animScaleX1 = ObjectAnimator.ofFloat(mVoiceMenus.get(0), View.SCALE_X, v);
        ObjectAnimator animScaleX2 = ObjectAnimator.ofFloat(mVoiceMenus.get(1), View.SCALE_X, v);
        ObjectAnimator animScaleY1 = ObjectAnimator.ofFloat(mVoiceMenus.get(0), View.SCALE_Y, v);
        ObjectAnimator animScaleY2 = ObjectAnimator.ofFloat(mVoiceMenus.get(1), View.SCALE_Y, v);

        ObjectAnimator alpha1 = ObjectAnimator.ofFloat(mFabLabels.get(0), View.ALPHA, v);
        ObjectAnimator alpha2 = ObjectAnimator.ofFloat(mFabLabels.get(1), View.ALPHA, v);

        AnimatorSet animatorSet1 = new AnimatorSet();
        AnimatorSet animatorSet2 = new AnimatorSet();
        AnimatorSet animatorSet3 = new AnimatorSet();

        animatorSet1.playTogether(animScaleX1, animScaleY1, alpha1);
        animatorSet2.playTogether(animScaleX2, animScaleY2, alpha2);

        animatorSet3.playSequentially(animatorSet1, animatorSet2);

        animatorSet3.start();
    }

    private void constructJob() {
        JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(this, UpdateLocalTunesService.class));
        builder.setPeriodic(300000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true);
        jobScheduler.schedule(builder.build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        jobScheduler = JobScheduler.getInstance(this);
        if (ParseUser.getCurrentUser() != null) {
            constructJob();
            assert getSupportActionBar() != null;
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            for (TextView v : mFabLabels) {
                v.setAlpha(0f);
            }

            // Setup drawer view
            setupDrawerContent(nvDrawer);
            drawerToggle = setupDrawerToggle();
            mDrawer.setDrawerListener(drawerToggle);

            View headerView = getLayoutInflater().inflate(R.layout.nav_header, nvDrawer, false);

            nvDrawer.addHeaderView(headerView);

            SimpleDraweeView mAvatar = (SimpleDraweeView) headerView.findViewById(R.id.avatar);

            String id = AccessToken.getCurrentAccessToken().getUserId();
            String url = "https://graph.facebook.com/" + id + "/picture?type=large";

            mAvatar.setImageURI(Uri.parse(url));

            if (savedInstanceState == null) {
                FragmentManager fm = getSupportFragmentManager();
                fm.beginTransaction().replace(R.id.container, TunesFragment.newInstance("parse_id", true)).commit();
            }

        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void setupDrawerContent(NavigationView nvDrawer) {
        nvDrawer.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawer.closeDrawers();
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    private void selectDrawerItem(MenuItem menuItem) {
        LogHelper.e(TAG, menuItem.getTitle());
        switch (menuItem.getItemId()) {
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        int id = item.getItemId();

        if (id == R.id.action_signout) {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public int contentResource() {
        return R.layout.activity_home;
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
        return true;
    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public String title() {
        return "Home";
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
        return onClickListener;
    }

}
