package co.infinum.showsapp.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import java.util.List;

import co.infinum.showsapp.Entities.Show;

/**
 * Created by Ivan Lovrencic on 29.7.2018..
 */

@Dao
public interface ShowDao {

    @android.arch.persistence.room.Query("SELECT * FROM show")
    List<Show> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Show show);
}
