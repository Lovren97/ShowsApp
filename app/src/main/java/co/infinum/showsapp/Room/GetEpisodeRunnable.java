package co.infinum.showsapp.Room;

import android.content.Context;
import android.os.Looper;

import java.util.List;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Episode;

/**
 * Created by Ivan Lovrencic on 30.7.2018..
 */

public class GetEpisodeRunnable implements Runnable {

    private final DatabaseCallback<List<Episode>> callback;
    private final Context context;
    private final android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());
    private String showId;

    public GetEpisodeRunnable(Context context, DatabaseCallback<List<Episode>> callback,String showId){
        this.callback = callback;
        this.context = context;
        this.showId = showId;
    }

    @Override
    public void run() {
        try{
            final List<Episode> episodes = RoomDatabaseFactory.db(context).episodeDao().getAll(showId);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(episodes);
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
