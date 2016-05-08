package net.glassstones.bambammusic.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.apradanas.simplelinkabletext.Link;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.intefaces.OnCommentInteraction;
import net.glassstones.bambammusic.intefaces.OnLikeListener;
import net.glassstones.bambammusic.models.Comment;
import net.glassstones.bambammusic.ui.adapters.viewholders.CommentHolder;
import net.glassstones.bambammusic.ui.adapters.viewholders.EmptyCommentHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Thompson on 21/04/2016.
 * For BambamMusic
 */
public class CommentsAdapter extends Adapter {
    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_HAS = 1;

    private List<Comment> mComments;
    private Context mContext;
    private OnCommentInteraction mListener;

    private View.OnClickListener likeOnClickListener = new OnLikeListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public void setListener(OnCommentInteraction l) {
        this.mListener = l;
    }

    public CommentsAdapter(List<Comment> comments, Context c) {
        this.mComments = comments;
        this.mContext = c;
    }

    @Override
    public int getItemViewType(int position) {
        if (mComments.size() < 1) {
            return TYPE_EMPTY;
        }
        return TYPE_HAS;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_EMPTY == viewType){
            View v = LayoutInflater.from(mContext).inflate(R.layout.empty_comments_layout, parent, false);
            return new EmptyCommentHolder(v);
        } else if (TYPE_HAS == viewType){
            View v = LayoutInflater.from(mContext).inflate(R.layout.comment_layout, parent, false);
            return new CommentHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentHolder){
            CommentHolder vh = (CommentHolder) holder;
            Comment comment = mComments.get(position);
            vh.getAvatar().setImageURI(Uri.parse(comment.getmAvatar()));
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

            vh.getCommentTV().setText(comment.getmUsername()+" "+comment.getmComment()).addLinks(links).build();
        }
    }

    @Override
    public int getItemCount() {
        if (mComments.size() < 1) {
            return 1;
        }
        return mComments.size();
    }

    public int add(Comment comment) {
        mComments.add(mComments.size(), comment);
        notifyItemInserted(mComments.size());
        return mComments.size();
    }

    public void change(int pos, Comment comment) {
        notifyItemChanged(pos, comment);
    }
}
