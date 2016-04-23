package net.glassstones.bambammusic.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.ui.adapters.CommentsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmList;

public class AddCommentActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.scrollableview)
    RecyclerView mRecycler;
    @Bind(R.id.fab_add_comment) FloatingActionButton mFab;
    @Bind({R.id.header})List<ImageView> mImages;

    private List<Comment> mComments;
    private CommentsAdapter adapter;

    private Realm realm;
    private ParseUser to;

    private Tunes t;

    @Override
    public int contentResource() {
        return R.layout.activity_add_comment;
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
        return null;
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
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Common.getRealm();


        if (getIntent().getStringExtra("tune_extra").isEmpty()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            init(getIntent().getStringExtra("tune_extra"));
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void init(String id) {

        mComments = new ArrayList<>();
        Tunes t = realm.where(Tunes.class).equalTo("parseId", id).findFirst();

        if (t != null){
            Picasso.with(this).load(t.getArtUrl()).into(mImages.get(0), new Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) mImages.get(0).getDrawable()).getBitmap();
                    handleLoadedBitmap(bitmap);
                }

                @Override
                public void onError() {

                }
            });
            RealmList<Comment> comments = t.getmComments();
            for (Comment cc : comments) {
                mComments.add(cc);
            }
            adapter = new CommentsAdapter(mComments, this);

            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRecycler.setAdapter(adapter);

            mRecycler.smoothScrollToPosition(mComments.size());
            getParseUser(t.getArtistObjId());
        }

    }

    private void handleLoadedBitmap(Bitmap bitmap) {
        if (bitmap != null){
            Palette.Builder builder = new Palette.Builder(bitmap);
            builder.generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    Palette.Swatch s = palette.getLightVibrantSwatch();
                    if (s != null) {
                        setSwatch(s);
                    } else {
                        Palette.Swatch s1 = palette.getVibrantSwatch();
                        if (s1 != null) {
                            setSwatch(s1);
                        }
                    }
                }
            });
        }
    }

    private void setSwatch(Palette.Swatch s) {
        TextView t = (TextView) mToolbar.findViewById(R.id.tb_title);
        t.setVisibility(View.VISIBLE);
        t.setTextColor(s.getTitleTextColor());
    }

    private void getParseUser(final String artistObjId) {
        new AsyncTask<String, Void, ParseUser>() {
            @Override
            protected ParseUser doInBackground(String... params) {
                ParseQuery<ParseUser> u = ParseUser.getQuery();
                u.whereEqualTo("objectId", params[0]);

                try {
                    return u.getFirst();
                } catch (ParseException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(ParseUser parseUser) {
                super.onPostExecute(parseUser);
                EventBus.getDefault().post(parseUser);
            }
        }.execute(artistObjId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getParseUser(ParseUser u) {

    }

    @Override
    public void onClick(View v) {

    }
}
