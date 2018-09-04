package co.infinum.showsapp.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.squareup.moshi.Json;

/**
 * Created by Ivan Lovrencic on 25.7.2018..
 */

@Entity(primaryKeys = {"email","showId"})
public class User {

    @Json(name = "email")
    @NonNull
    @ColumnInfo(name = "email")
    private String email;

    @Json(name = "password")
    private String password;

    @NonNull
    @ColumnInfo(name = "showId")
    private String showId;

    @ColumnInfo(name = "showStatus")
    private String showStatus;

    @Ignore
    public User(String email,String password){
        this.email = email;
        this.password = password;
    }

    public User(String email,String showId,String showStatus){
        this.email = email;
        this.showId = showId;
        this.showStatus = showStatus;
    }

    @NonNull
    public String getShowId() {
        return showId;
    }

    public String getShowStatus() {
        return showStatus;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public void setShowId(@NonNull String showId) {
        this.showId = showId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setShowStatus(String showStatus) {
        this.showStatus = showStatus;
    }
}
