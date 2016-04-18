package net.glassstones.bambammusic.utils.helpers;

import android.content.Context;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Thompson on 15/04/2016.
 * For BambamMusic
 */
public class CalloutLink extends ClickableSpan {

    Context mContext;

    public CalloutLink(Context context){
        super();
        mContext = context;
    }

    @Override
    public void onClick(View widget) {
        TextView textView = (TextView) widget;
        Spanned spanned = (Spanned) textView.getText();
        int start = spanned.getSpanStart(this);
        int end = spanned.getSpanEnd(this);
        String theWord = spanned.subSequence(start + 1, end).toString();
    }
}
