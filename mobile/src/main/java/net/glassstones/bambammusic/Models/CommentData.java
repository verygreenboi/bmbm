package net.glassstones.bambammusic.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Thompson on 24/04/2016.
 * For BambamMusic
 */
public class CommentData implements Parcelable {
    private long c_index;
    private String tuneId,comment;

    public CommentData() {
    }

    protected CommentData(Parcel in) {
        c_index = in.readLong();
        tuneId = in.readString();
        comment = in.readString();
    }

    public static final Creator<CommentData> CREATOR = new Creator<CommentData>() {
        @Override
        public CommentData createFromParcel(Parcel in) {
            return new CommentData(in);
        }

        @Override
        public CommentData[] newArray(int size) {
            return new CommentData[size];
        }
    };

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(c_index);
        dest.writeString(tuneId);
        dest.writeString(comment);
    }
}
