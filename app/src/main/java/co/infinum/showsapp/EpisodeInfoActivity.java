package co.infinum.showsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import co.infinum.showsapp.Database.CommentRepositoryImpl;
import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Database.EpisodeRepositoryImpl;
import co.infinum.showsapp.Entities.Comment;
import co.infinum.showsapp.Entities.Episode;
import co.infinum.showsapp.Network.ApiService;
import co.infinum.showsapp.Network.GenericResponse;
import co.infinum.showsapp.Network.InitApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EpisodeInfoActivity extends AppCompatActivity {

    private Episode episode;
    private Toolbar toolbar;
    private TextView episodeTitle;
    private TextView episodeSeasonAndEpisode;
    private TextView episodeDesc;
    private ImageView episodeImage;
    private ImageView backArrow;
    private ProgressDialog progressDialog;
    private ApiService apiService = InitApiService.apiService;
    private String episodeId;
    private EpisodeRepositoryImpl episodeRepository;
    private TextView comments;
    private ProgressBar progressBar;
    private CommentRepositoryImpl commentRepository;
    private Call<GenericResponse<List<Comment>>> callComment;
    private Call<GenericResponse<Episode>> callEpisode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episode_info);

        //variable initialisation
        toolbar = findViewById(R.id.toolbar4);
        episodeTitle = findViewById(R.id.MainTitle);
        episodeSeasonAndEpisode = findViewById(R.id.episodeSeasonAndEpisode);
        episodeDesc = findViewById(R.id.description);
        episodeImage = findViewById(R.id.imageEpisode);
        backArrow = findViewById(R.id.backArrow3);
        comments = findViewById(R.id.comments);
        progressBar = findViewById(R.id.imageLoading);

        //getting Episode ID
        episodeId = getIntent().getStringExtra("episodeId");


        initListeners();
        initDatabaseExecutor();
        loadEpisodeDetails();
    }

    private void initListeners(){
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                backArrow.startAnimation(animation);

                finish();
            }
        });

        comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EpisodeInfoActivity.this,CommentsActivity.class);
                intent.putExtra("episodeId",episodeId);
                startActivityForResult(intent,932);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 932){
                loadCommentNumber();
            }
        }
    }

    private void loadEpisodeDetails(){
        if(isInternetAvailable()){
            fetchEpisodeInfoFromInternet();
        }else{
            fetchEpisodeInfoFromDatabase();
        }
        loadCommentNumber();
    }

    private void loadCommentNumber(){
        if(isInternetAvailable()){
            fetchCommentNumberFromInternet();
        }else{
            fetchCommentNumberFromDatabase();
        }
    }

    private void fetchCommentNumberFromInternet(){
        callComment = apiService.getComments(episodeId);
        callComment.enqueue(new Callback<GenericResponse<List<Comment>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Comment>>> call, Response<GenericResponse<List<Comment>>> response) {
                if(response.isSuccessful()){
                    Integer numberOfComments = response.body().getResponseData().size();
                    comments.setText("Comments ("+numberOfComments.toString()+")");
                }else{
                    fetchCommentNumberFromDatabase();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Comment>>> call, Throwable t) {
                fetchCommentNumberFromDatabase();
            }
        });
    }

    private void fetchCommentNumberFromDatabase(){
        commentRepository.getComments(new DatabaseCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                Integer numberOfComments = data.size();
                comments.setText("Comments ("+numberOfComments.toString()+")");
            }

            @Override
            public void onError(Throwable t) {
                //nothing
            }
        });
    }

    private void fetchEpisodeInfoFromInternet(){
        showsLoading();
        callEpisode = apiService.getEpisodeInfo(episodeId);
        callEpisode.enqueue(new Callback<GenericResponse<Episode>>() {
            @Override
            public void onResponse(Call<GenericResponse<Episode>> call, Response<GenericResponse<Episode>> response) {
                hideLoading();
                if(response.isSuccessful()){
                    episode = response.body().getResponseData();
                    displayData();
                }else{
                    showErrorLoading("Connection problem. Fetching info from database!");
                    fetchEpisodeInfoFromDatabase();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Episode>> call, Throwable t) {
                hideLoading();
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Connection problem occurred. Fetching data from database!");
                }
                fetchEpisodeInfoFromDatabase();
            }
        });
    }


    private void fetchEpisodeInfoFromDatabase(){
        episodeRepository.getEpisode(new DatabaseCallback<Episode>() {
            @Override
            public void onSuccess(Episode data) {
                if(data != null){
                    episode = data;
                    displayData();
                }
            }

            @Override
            public void onError(Throwable t) {
                showErrorLoading("An error occurred. Please try again!");
            }
        });
    }

    private void displayData(){
        episodeTitle.setText(episode.getTitle());

        if(episode.getDescription().length() > 0){
            episodeDesc.setText(episode.getDescription());
        }else{
            episodeDesc.setText("No description");
        }

        episodeSeasonAndEpisode.setText("S"+episode.getSeason()+ " "+"Ep"+episode.getEpisodeNmb());

        progressBar.setVisibility(View.VISIBLE);
        if(episode.getImageUrl().length() > 0){
            Picasso.get()
                    .load(MainActivity.BASE_URL+episode.getImageUrl())
                    .into(episodeImage, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            progressBar.setVisibility(View.GONE);
                            episodeImage.setImageResource(R.drawable.ic_empty_placeholder);
                        }
                    });
        }else{
            progressBar.setVisibility(View.GONE);
            Picasso.get()
                    .load(R.drawable.ic_empty_placeholder)
                    .into(episodeImage);
        }
    }

    private void initDatabaseExecutor(){
        episodeRepository = new EpisodeRepositoryImpl(episodeId,this);
        commentRepository = new CommentRepositoryImpl(this,episodeId);
    }



    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showsLoading(){
        progressDialog = ProgressDialog.show(this,"","Loading episodes...",true,false);
    }

    private void hideLoading(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void showErrorLoading(String message){
        new android.app.AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(message)
                .setPositiveButton("OK",null)
                .create()
                .show();
    }

    @Override
    protected void onStop() {
        if(callEpisode != null){
            callEpisode.cancel();
        }
        if(callComment != null){
            callComment.cancel();
        }
        super.onStop();
    }
}
