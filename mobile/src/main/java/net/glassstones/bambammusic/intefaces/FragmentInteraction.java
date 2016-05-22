package net.glassstones.bambammusic.intefaces;

import net.glassstones.bambammusic.models.MediaData;

/**
 * Created by Thompson on 18/12/2015.
 * For BambamMusic
 */
public interface FragmentInteraction {
    void sendMediaData(MediaData mediaData);

    void sendCurentPlayPosition(int currentPosition);
}
