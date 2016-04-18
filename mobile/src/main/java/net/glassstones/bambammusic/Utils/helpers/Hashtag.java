package net.glassstones.bambammusic.utils.helpers;

import android.content.Context;
import android.content.Intent;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import net.glassstones.bambammusic.intefaces.OnCommentInteraction;

/**
 * Created by Thompson on 15/04/2016.
 * For BambamMusic
 */
public class Hashtag extends ClickableSpan {

    Context mContext;

    OnCommentInteraction mListener;

    TextPaint mTextPaint;
    public Hashtag(Context ctx) {
        super();
        mContext = ctx;
    }

    public void setHashTagClickListener(OnCommentInteraction l){
        this.mListener = l;
    }

    @Override
    public void onClick(View widget) {
        TextView tv = (TextView) widget;
        Spanned s = (Spanned) tv.getText();
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);
        String theWord = s.subSequence(start + 1, end).toString();
        Intent i = new Intent();
        i.putExtra("HASHTAG", theWord);
        mListener.onHashTagClick(i);
    }
}
