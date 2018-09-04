package co.infinum.showsapp.Database;

/**
 * Created by Ivan Lovrencic on 29.7.2018..
 */

public interface DatabaseCallback<T> {

    void onSuccess(T data);
    void onError(Throwable t);
}
