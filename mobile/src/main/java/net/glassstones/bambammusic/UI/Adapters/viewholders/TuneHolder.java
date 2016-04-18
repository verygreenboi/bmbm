package net.glassstones.bambammusic.ui.adapters.viewholders;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.models.MediaData;
import net.glassstones.library.utils.UIUtils;

import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

@SuppressWarnings("unused")
public class TuneHolder extends RecyclerView.ViewHolder {

    private TextView tv_title, tv_artist, tv_duration;
    private RelativeLayout mRoot;


    public TuneHolder(View itemView) {
        super(itemView);
        mRoot = (RelativeLayout) itemView.findViewById(R.id.root);
        tv_title = (TextView) itemView.findViewById(R.id.title);
        tv_artist = (TextView) itemView.findViewById(R.id.artist);
        tv_duration = (TextView) itemView.findViewById(R.id.time);
        ButterKnife.bind(this, itemView);
    }

    public void bind(MediaData mediaData) {
        setTitle(tv_title, mediaData.getTitle());
        tv_artist.setText(mediaData.getArtist());
        tv_duration.setText(UIUtils.getTime(mediaData.getDuration(), Locale.US));

        if (mediaData.isChecked()) {
            mRoot.setBackgroundColor(ContextCompat.getColor(mRoot.getContext(), R.color.colorPrimary));
            tv_title.setTextColor(ContextCompat.getColor(mRoot.getContext(), R.color.white));
            tv_duration.setTextColor(ContextCompat.getColor(mRoot.getContext(), R.color.white));
            tv_artist.setTextColor(ContextCompat.getColor(mRoot.getContext(), R.color.secondary_text_dark));

        } else {
            mRoot.setBackgroundColor(ContextCompat.getColor(mRoot.getContext(), R.color.white));
            tv_title.setTextColor(ContextCompat.getColor(mRoot.getContext(), R.color.primary_colored_text));
            tv_duration.setTextColor(ContextCompat.getColor(mRoot.getContext(), R.color.secondary_colored_text));
            tv_artist.setTextColor(ContextCompat.getColor(mRoot.getContext(), R.color.secondary_text_light));
        }

    }

    private void setTitle(TextView tv_title, String title) {
        tv_title.setText(title);
    }

    public TextView getTv_title() {
        return tv_title;
    }

    public void setTv_title(TextView tv_title) {
        this.tv_title = tv_title;
    }

    public TextView getTv_artist() {
        return tv_artist;
    }

    public void setTv_artist(TextView tv_artist) {
        this.tv_artist = tv_artist;
    }

    public TextView getTv_duration() {
        return tv_duration;
    }

    public void setTv_duration(TextView tv_duration) {
        this.tv_duration = tv_duration;
    }

    public RelativeLayout getmRoot() {
        return mRoot;
    }

    public void setmRoot(RelativeLayout mRoot) {
        this.mRoot = mRoot;
    }
}
