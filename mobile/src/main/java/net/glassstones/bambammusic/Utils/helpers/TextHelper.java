package net.glassstones.bambammusic.utils.helpers;

import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;

import net.glassstones.bambammusic.Common;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.models.LocalCreditCard;

/**
 * Created by Thompson on 03/04/2016.
 * For BambamMusic
 */
public class TextHelper {

    public static String formatCreditCard(String cc, LocalCreditCard card){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < cc.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                result.append(" ");
            }

            result.append(cc.charAt(i));
        }
        return result.toString();
    }

    public static SpannableStringBuilder formatUserNameAndText(String userName, String text) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(userName);
        ForegroundColorSpan blueColorSpan = new ForegroundColorSpan(ContextCompat.getColor(Common.getsInstance().getApplicationContext(), R.color.md_blue_500));
        TypefaceSpan typefaceSpanMedium = new TypefaceSpan("sans-serif-medium");
        spannableStringBuilder.setSpan(typefaceSpanMedium, 0,
                spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(blueColorSpan, 0,
                spannableStringBuilder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append(" ");
        spannableStringBuilder.append(text);

        TypefaceSpan typefaceSpan = new TypefaceSpan("sans-serif");
        spannableStringBuilder.setSpan(typefaceSpan, spannableStringBuilder.length() - text.length(),
                spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan grayColorSpan = new ForegroundColorSpan(ContextCompat.getColor(Common.getsInstance().getApplicationContext(), R.color.md_grey_500));
        spannableStringBuilder.setSpan(grayColorSpan, spannableStringBuilder.length() - text.length(),
                spannableStringBuilder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableStringBuilder;
    }

}
