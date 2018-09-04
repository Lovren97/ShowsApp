package co.infinum.showsapp.Room;

import android.content.Context;
import android.os.Looper;

import java.util.List;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Show;

/**
 * Created by Ivan Lovrencic on 29.7.2018..
 */

public class GetShowRunnable implements Runnable {

    private final DatabaseCallback<List<Show>> callback;
    private final Context context;
    private final android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());

    public GetShowRunnable(Context context,DatabaseCallback<List<Show>> callback){
        this.context = context;
        this.callback = callback;
    }


    @Override
    public void run() {
        try{
            final List<Show> shows = RoomDatabaseFactory.db(context).showDao().getAll();
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(shows);
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
