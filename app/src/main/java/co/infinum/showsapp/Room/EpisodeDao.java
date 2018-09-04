package co.infinum.showsapp.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.infinum.showsapp.Entities.Episode;

/**
 * Created by Ivan Lovrencic on 30.7.2018..
 */

@Dao
public interface EpisodeDao {

    @Query("SELECT * FROM episode WHERE showId IS (:id)")
    List<Episode> getAll(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Episode> episodes);

    @Query("SELECT * FROM episode WHERE id IS (:id)")
    Episode getEpisode(String id);
}
