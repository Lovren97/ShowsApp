package co.infinum.showsapp.Room;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Comment;
import co.infinum.showsapp.Entities.Episode;

/**
 * Created by Ivan Lovrencic on 15.8.2018..
 */

public class InsertCommentRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<Comment> comments;

    public InsertCommentRunnable(DatabaseCallback<Void> callback,Context context,List<Comment> comments){
        this.callback = callback;
        this.context = context;
        this.comments = comments;
    }


    @Override
    public void run() {
        try{
            RoomDatabaseFactory.db(context).commentDao().insertAllComments(comments);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(null);
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
