package co.infinum.showsapp.Network;

import com.squareup.moshi.Json;

/**
 * Created by Ivan Lovrencic on 27.7.2018..
 */

public class GenericResponse<T> {

    @Json(name  = "data")
    private T responseData;

    public T getResponseData() {
        return responseData;
    }
}
