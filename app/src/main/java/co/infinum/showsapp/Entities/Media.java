package co.infinum.showsapp.Entities;

import com.squareup.moshi.Json;

/**
 * Created by Ivan Lovrencic on 15.8.2018..
 */

public class Media {

    @Json(name = "path")
    private String path;

    @Json(name = "type")
    private String type;

    @Json(name = "_id")
    private String id;

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
