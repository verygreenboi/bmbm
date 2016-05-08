package net.glassstones.bambammusic.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.apradanas.simplelinkabletext.LinkableTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import net.glassstones.bambammusic.R;
import net.glassstones.library.ui.widget.CustomFontTextView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Thompson on 23/04/2016.
 * For BambamMusic
 */
public class CommentHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.avatar)
    SimpleDraweeView avatar;
    @Bind(R.id.tv_comment)
    LinkableTextView commentTV;
    @Bind(R.id.timestamp)
    CustomFontTextView timestamp;

    public CommentHolder(View v) {
        super(v);
        ButterKnife.bind(this, v);
    }

    public SimpleDraweeView getAvatar() {
        return avatar;
    }

    public LinkableTextView getCommentTV() {
        return commentTV;
    }

    public CustomFontTextView getTimestamp() {
        return timestamp;
    }
}
