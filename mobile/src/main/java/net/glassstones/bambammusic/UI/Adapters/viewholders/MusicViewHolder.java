package net.glassstones.bambammusic.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.apradanas.simplelinkabletext.LinkableTextView;
import com.facebook.drawee.view.SimpleDraweeView;

import net.glassstones.bambammusic.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Thompson on 18/04/2016.
 * For BambamMusic
 */
public class MusicViewHolder extends ViewHolder {
    @Bind({R.id.iv_art, R.id.sdvImage})
    List<SimpleDraweeView> mArt;
    @Bind({R.id.tv_username, R.id.tv_time_ago, R.id.tv_title, R.id.tv_artist})
    List<TextView> mText;

    @Bind({R.id.iv_like, R.id.iv_comment})
    List<ImageView> metaImgList;

    @Bind({R.id.tv_cm_1, R.id.tv_cm_2, R.id.tv_cm_3})
    List<LinkableTextView> comments;

    public MusicViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public SimpleDraweeView getmArt() {
        return mArt.get(0);
    }

    public SimpleDraweeView getUserAvatar() {
        return mArt.get(1);
    }

    public TextView getUserName() {
        return mText.get(0);
    }

    public TextView getCreatedAt() {
        return mText.get(1);
    }

    public TextView getTitle() {
        return mText.get(2);
    }

    public TextView getArtist() {
        return mText.get(3);
    }

    public ImageView getLikeMetaImg() {
        return metaImgList.get(0);
    }
    public ImageView getCommentMetaImg() {
        return metaImgList.get(1);
    }

    public LinkableTextView getCommentOne() {
        return comments.get(0);
    }

    public LinkableTextView getCommentTwo() {
        return comments.get(1);
    }

    public LinkableTextView getCommentThree() {
        return comments.get(2);
    }
}
