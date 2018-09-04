package co.infinum.showsapp.Database;

import java.util.List;

import co.infinum.showsapp.Entities.Comment;

/**
 * Created by Ivan Lovrencic on 15.8.2018..
 */

public interface CommentRepository {

    void getComments(DatabaseCallback<List<Comment>> callback);
    void insertComments(List<Comment> comments,DatabaseCallback<Void> callback);
}
