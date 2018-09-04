package co.infinum.showsapp.Database;

import java.util.List;

import co.infinum.showsapp.Entities.Episode;

/**
 * Created by Ivan Lovrencic on 30.7.2018..
 */

public interface EpisodeRepository {

    void getEpisodes(DatabaseCallback<List<Episode>> callback);
    void insertEpisodes(List<Episode> episodes,DatabaseCallback<Void> callback);
    void getEpisode(DatabaseCallback<Episode> callback);
}
