package co.infinum.showsapp.Database;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.infinum.showsapp.Entities.Episode;
import co.infinum.showsapp.Room.GetEpisodeInfoRunnable;
import co.infinum.showsapp.Room.GetEpisodeRunnable;
import co.infinum.showsapp.Room.InsertEpisodeRunnable;

/**
 * Created by Ivan Lovrencic on 30.7.2018..
 */

public class EpisodeRepositoryImpl implements EpisodeRepository {

    private final Context context;
    private final ExecutorService executorService;
    private Future task;
    private String showId;
    private String episodeId;

    public EpisodeRepositoryImpl(Context context,String showId){
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        this.showId = showId;
    }

    public EpisodeRepositoryImpl(String episodeId,Context context){
        this.context = context;
        this.episodeId = episodeId;
        this.executorService = Executors.newSingleThreadExecutor();
    }


    @Override
    public void getEpisodes(DatabaseCallback<List<Episode>> callback) {
        cancel();
        this.task = executorService.submit(new GetEpisodeRunnable(context,callback,showId));
    }

    @Override
    public void insertEpisodes(List<Episode> episodes, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executorService.submit(new InsertEpisodeRunnable(callback,context,episodes));
    }

    @Override
    public void getEpisode(DatabaseCallback<Episode> callback) {
        cancel();
        this.task = executorService.submit(new GetEpisodeInfoRunnable(callback,context,episodeId));
    }

    private void cancel(){
        if(task != null && !task.isDone()){
            task.cancel(true);
        }
    }
}
