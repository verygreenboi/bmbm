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
import com.facebook.drawee.view.SimpleDraweeView;
import com.ocpsoft.pretty.time.PrettyTime;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.OnCommentInteraction;
import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.models.Tunes;
import net.glassstones.bambammusic.ui.adapters.viewholders.MusicViewHolder;
import net.glassstones.library.utils.LogHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;

public class TuneAdapter extends Adapter<ViewHolder> {

    private static final int TYPE_MUSIC = 0;
    private static final int TYPE_VOICE = 1;
    private static final String TAG = TuneAdapter.class.getSimpleName();

    List<Tunes> mTunes;
    Context mContext;
    boolean isCurrentUser = true;
    int pos;


    Realm r = Common.getRealm();

    OnCommentInteraction mListener;
    private View.OnClickListener commentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            pos = (int) v.getTag();
            Tunes t = mTunes.get(pos);
            mListener.onNewComment(t);
        }
    };
    private View.OnClickListener likeOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            Tunes t = mTunes.get(pos);
            LogHelper.e(TAG, t.getTitle()+" liked");
            HashMap<String, String> params = new HashMap<>();
            params.put("tuneId", t.getParseId());
            if (!t.isLiked()) {
                doLikeFunction("likeTune", params);
            } else {
                doLikeFunction("dislikeTune",params);
            }
        }
    };

    private void doLikeFunction(final String key, HashMap<String, String> params) {
        ParseCloud.callFunctionInBackground(key, params, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                Tunes t = mTunes.get(pos);
                if (e == null){
                    switch (key){
                        case "likeTune":
                            r.beginTransaction();
                            t.setIsLiked(true);
                            r.commitTransaction();
                            break;
                        case "dislikeTune":
                            r.beginTransaction();
                            t.setIsLiked(false);
                            r.commitTransaction();
                            break;
                        default:
                            r.beginTransaction();
                            t.setIsLiked(false);
                            r.commitTransaction();
                            break;
                    }
                    notifyItemChanged(pos);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

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
            setArtistAvatar(t, vh.getUserAvatar());
            if (t.isForSale()) {
                vh.getLikeMetaImg().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_add_shopping_cart));
            } else {
                vh.getLikeMetaImg().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_outline_white));
            }
            vh.getLikeMetaImg().setTag(position);
            if (t.isLiked()){
                vh.getLikeMetaImg().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart));
            } else {
                vh.getLikeMetaImg().setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_heart_outline_white));
            }
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

    private void setArtistAvatar(Tunes t, final SimpleDraweeView userAvatar) {
        ParseQuery<ParseUser> user = ParseUser.getQuery();
        user.whereEqualTo("objectId", t.getArtistObjId());
        user.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    String fb_id = user.getString("fb_id");
                    String url = "https://graph.facebook.com/" + fb_id + "/picture?type=large";
                    userAvatar.setImageURI(Uri.parse(url));
                } else {
                    userAvatar.setImageURI(null);
                }
            }
        });
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

    public void updateAll(List<Tunes> tunes, boolean streamToTop) {
        for (Tunes t : tunes) {
            add(t, streamToTop);
        }
    }

    public void add(Tunes tune, boolean toTop) {
        if (!isTuneLocal(tune)) {
            addToRealm(tune);
            mTunes.add(toTop ? 0 : mTunes.size(), tune);
            notifyItemInserted(toTop ? 0 : mTunes.size());
        }
    }

    private void addToRealm(Tunes tune) {
        r.beginTransaction();
        r.copyToRealmOrUpdate(tune);
        r.commitTransaction();
    }

    private boolean isTuneLocal (Tunes t){
        Tunes tt = r.where(Tunes.class).equalTo("parseId", t.getParseId()).findFirst();
        return tt != null;
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
