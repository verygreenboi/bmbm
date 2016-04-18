package net.glassstones.bambammusic.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.glassstones.bambammusic.intefaces.OnTrackItemClickListener;
import net.glassstones.bambammusic.R;
import net.glassstones.bambammusic.models.MediaData;
import net.glassstones.bambammusic.ui.adapters.viewholders.TuneHolder;

import java.util.ArrayList;
import java.util.List;

public class LocalTunesAdapter extends RecyclerView.Adapter<TuneHolder> implements View.OnClickListener {

    Context mContext;
    List<MediaData> md;

    int chosen = -1;
    int previous = -1;

    private OnTrackItemClickListener onTrackItemClickListener;

    public LocalTunesAdapter(Context mContext, List<MediaData> md) {
        this.mContext = mContext;
        this.md = new ArrayList<>(md);
    }

    public void setOnTrackItemClickListener(OnTrackItemClickListener onTrackItemClickListener) {
        this.onTrackItemClickListener = onTrackItemClickListener;
    }

    @Override
    public TuneHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.local_tunes_layout, parent, false);
        TuneHolder th = new TuneHolder(v);
        th.getmRoot().setOnClickListener(this);
        return th;
    }

    @Override
    public void onBindViewHolder(TuneHolder holder, int position) {
        MediaData mediaData = md.get(position);
        holder.getmRoot().setTag(position);
        holder.bind(mediaData);
    }

    @Override
    public int getItemCount() {
        return md.size();
    }

    public void animateTo(List<MediaData> models) {

        md = models;

        for (MediaData m : md){
            m.setChecked(false);
            notifyItemChanged(md.indexOf(m));
        }

        notifyDataSetChanged();
    }

    public void choose (int position){

        for (MediaData m : md){
            if (md.indexOf(m) == position){
                m.setChecked(true);
            } else {
                m.setChecked(false);
            }
            notifyItemChanged(md.indexOf(m));
        }
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        onTrackItemClickListener.onTrackClick(md.get(position), position);
    }


}
