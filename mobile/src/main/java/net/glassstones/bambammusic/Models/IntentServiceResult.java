package net.glassstones.bambammusic.models;

/**
 * Created by Thompson on 21/04/2016.
 * For BambamMusic
 */
public class IntentServiceResult {
    int mResult;
    String mTunes;
    public IntentServiceResult(int resultOk, String tunes) {
        mResult = resultOk;
        mTunes = tunes;
    }

    public int getmResult() {
        return mResult;
    }

    public String getmTunes() {
        return mTunes;
    }
}
