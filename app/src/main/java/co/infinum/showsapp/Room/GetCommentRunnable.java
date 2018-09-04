package co.infinum.showsapp.Room;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Comment;

/**
 * Created by Ivan Lovrencic on 15.8.2018..
 */

public class GetCommentRunnable implements Runnable {

    private final DatabaseCallback<List<Comment>> callback;
    private final Context context;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private String episodeId;

    public GetCommentRunnable(DatabaseCallback<List<Comment>> callback,Context context,String episodeId){
        this.callback = callback;
        this.context = context;
        this.episodeId = episodeId;
    }


    @Override
    public void run() {
        try{
            final List<Comment> comments = RoomDatabaseFactory.db(context).commentDao().getAllComments(episodeId);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(comments);
                }
            });
        }catch (final Exception e){
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onError(e);
                }
            });
        }
    }
}
