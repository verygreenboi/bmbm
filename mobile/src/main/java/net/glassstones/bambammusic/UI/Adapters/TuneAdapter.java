package net.glassstones.bambammusic.ui.adapters;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.apradanas.simplelinkabletext.Link;
import com.apradanas.simplelinkabletext.LinkableTextView;
import com.ocpsoft.pretty.time.PrettyTime;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.OnCommentInteraction;
import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.ui.adapters.viewholders.MusicViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TuneAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_MUSIC = 0;
    private static final int TYPE_VOICE = 1;

    List<Tunes> mTunes;
    Context mContext;
    boolean isCurrentUser = true;

    OnCommentInteraction mListener;
    private View.OnClickListener commentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            Tunes t = mTunes.get(pos);
            mListener.onNewComment(t);
        }
    };
    private View.OnClickListener likeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public TuneAdapter(List<Tunes> tunes, Context context, boolean isCurrentUser) {
        this.mTunes = tunes;
        this.mContext = context;
        this.isCurrentUser = isCurrentUser;
    }

    public void setListener(OnCommentInteraction l) {
        this.mListener = l;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (TYPE_MUSIC == viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.music_item_layout, parent, false);
            return new MusicViewHolder(v);
        } else if (TYPE_VOICE == viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.voice_item_layout, parent, false);
            return new VoiceViewHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof MusicViewHolder) {
            MusicViewHolder vh = (MusicViewHolder) holder;
            bindMusicHolder(vh, position);
        } else if (holder instanceof VoiceViewHolder) {
            VoiceViewHolder vh = (VoiceViewHolder) holder;
            bindVoiceHolder(vh);
        }
    }

    private void bindVoiceHolder(VoiceViewHolder vh) {

    }

    private void bindMusicHolder(MusicViewHolder vh, int position) {
        if (mTunes.get(position).getArtUrl() != null) {
            Tunes t = mTunes.get(position);
            Uri picUri = Uri.parse(t.getArtUrl().trim());
            try {
                vh.getmArt().setImageURI(picUri);
            } catch (Exception e) {
                Toast.makeText(mContext, mTunes.get(position).getArtUrl(), Toast.LENGTH_LONG).show();
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            vh.getUserName().setText(t.getArtistName());
            PrettyTime p = new PrettyTime();
            vh.getCreatedAt().setText(p.format(t.getCreatedAt()));
            vh.getCreatedAt().setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            vh.getTitle().setText(t.getTitle());
            vh.getArtist().setText(t.getArtistName());
            if (t.isForSale()) {
                vh.getLikeMetaImg().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_add_shopping_cart));
            } else {
                vh.getLikeMetaImg().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_outline_white));
            }
            vh.getLikeMetaImg().setTag(position);
            vh.getLikeMetaImg().setOnClickListener(likeOnClickListener);

            vh.getCommentMetaImg().setTag(position);
            vh.getCommentMetaImg().setOnClickListener(commentOnClickListener);

            if (t.getmComments() != null && t.getCommentsCount() > 0) {
                setComments(vh, t);
            } else {
                vh.getCommentOne().setVisibility(View.GONE);
                vh.getCommentTwo().setVisibility(View.GONE);
                vh.getCommentThree().setVisibility(View.GONE);
            }

        }
    }

    private void setComments(MusicViewHolder vh, Tunes t) {
        int x = 0;
        try {
            while (x < 2) {
                setValues(vh.getCommentOne(), t.getmComments().get(t.getCommentsCount() - (3 - x)));
                x++;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            switch (x) {
                case 0:
                    setValues(vh.getCommentOne(), null);
                    break;
                case 1:
                    setValues(vh.getCommentTwo(), null);
                    break;
                default:
                    setValues(vh.getCommentThree(), null);
                    break;
            }
            e.printStackTrace();
        }
    }

    private void setValues(LinkableTextView t, Comment comment) {

        if (comment != null && !comment.getmComment().isEmpty() && !comment.getmUsername().isEmpty()) {
            /*
        **  define rules
        */

            // find hashtags
            Link linkHashtag = new Link(Pattern.compile("(#\\w+)"))
                    .setUnderlined(true)
                    .setTextStyle(Link.TextStyle.ITALIC)
                    .setClickListener(new Link.OnClickListener() {
                        @Override
                        public void onClick(String text) {
                            mListener.onHashTagClick(text);
                        }
                    });

            // find callout
            Link linkCallout = new Link(Pattern.compile("(@\\w+)"))
                    .setUnderlined(false)
                    .setTextColor(Color.parseColor("#D00000"))
                    .setTextStyle(Link.TextStyle.BOLD)
                    .setClickListener(new Link.OnClickListener() {
                        @Override
                        public void onClick(String text) {
                            mListener.onCalloutClick(text);
                        }
                    });
            // find username
            Link linkUsername = new Link(comment.getmUsername())
                    .setUnderlined(false)
                    .setTextColor(Color.BLUE)
                    .setTextStyle(Link.TextStyle.BOLD_ITALIC)
                    .setClickListener(new Link.OnClickListener() {
                        @Override
                        public void onClick(String text) {
                            mListener.userProfileClick(text);
                        }
                    });
            List<Link> links = new ArrayList<>();
            links.add(linkHashtag);
            links.add(linkUsername);
            links.add(linkCallout);
            t.setText(comment.getmUsername() + " " + comment.getmComment()).addLinks(links).build();
        } else {
            t.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        assert mTunes != null;
        return mTunes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mTunes.get(position).getMediaType() == 0) {
            return TYPE_MUSIC;
        } else {
            return TYPE_VOICE;
        }
    }

    public void updateAll(List<Tunes> tunes) {
        mTunes = tunes;
        notifyDataSetChanged();
    }

    protected class VoiceViewHolder extends ViewHolder {
        @Bind(R.id.title)
        TextView mTitle;

        public VoiceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public TextView getmTitle() {
            return mTitle;
        }
    }
}
