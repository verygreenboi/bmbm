package net.glassstones.bambammusic.models;

import com.parse.ParseException;

/**
 * Created by Thompson on 21/04/2016.
 * For BambamMusic
 */
public class IntentServiceResult {
    int mResult;
    String mTunes;
    boolean isSaved;
    Exception exception;

    public IntentServiceResult(int resultOk, String tunes) {
        mResult = resultOk;
        mTunes = tunes;
    }

    public IntentServiceResult(int tuneSaveStatusOk, Boolean isSaved) {
        this.mResult = tuneSaveStatusOk;
        this.isSaved = isSaved;
    }

    public IntentServiceResult(int tuneGetFailure, Exception e) {
        this.mResult = tuneGetFailure;
        this.exception = e;
    }

    public int getmResult() {
        return mResult;
    }

    public String getmTunes() {
        return mTunes;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public Exception getException() {
        return exception;
    }
}
