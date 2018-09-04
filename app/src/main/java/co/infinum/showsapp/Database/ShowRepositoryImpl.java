package co.infinum.showsapp.Database;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.infinum.showsapp.Entities.Show;
import co.infinum.showsapp.Room.GetShowRunnable;
import co.infinum.showsapp.Room.InsertShowRunnable;

/**
 * Created by Ivan Lovrencic on 29.7.2018..
 */

public class ShowRepositoryImpl implements ShowRepository {

    private final Context context;
    private final ExecutorService executorService;
    private Future task;

    public ShowRepositoryImpl(Context context){
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void getShows(DatabaseCallback<List<Show>> callback) {
        cancel();
        this.task = executorService.submit(new GetShowRunnable(context,callback));
    }

    @Override
    public void insertShow(Show show, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executorService.submit(new InsertShowRunnable(context,show,callback));
    }

    private void cancel(){
        if(task != null && !task.isDone()){
            task.cancel(true);
        }
    }
}
