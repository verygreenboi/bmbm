package net.glassstones.bambammusic.models;

/**
 * Created by Thompson on 24/04/2016.
 * For BambamMusic
 */
public class CommentPack {
    int pos;
    Comment comment;
    Tunes tune;

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Tunes getTune() {
        return tune;
    }

    public void setTune(Tunes tune) {
        this.tune = tune;
    }
}
