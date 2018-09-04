package co.infinum.showsapp.Database;

import android.content.Context;
import android.renderscript.ScriptGroup;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.infinum.showsapp.Entities.Comment;
import co.infinum.showsapp.Room.GetCommentRunnable;
import co.infinum.showsapp.Room.InsertCommentRunnable;

/**
 * Created by Ivan Lovrencic on 15.8.2018..
 */

public class CommentRepositoryImpl implements CommentRepository {

    private final Context context;
    private final ExecutorService executorService;
    private Future task;
    private String episodeId;

    public CommentRepositoryImpl(Context context,String episodeId){
        this.context = context;
        this.episodeId = episodeId;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void getComments(DatabaseCallback<List<Comment>> callback) {
        cancel();
        this.task = executorService.submit(new GetCommentRunnable(callback,context,episodeId));
    }

    @Override
    public void insertComments(List<Comment> comments, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executorService.submit(new InsertCommentRunnable(callback,context,comments));
    }

    private void cancel(){
        if(task != null && !task.isDone()){
            task.cancel(true);
        }
    }
}
