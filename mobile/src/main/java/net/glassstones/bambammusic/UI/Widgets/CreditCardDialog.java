package net.glassstones.bambammusic.ui.widgets;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import net.glassstones.bambammusic.R;

/**
 * Created by Thompson on 04/04/2016.
 * For BambamMusic
 */
public class CreditCardDialog extends DialogPreference {

    public CreditCardDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(false);
        setDialogLayoutResource(R.layout.cc_input_layout);
    }

}
