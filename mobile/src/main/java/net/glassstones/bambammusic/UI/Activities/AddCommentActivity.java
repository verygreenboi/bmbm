package net.glassstones.bambammusic.ui.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.konifar.fab_transformation.FabTransformation;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.OnCommentInteraction;
import net.glassstones.bambammusic.intefaces.OnLikeListener;
import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.models.CommentData;
import net.glassstones.bambammusic.models.CommentPack;
import net.glassstones.bambammusic.models.IntentServiceResult;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.services.CreateCommentService;
import net.glassstones.bambammusic.services.CreateCommentService.CommentBinder;
import net.glassstones.bambammusic.ui.adapters.CommentsAdapter;
import net.glassstones.library.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import io.realm.Realm;
import io.realm.RealmList;

public class AddCommentActivity extends BaseActivity implements View.OnClickListener, OnCommentInteraction {

    private static final String TAG = AddCommentActivity.class.getSimpleName();
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.scrollableview)
    RecyclerView mRecycler;
    @Bind(R.id.fab_add_comment)
    FloatingActionButton mFab;
    @Bind(R.id.comment_lv)
    LinearLayout mCommentLayout;
    @Bind(R.id.btn_send_comment)
    Button mSendButton;
    @Bind(R.id.comment_et)
    EditText mCommentET;

    //    @OnClick({R.id.fab_add_comment, R.id.btn_send_comment})
//    void onAddComment(View v){
//        switch (v.getId()){
//            case R.id.fab_add_comment:
//                transformTo();
//                break;
//            case R.id.btn_send_comment:
//                transformFrom();
//                break;
//        }
//    }
    private List<Comment> mComments;
    private Comment comment;
    int pos;
    private CommentsAdapter adapter;
    private Realm realm;
    private ParseUser to;
    private Tunes t;
    private boolean mServiceBound = false;
    private CreateCommentService mBoundService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CommentBinder binder = (CommentBinder) service;
            mBoundService = binder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    private void transformTo() {
        FabTransformation.with(mFab).setListener(new FabTransformation.OnTransformListener() {
            @Override
            public void onStartTransform() {

            }

            @Override
            public void onEndTransform() {

            }
        }).transformTo(mCommentLayout);
    }

    private void transformFrom() {
        FabTransformation.with(mFab).setListener(new FabTransformation.OnTransformListener() {
            @Override
            public void onStartTransform() {

            }

            @Override
            public void onEndTransform() {
                sendComment();
            }
        }).transformFrom(mCommentLayout);
    }

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
        return true;
    }

    @Override
    public boolean hasIcon() {
        return false;
    }

    @Override
    public String title() {
        return "BamBam";
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
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        realm = Common.getRealm();


        if (getIntent().getStringExtra("tune_extra").isEmpty()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            init(getIntent().getStringExtra("tune_extra"));
            mFab.setOnClickListener(new OnLikeListener() {
                @Override
                public void onClick(View v) {
                    transformTo();
                }
            });
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transformFrom();
                }
            });
        }


    }

    private void sendComment() {
        EditText et = mCommentET;
        String commentText = et.getText().toString().trim();
        if (!commentText.isEmpty()) {
            comment = new Comment();
            comment.setC_index(new Date().getTime());
            comment.setmComment(commentText);
            comment.setmUsername(ParseUser.getCurrentUser().getUsername());
            comment.setmAvatar("https://graph.facebook.com/" + ParseUser.getCurrentUser().get("f_id") + "/picture?type=large");
            comment.setStatus(Constants.COMMENT_STATUS_CREATED);
            realm.beginTransaction();
            t.getmComments().add(comment);
            realm.commitTransaction();
            pos = adapter.add(comment);
            uploadComment(pos, comment, t);
            et.setText("");
        } else {
            Toast.makeText(this, "You can't send an empty comment", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadComment(final int pos, final Comment comment, Tunes t) {
        CommentPack commentPack = new CommentPack();
        commentPack.setComment(comment);
        commentPack.setPos(pos);
        commentPack.setTune(t);
        CommentData cd = new CommentData();
        cd.setComment(comment.getmComment());
        cd.setTuneId(t.getParseId());
        if (mServiceBound) {
            mBoundService.startSaveComment(cd, to);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this, CreateCommentService.class);
        if (!Common.getsInstance().isMyServiceRunning(CreateCommentService.class)) {
            startService(i);
            bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
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
        t = realm.where(Tunes.class).equalTo("parseId", id).findFirst();

        if (t != null) {
            RealmList<Comment> comments = t.getmComments();
            for (Comment cc : comments) {
                mComments.add(cc);
            }
            adapter = new CommentsAdapter(mComments, this);
            adapter.setListener(this);

            mRecycler.setLayoutManager(new LinearLayoutManager(this));
            mRecycler.setAdapter(adapter);

            mRecycler.smoothScrollToPosition(mComments.size());
            getParseUser(t.getArtistObjId());
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getCommentResponse(CreateCommentService.CommentResponse cr) {
        adapter.change(cr.getPos(), cr.getComment());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getActivitySave(IntentServiceResult i) {
        switch (i.getmResult()){
            case Constants.TUNE_SAVE_STATUS_OK:
                if (comment != null){
                    realm.beginTransaction();
                    comment.setStatus(Constants.COMMENT_STATUS_UPLOADED);
                    realm.commitTransaction();
                    adapter.notifyItemChanged(pos);
                }
                break;
            case Constants.SAVE_COMMENT_FAILURE:
            case Constants.TUNE_GET_FAILURE:
            case Constants.TUNE_ACTIVITY_SAVE_FAILURE:
                if (comment != null){
                        realm.beginTransaction();
                        comment.setStatus(Constants.COMMENT_STATUS_UPLOAD_FAILED);
                        realm.commitTransaction();
                        adapter.notifyItemChanged(pos);
                }
                break;
        }
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
                if (parseUser != null) {
                    EventBus.getDefault().post(parseUser);
                }
            }
        }.execute(artistObjId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getParseUser(ParseUser u) {
        LogHelper.e(TAG, u.getUsername());
        to = u;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void userProfileClick(String username) {
        LogHelper.e(TAG, username);
    }

    @Override
    public void onHashTagClick(String hashtag) {
        LogHelper.e(TAG, hashtag);
    }

    @Override
    public void onCalloutClick(String callout) {
        LogHelper.e(TAG, callout);
    }

    @Override
    public void onNewComment(Tunes tune) {

    }

    @Override
    public void onCreateComment(Comment comment, Tunes tunes) {

    }
}
