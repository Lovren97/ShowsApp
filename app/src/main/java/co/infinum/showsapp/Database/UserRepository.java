package co.infinum.showsapp.Database;

import java.util.List;

import co.infinum.showsapp.Entities.User;

/**
 * Created by Ivan Lovrencic on 17.8.2018..
 */

public interface UserRepository {

    void getUsers(DatabaseCallback<List<User>> callback);
    void insertUsers(List<User> users,DatabaseCallback<Void> callback);
}
