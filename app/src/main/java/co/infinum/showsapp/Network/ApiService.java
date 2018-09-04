package co.infinum.showsapp.Network;


import java.util.List;

import co.infinum.showsapp.Entities.Comment;
import co.infinum.showsapp.Entities.Episode;
import co.infinum.showsapp.Entities.Media;
import co.infinum.showsapp.Entities.RegisteredUser;
import co.infinum.showsapp.Entities.Show;
import co.infinum.showsapp.Entities.Token;
import co.infinum.showsapp.Entities.User;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Ivan Lovrencic on 25.7.2018..
 */

public interface ApiService {

    @POST("/api/users/sessions")
    Call<GenericResponse<Token>> loginUser(@Body User user);

    @POST("/api/users")
    Call<GenericResponse<RegisteredUser>> registerUser(@Body User user);

    @GET("/api/shows")
    Call<GenericResponse<List<Show>>> getShows();

    @GET("/api/shows/{showId}")
    Call<GenericResponse<Show>> getShow(@Path("showId") String showId);

    @GET("/api/shows/{showId}/episodes")
    Call<GenericResponse<List<Episode>>> getEpisodes(@Path("showId") String id);

    @POST("/api/episodes")
    Call<GenericResponse<Episode>> addEpisode(@Header("Authorization") String token, @Body Episode episode);

    @GET("/api/episodes/{episodeId}")
    Call<GenericResponse<Episode>> getEpisodeInfo(@Path("episodeId") String episodeId);

    @GET("/api/episodes/{episodeId}/comments")
    Call<GenericResponse<List<Comment>>> getComments(@Path("episodeId") String episodeId);

    @POST("/api/comments")
    Call<GenericResponse<Comment>> postComment(@Header("Authorization") String token, @Body Comment comment);

    @POST("/api/media")
    @Multipart
    Call<GenericResponse<Media>> addPicutre(@Header("Authorization") String token, @Part("file\"; filename=\"image.jpg\"") RequestBody request);

    @POST("/api/shows/{showId}/like")
    Call<GenericResponse<Show>> likeShow(@Header("Authorization") String token, @Path("showId") String id);

    @POST("/api/shows/{showId}/dislike")
    Call<GenericResponse<Show>> dislikeShow(@Header("Authorization") String token, @Path("showId") String id);

}

