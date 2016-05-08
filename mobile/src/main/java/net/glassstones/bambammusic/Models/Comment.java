package net.glassstones.bambammusic.models;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@SuppressWarnings("unused")
public class Comment extends RealmObject {
    @PrimaryKey
    private long c_index;

    private String mComment, mUsername, mAvatar, tuneId;

    private Date mCreatedAt;

    private int status, type;

    public Comment() {
        this.c_index = new Date().getTime();
    }

    public Comment(String mComment, String mUsername, String mAvatar, Date mCreatedAt, int status) {
        this.mComment = mComment;
        this.mUsername = mUsername;
        this.mAvatar = mAvatar;
        this.mCreatedAt = mCreatedAt;
        this.status = status;
    }


    public Date getmCreatedAt() {
        return mCreatedAt;
    }

    public void setmCreatedAt(Date mCreatedAt) {
        this.mCreatedAt = mCreatedAt;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmAvatar() {
        return mAvatar;
    }

    public void setmAvatar(String mAvatar) {
        this.mAvatar = mAvatar;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getC_index() {
        return c_index;
    }

    public void setC_index(long c_index) {
        this.c_index = c_index;
    }

    public String getTuneId() {
        return tuneId;
    }

    public void setTuneId(String tuneId) {
        this.tuneId = tuneId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
