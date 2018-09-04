package co.infinum.showsapp.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

/**
 * Created by Ivan Lovrencic on 16.7.2018..
 */

@Entity
public class Episode{

    @Json(name = "_id")
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @Json(name = "title")
    @ColumnInfo(name = "title")
    private String title;

    @Json(name = "description")
    @ColumnInfo(name = "description")
    private String description;

    @Json(name = "imageUrl")
    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    @Json(name = "episodeNumber")
    @ColumnInfo(name = "episodeNmb")
    private String episodeNmb;

    @Json(name = "season")
    @ColumnInfo(name = "season")
    private String season;

    @ColumnInfo(name = "showId")
    @Json(name = "showId")
    @NonNull
    private String showId;

    @Json(name = "mediaId")
    private String mediaId;

    public Episode(String id,String title,String description,String imageUrl,String episodeNmb,String season){
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.episodeNmb = episodeNmb;
        this.season = season;
        this.description = description;
    }

    public Episode(String showId,String mediaId,String title,String description,String episodeNmb,String season,boolean value){
        this.showId = showId;
        this.mediaId = mediaId;
        this.title = title;
        this.description = description;
        this.episodeNmb = episodeNmb;
        this.season = season;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getEpisodeNmb() {
        return episodeNmb;
    }

    public String getSeason() {
        return season;
    }

    @NonNull
    public String getShowId() {
        return showId;
    }

    public void setShowId(@NonNull String showId) {
        this.showId = showId;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public void setEpisodeNmb(String episodeNmb) {
        this.episodeNmb = episodeNmb;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
