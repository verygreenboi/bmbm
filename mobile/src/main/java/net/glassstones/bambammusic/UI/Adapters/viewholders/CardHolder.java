package net.glassstones.bambammusic.ui.adapters.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by Thompson on 02/04/2016.
 * For BambamMusic
 */
public class CardHolder extends RecyclerView.ViewHolder {

    public CardHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
