package co.infinum.showsapp.Entities;

import com.squareup.moshi.Json;

/**
 * Created by Ivan Lovrencic on 26.7.2018..
 */

public class Token {

    @Json(name = "token")
    private String token;

    public Token(String token){
        setToken(token);
    }

    public String getTokenString() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
