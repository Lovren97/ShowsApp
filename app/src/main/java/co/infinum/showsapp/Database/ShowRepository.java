package co.infinum.showsapp.Database;

import java.util.List;

import co.infinum.showsapp.Entities.Show;

/**
 * Created by Ivan Lovrencic on 29.7.2018..
 */

public interface ShowRepository {

    void getShows(DatabaseCallback<List<Show>> callback);
    void insertShow(Show show, DatabaseCallback<Void> callback);
}
