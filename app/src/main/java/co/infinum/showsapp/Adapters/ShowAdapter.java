package co.infinum.showsapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import java.util.List;

import co.infinum.showsapp.Entities.Show;
import co.infinum.showsapp.MainActivity;
import co.infinum.showsapp.R;

/**
 * Created by Ivan Lovrencic on 27.7.2018..
 */

public class ShowAdapter extends RecyclerView.Adapter<ShowAdapter.ViewHolder> {

    private List<Show> showList;
    private OnShowClickListener onShowClickListener;
    private boolean layout;

    public ShowAdapter(List<Show> showList, boolean layout){
        this.showList = showList;
        this.layout = layout;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if(layout){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_show,parent,false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_show2,parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Show show = showList.get(position);
        final ImageView showImage = holder.itemView.findViewById(R.id.showPhoto);
        final TextView showTitle = holder.itemView.findViewById(R.id.MainTitle);

        Picasso.get().load(MainActivity.BASE_URL + show.getImageUrl()).into(showImage);
        showTitle.setText(show.getTitle());

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowClickListener.onShowClick(show);
            }
        };

        holder.itemView.setOnClickListener(listener);
    }

    public void setShowList(List<Show> shows){
        this.showList = shows;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return showList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private View itemView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }


    public void setListener(OnShowClickListener listener){
        this.onShowClickListener  = listener;
    }

    public interface OnShowClickListener{
        void onShowClick(Show show);
    }


}
