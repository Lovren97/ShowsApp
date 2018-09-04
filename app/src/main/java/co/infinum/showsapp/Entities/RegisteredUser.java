package co.infinum.showsapp.Entities;

import com.squareup.moshi.Json;

/**
 * Created by Ivan Lovrencic on 26.7.2018..
 */

public class RegisteredUser {

    @Json(name = "type")
    private String type;
    @Json(name = "email")
    private String email;
    @Json(name = "_id")
    private String id;


    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
