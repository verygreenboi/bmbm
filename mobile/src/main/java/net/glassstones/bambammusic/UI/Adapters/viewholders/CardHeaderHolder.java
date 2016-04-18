package net.glassstones.bambammusic.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import net.glassstones.bambammusic.R;
import net.glassstones.library.ui.widget.CustomFontTextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Thompson on 02/04/2016.
 * For BambamMusic
 */
public class CardHeaderHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.ccImg)
    ImageView ccImg;
    @Bind({R.id.ccLabel, R.id.ccStatus})List<CustomFontTextView> mTexts;

    public CardHeaderHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public ImageView getCcImg() {
        return ccImg;
    }

    public CustomFontTextView getCcLabel(){
        return mTexts.get(0);
    }

    public CustomFontTextView getCcStatus(){
        return mTexts.get(1);
    }
}
