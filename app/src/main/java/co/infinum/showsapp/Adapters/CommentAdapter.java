package co.infinum.showsapp.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.infinum.showsapp.Entities.Comment;
import co.infinum.showsapp.R;

/**
 * Created by Ivan Lovrencic on 14.8.2018..
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<Comment> comments;

    public CommentAdapter(List<Comment> comments){
        this.comments = comments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_comment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        TextView user_email = holder.itemView.findViewById(R.id.user_email);
        TextView user_comment = holder.itemView.findViewById(R.id.user_comment);

        user_email.setText(comment.getUser_email());
        user_comment.setText(comment.getUser_comment());
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    public void setComments(List<Comment> comments){
        this.comments = comments;
        notifyDataSetChanged();
    }
}
