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
public class Show{


    @Json(name = "type")
    @ColumnInfo(name = "type")
    private String type;

    @Json(name = "title")
    @ColumnInfo(name = "title")
    private String title;

    @Json(name = "description")
    @ColumnInfo(name = "description")
    private String description;

    @Json(name = "_id")
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @Json(name = "likesCount")
    @ColumnInfo(name = "likes")
    private int likes;

    @Json(name = "imageUrl")
    @ColumnInfo(name = "imageUrl")
    private String imageUrl;

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public int getLikes() {
        return likes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
