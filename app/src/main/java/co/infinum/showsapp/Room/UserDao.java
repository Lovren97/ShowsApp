package co.infinum.showsapp.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.infinum.showsapp.Entities.User;

/**
 * Created by Ivan Lovrencic on 17.8.2018..
 */

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE email IS (:email)")
    List<User> getUser(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUsers(List<User> users);
}
