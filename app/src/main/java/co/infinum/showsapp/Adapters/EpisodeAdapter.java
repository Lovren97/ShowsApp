package co.infinum.showsapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.infinum.showsapp.Entities.Episode;
import co.infinum.showsapp.R;

/**
 * Created by Ivan Lovrencic on 28.7.2018..
 */

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.ViewHolder> {

    private List<Episode> episodeList;
    private OnEpisodeClickListener onEpisodeClickListener;

    public EpisodeAdapter(List<Episode> episodes){
        this.episodeList = episodes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_episode,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EpisodeAdapter.ViewHolder holder, int position) {
        final Episode episode = episodeList.get(position);
        final TextView episodeTitle = holder.itemView.findViewById(R.id.episodeSeasonAndEpisode);
        final TextView seasonAndEpisode = holder.itemView.findViewById(R.id.seasonAndEpisode);

        episodeTitle.setText(episode.getTitle());
        seasonAndEpisode.setText("S"+episode.getSeason()+" "+"Ep"+episode.getEpisodeNmb());

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onEpisodeClickListener.onEpisodeClick(episode);
            }
        };

        holder.itemView.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setEpisodeList(List<Episode> episodeList){
        this.episodeList = episodeList;
        notifyDataSetChanged();
    }

    public interface OnEpisodeClickListener{
        void onEpisodeClick(Episode episode);
    }

    public void setListener(OnEpisodeClickListener onEpisodeClickListener){
        this.onEpisodeClickListener = onEpisodeClickListener;
    }


}
