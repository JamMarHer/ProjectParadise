package paradise.ccclxix.projectparadise.RecyclerViewAdapters;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import paradise.ccclxix.projectparadise.R;

public class WaveCardViewHolder extends RecyclerView.ViewHolder{

    TextView waveName;
    ImageView waveThumbnail;
    ImageView waveActiveIndicator;
    ConstraintLayout generalLayout;
    View mView;

    public WaveCardViewHolder(View itemView) {
        super(itemView);
        waveName = itemView.findViewById(R.id.wave_single_card_name);
        waveThumbnail = itemView.findViewById(R.id.main_wave_thumbnail);
        waveActiveIndicator = itemView.findViewById(R.id.active_indicator);
        generalLayout = itemView.findViewById(R.id.wave_card_mini_layout);
        mView = itemView;
    }
}