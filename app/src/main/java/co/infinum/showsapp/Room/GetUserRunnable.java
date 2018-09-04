package co.infinum.showsapp.Room;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Looper;

import java.util.List;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Episode;
import co.infinum.showsapp.Entities.User;

/**
 * Created by Ivan Lovrencic on 17.8.2018..
 */

public class GetUserRunnable implements Runnable {
    private final DatabaseCallback<List<User>> callback;
    private final Context context;
    private final android.os.Handler mainHandler = new android.os.Handler(Looper.getMainLooper());
    private String email;

    public GetUserRunnable(DatabaseCallback<List<User>> callback,Context context,String email){
        this.callback = callback;
        this.context = context;
        this.email = email;
    }

    @Override
    public void run() {
        try{
            final List<User> users = RoomDatabaseFactory.db(context).userDao().getUser(email);
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(users);
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
