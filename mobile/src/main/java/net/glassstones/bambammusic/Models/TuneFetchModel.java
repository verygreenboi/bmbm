package net.glassstones.bambammusic.models;

import java.util.List;

/**
 * Created by Thompson on 08/05/2016.
 * For BambamMusic
 */
public class TuneFetchModel {
    private List<Tunes> tunes;
    private boolean streamToTop;

    public TuneFetchModel(List<Tunes> tunes, boolean streamToTop) {
        this.tunes = tunes;
        this.streamToTop = streamToTop;
    }

    public List<Tunes> getTunes() {
        return tunes;
    }

    public void setTunes(List<Tunes> tunes) {
        this.tunes = tunes;
    }

    public boolean isStreamToTop() {
        return streamToTop;
    }

    public void setStreamToTop(boolean streamToTop) {
        this.streamToTop = streamToTop;
    }
}
