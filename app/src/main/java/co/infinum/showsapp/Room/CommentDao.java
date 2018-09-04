package co.infinum.showsapp.Room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import co.infinum.showsapp.Entities.Comment;

/**
 * Created by Ivan Lovrencic on 15.8.2018..
 */

@Dao
public interface CommentDao {

    @Query("SELECT * FROM comment WHERE episodeId IS (:id)")
    List<Comment> getAllComments(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllComments(List<Comment> comments);

}
