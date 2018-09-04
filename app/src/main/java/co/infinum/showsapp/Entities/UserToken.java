package co.infinum.showsapp.Entities;

/**
 * Created by Ivan Lovrencic on 6.8.2018..
 */

public class UserToken {

    public static Token user_token;
    public static String email;

    public static void setUser_token(Token user_token) {
        UserToken.user_token = user_token;
    }

    public static Token getUser_token() {
        return user_token;
    }
}
