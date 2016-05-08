package net.glassstones.bambammusic.intefaces;

import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.models.Tunes;

/**
 * Created by Thompson on 15/04/2016.
 * For BambamMusic
 */
public interface OnCommentInteraction {
    void userProfileClick(String username);

    void onHashTagClick(String hashtag);

    void onCalloutClick(String callout);

    void onNewComment(Tunes tune);

    void onCreateComment(Comment comment, Tunes tunes);
}
