package co.infinum.showsapp.Database;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.infinum.showsapp.Entities.User;
import co.infinum.showsapp.Room.GetShowRunnable;
import co.infinum.showsapp.Room.GetUserRunnable;
import co.infinum.showsapp.Room.InsertUserRunnable;

/**
 * Created by Ivan Lovrencic on 17.8.2018..
 */

public class UserRepositoryImpl implements UserRepository {

    private final Context context;
    private final ExecutorService executorService;
    private Future task;
    private String email;

    public UserRepositoryImpl(Context context,String email){
        this.context = context;
        this.email = email;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void getUsers(DatabaseCallback<List<User>> callback) {
        cancel();
        this.task = executorService.submit(new GetUserRunnable(callback,context,email));
    }

    @Override
    public void insertUsers(List<User> users, DatabaseCallback<Void> callback) {
        cancel();
        this.task = executorService.submit(new InsertUserRunnable(callback,context,users));
    }

    private void cancel(){
        if(task != null && !task.isDone()){
            task.cancel(true);
        }
    }
}
