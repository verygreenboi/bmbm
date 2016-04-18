package net.glassstones.bambammusic.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.models.LocalCreditCard;
import net.glassstones.bambammusic.ui.adapters.viewholders.CardHeaderHolder;
import net.glassstones.bambammusic.ui.adapters.viewholders.CardHolder;
import net.glassstones.bambammusic.utils.helpers.AppPreferences;
import net.glassstones.library.ui.widget.CustomFontTextView;

import java.util.List;

/**
 * Created by Thompson on 02/04/2016.
 * For BambamMusic
 */
public class CardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_MAIN = 1;
    private static final int TYPE_HEADER = 2;
    private Context context;
    private List<LocalCreditCard> cards;
    private AppPreferences sp;

    public CardsAdapter(List<LocalCreditCard> cards, Context context) {
        this.cards = cards;
        this.context = context;
        this.sp = new AppPreferences(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (cards.size() > 0)
            if (position == 0) {
                return TYPE_HEADER;
            } else {
                return TYPE_MAIN;
            }
        else
            return TYPE_EMPTY;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (TYPE_HEADER == viewType){
            v = LayoutInflater.from(context).inflate(R.layout.cards_item_header, parent, false);
            return new CardHeaderHolder(v);
        } else if (TYPE_MAIN == viewType){
            v = LayoutInflater.from(context).inflate(R.layout.cards_item_body, parent, false);
            return new CardHolder(v);
        } else {
            LayoutInflater.from(context).inflate(R.layout.cards_empty, parent, false);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CardHeaderHolder){
            CardHeaderHolder vh = (CardHeaderHolder) holder;
            bindHeader(vh);
        } else if (holder instanceof CardHolder){
            CardHolder vh = (CardHolder) holder;
            LocalCreditCard creditCard = cards.get(position);
            bindCard(vh, creditCard);
        }
    }

    private void bindHeader(CardHeaderHolder vh) {
        ImageView ccImg = vh.getCcImg();
        CustomFontTextView label = vh.getCcLabel();
        CustomFontTextView status = vh.getCcStatus();

        label.setText(sp.getSomeString("defaultCardNumber"));
        status.setText(sp.getSomeString("defaultCardStatus"));

        Drawable cc = null;

        switch (sp.getSomeString("defaultCardType")){
            case "MASTERCARD":
                cc = ContextCompat.getDrawable(context, R.drawable.ic_mastercard);
                break;
            case "VISA":
                cc = ContextCompat.getDrawable(context, R.drawable.ic_visa);
                break;
            case "AMEX":
                cc = ContextCompat.getDrawable(context, R.drawable.ic_amex);
                break;

        }

        try {
            ccImg.setImageDrawable(cc);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void bindCard(CardHolder vh, LocalCreditCard creditCard) {

    }

    @Override
    public int getItemCount() {
        if (cards.size() > 0)
            return cards.size() + 1;
        else
            return 1;
    }
}
