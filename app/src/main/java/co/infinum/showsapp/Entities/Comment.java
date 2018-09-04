package co.infinum.showsapp.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

/**
 * Created by Ivan Lovrencic on 14.8.2018..
 */

@Entity
public class Comment {

    @Json(name = "userEmail")
    @ColumnInfo(name = "email")
    private String user_email;

    @Json(name = "text")
    @ColumnInfo(name = "comment")
    private String user_comment;

    @Json(name = "episodeId")
    @ColumnInfo(name = "episodeId")
    @NonNull
    private String episodeId;

    @Json(name = "_id")
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String commentId;

    @Json(name = "type")
    @ColumnInfo(name = "type")
    private String type;

    @Json(name = "userId")
    @ColumnInfo(name = "userId")
    private String userId;

    public Comment(String user_email,String user_comment,String episodeId,String commentId,String type,String userId){
        this.user_email = user_email;
        this.user_comment = user_comment;
        this.episodeId = episodeId;
        this.commentId = commentId;
        this.type = type;
        this.userId = userId;
    }

    public Comment(String text,String episodeId){
        this.user_comment = text;
        this.episodeId = episodeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getEpisodeId() {
        return episodeId;
    }

    public String getUser_comment() {
        return user_comment;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public void setUser_comment(String user_comment) {
        this.user_comment = user_comment;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
