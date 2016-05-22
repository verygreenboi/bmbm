package net.glassstones.bambammusic.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import net.glassstones.bambammusic.Constants;
import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.models.CommentData;
import net.glassstones.bambammusic.models.IntentServiceResult;
import net.glassstones.library.utils.LogHelper;

import org.greenrobot.eventbus.EventBus;

public class CreateCommentService extends Service {
    private static final String TAG = CreateCommentService.class.getSimpleName();
    private CommentData cd;
    private ParseObject pComment;
    private ParseObject pTune;
    private ParseUser to;
    private CommentBinder mBinder = new CommentBinder();
    private SaveCallback saveCommentCallback = new SaveCallback() {
        @Override
        public void done(ParseException e) {
            if (e == null && !pComment.isDirty()) {
                LogHelper.e(TAG, "Comment " + pComment.getObjectId() + " is saved.");
                uploadActivity(pComment, to);
            } else {
                EventBus.getDefault().post(new IntentServiceResult(Constants.SAVE_COMMENT_FAILURE, e));
                stopSelf();
            }
        }
    };
    private GetCallback<ParseObject> getTuneCallback = new GetCallback<ParseObject>() {
        @Override
        public void done(ParseObject tune, ParseException e) {
            pTune = tune;
            if (cd != null && to != null && e == null) {
                LogHelper.e(TAG, "Tune retrieved: " + tune.getObjectId());
                uploadComment(tune, cd);
            } else {
                EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_GET_FAILURE, e));
                stopSelf();
            }
        }
    };

    public CreateCommentService() {
    }

    private void uploadComment(ParseObject tune, CommentData cd) {
        pComment = new ParseObject("Comment");
        pComment.put("comment", cd.getComment());
        pComment.put("tune", tune);
        pComment.saveEventually(saveCommentCallback);
    }

    private void uploadActivity(ParseObject pComment, ParseUser to) {
        final ParseObject a = new ParseObject("Activities");
        a.put("type", "comment");
        a.put("from", ParseUser.getCurrentUser());
        a.put("to", to);
        a.put("comment", pComment);
        a.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_ACTIVITY_SAVE_FAILURE, e));
                } else {
                    EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_SAVE_STATUS_OK, pTune.getObjectId()));
                }
                stopSelf();
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void startSaveComment(CommentData commentData, ParseUser pTo) {
        to = pTo;
        cd = commentData;
        ParseQuery<ParseObject> getTuneAsync = new ParseQuery<>("Tunes");
        getTuneAsync.whereEqualTo("objectId", cd.getTuneId());
//        getTuneAsync.getFirstInBackground(getTuneCallback);
        getTuneAsync.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject tune, ParseException e) {
                pTune = tune;
                createComment(pTune, cd, to);
            }
        });
    }

    private void createComment(ParseObject pTune, CommentData cd, final ParseUser to) {
        final ParseObject comment = new ParseObject("Comment");
        comment.put("from", ParseUser.getCurrentUser());
        comment.put("tune", pTune);
        comment.put("to", to);
        comment.put("comment", cd.getComment());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    createActivity(comment, to);
                }
            }
        });
    }

    private void createActivity(ParseObject comment, ParseUser to) {
        ParseObject activity = new ParseObject("Activities");
        activity.put("from", ParseUser.getCurrentUser());
        activity.put("to", to);
        activity.put("type", "comment");
        activity.put("comment", comment);
        activity.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_SAVE_STATUS_OK, pTune.getObjectId()));
                } else {
                    EventBus.getDefault().post(new IntentServiceResult(Constants.TUNE_ACTIVITY_SAVE_FAILURE, e));
                }
            }
        });
    }

    public class CommentBinder extends Binder {

        public CreateCommentService getService() {
            return CreateCommentService.this;
        }
    }

    public class CommentResponse {

        int pos;
        Comment comment;

        public CommentResponse(int pos, Comment comment) {
            this.pos = pos;
            this.comment = comment;
        }

        public int getPos() {
            return pos;
        }

        public Comment getComment() {
            return comment;
        }

    }
}
