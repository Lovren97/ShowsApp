package co.infinum.showsapp.Room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import co.infinum.showsapp.Entities.Comment;
import co.infinum.showsapp.Entities.Episode;
import co.infinum.showsapp.Entities.Show;
import co.infinum.showsapp.Entities.User;

/**
 * Created by Ivan Lovrencic on 29.7.2018..
 */

@Database(
        entities = {
                Show.class,
                Episode.class,
                Comment.class,
                User.class
        },
        version = 3
)

public abstract class AppDatabase extends RoomDatabase {
    public abstract  ShowDao showDao();
    public abstract  EpisodeDao episodeDao();
    public abstract  CommentDao commentDao();
    public abstract UserDao userDao();
}
