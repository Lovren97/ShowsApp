package co.infinum.showsapp.Room;

import android.content.Context;
import android.os.Looper;

import java.util.List;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Episode;

/**
 * Created by Ivan Lovrencic on 14.8.2018..
 */

public class GetEpisodeInfoRunnable implements Runnable {

    private final DatabaseCallback<Episode> callback;
    private final Context context;
    private final android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());
    private String episodeId;

    public GetEpisodeInfoRunnable(DatabaseCallback<Episode> callback,Context context,String episodeId){
        this.callback = callback;
        this.context = context;
        this.episodeId = episodeId;
    }

    @Override
    public void run() {
        try{
            final Episode episode = RoomDatabaseFactory.db(context).episodeDao().getEpisode(episodeId);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(episode);
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
