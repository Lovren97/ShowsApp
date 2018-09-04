package co.infinum.showsapp.Room;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Show;

/**
 * Created by Ivan Lovrencic on 29.7.2018..
 */

public class InsertShowRunnable implements Runnable {
    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Show show;


    public InsertShowRunnable(Context context,Show show,DatabaseCallback<Void> callback){
        this.context = context;
        this.show = show;
        this.callback = callback;
    }

    @Override
    public void run() {
        try{
            RoomDatabaseFactory.db(context).showDao().insert(show);
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
