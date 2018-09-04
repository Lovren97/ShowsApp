package co.infinum.showsapp.Room;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Episode;

/**
 * Created by Ivan Lovrencic on 30.7.2018..
 */

public class InsertEpisodeRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<Episode> episodes;

    public InsertEpisodeRunnable(DatabaseCallback<Void> callback,Context context,List<Episode> episodes){
        this.callback = callback;
        this.context = context;
        this.episodes = episodes;
    }


    @Override
    public void run() {
        try{
            RoomDatabaseFactory.db(context).episodeDao().insertAll(episodes);
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
