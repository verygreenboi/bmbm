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
    Tunes tunes;

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

    public IntentServiceResult(int resultOk, Tunes t) {
        mResult = resultOk;
        this.tunes = t;
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

    public Tunes getTunes() {
        return tunes;
    }

    public void setTunes(Tunes tunes) {
        this.tunes = tunes;
    }
}
