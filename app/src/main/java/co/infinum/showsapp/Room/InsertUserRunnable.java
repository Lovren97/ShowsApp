package co.infinum.showsapp.Room;

import android.content.Context;
import android.icu.util.IslamicCalendar;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;

import java.util.List;

import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Comment;
import co.infinum.showsapp.Entities.User;

/**
 * Created by Ivan Lovrencic on 17.8.2018..
 */

public class InsertUserRunnable implements Runnable {

    private final DatabaseCallback<Void> callback;
    private final Context context;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final List<User> users;

    public InsertUserRunnable(DatabaseCallback<Void> callback,Context context,List<User> users){
        this.callback = callback;
        this.context = context;
        this.users = users;
    }

    @Override
    public void run() {
        try {
            RoomDatabaseFactory.db(context).userDao().insertUsers(users);
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
