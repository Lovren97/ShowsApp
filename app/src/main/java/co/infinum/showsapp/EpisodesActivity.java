package co.infinum.showsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import co.infinum.showsapp.Adapters.EpisodeAdapter;
import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Database.EpisodeRepositoryImpl;
import co.infinum.showsapp.Database.ShowRepositoryImpl;
import co.infinum.showsapp.Database.UserRepositoryImpl;
import co.infinum.showsapp.Entities.Episode;
import co.infinum.showsapp.CustomViews.ExpandableTextView;
import co.infinum.showsapp.Entities.User;
import co.infinum.showsapp.Entities.UserToken;
import co.infinum.showsapp.Network.GenericResponse;
import co.infinum.showsapp.Entities.Show;
import co.infinum.showsapp.Network.ApiService;
import co.infinum.showsapp.Network.InitApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Header;
import retrofit2.http.Path;

import static co.infinum.showsapp.Entities.UserToken.email;


public class EpisodesActivity extends AppCompatActivity {


    //variable definition
    private Show show;
    private EpisodeAdapter adapter;
    private RecyclerView recyclerView;
    private List<Episode> episodes = new ArrayList<>();
    private ProgressDialog progressDialog;
    private ApiService apiService = InitApiService.apiService;
    private EpisodeRepositoryImpl episodeRepository;
    private ExpandableTextView showDesc;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private ImageView showImage;
    private TextView episodeNmb;
    private TextView likeRatio;
    private String showId;
    private ImageButton like;
    private ImageButton dislike;
    private ShowRepositoryImpl showRepository;
    private UserRepositoryImpl userRepository;
    private User user;
    private Call<GenericResponse<List<Episode>>> callEpisodes;
    private Call<GenericResponse<Show>> callShow;
    private Call<GenericResponse<Show>> callLike;
    private Call<GenericResponse<Show>> callDislike;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);

        //variable initialisation
        fab = findViewById(R.id.fab);
        toolbar = findViewById(R.id.toolbar2);
        showImage = findViewById(R.id.showScreen);
        showDesc = findViewById(R.id.showDesc);
        recyclerView = findViewById(R.id.recyclerView2);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        episodeNmb = findViewById(R.id.episodeCounter);
        likeRatio = findViewById(R.id.likeRatio);
        like = findViewById(R.id.likeButton);
        dislike = findViewById(R.id.dislikeButton);

        //setting up toolbar
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        //loading up and displaying Show's image
        showId = getIntent().getStringExtra("show");


        //getting show with API call or database and then getting all episodes from that show
        getShow();
    }

    private void loadUserInfo(){
        userRepository.getUsers(new DatabaseCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                for(User temp : data){
                    if(temp.getShowId().equals(show.getId())){
                       user = temp;
                       break;
                    }
                }
                setLikesDisplay();
            }

            @Override
            public void onError(Throwable t) {
                showErrorLoading("Error occurred!");
            }
        });
    }

    private void setLikesDisplay(){
        if(user.getShowStatus().equals("like")){
            like.setImageResource(R.drawable.ic_thumb_up_black_24dp);
            like.setBackgroundResource(R.drawable.rounded_button2);
            like.setEnabled(false);
        }
        if(user.getShowStatus().equals("dislike")){
            dislike.setImageResource(R.drawable.ic_thumb_down_black_24dp);
            dislike.setBackgroundResource(R.drawable.rounded_button2);
            dislike.setEnabled(false);
        }
    }


    /**
     * Function that makes API or Database call to get Show's information. After getting information, app proceeds to
     * make another call to get all episodes from that show.
     */
    private void getShow(){
        if(isInternetAvailable()){
            getShowFromInternet();
        }else{
            initDatabaseExecutorForShow();
            getShowFromDatabase();
        }
    }

    /**
     * @param shows
     * @return
     *
     * Function that returns Show from show list that we got from database.
     *
     *
     * FOR REVIEWER
     * ->
     * (I could have made another Database call that only calls for specific Show, but the list of shows is relatively small and
     * because of that, the time difference is immeasurable)
     *
     */
    private Show getShowFromData(List<Show> shows){
        for( Show show : shows){
            if(show.getId().equals(showId)){
                return show;
            }
        }
        return null;
    }

    /**
     * Function sets collapsing toolbar and fills it with freshly gotten show information
     */
    private void setDisplay(){
        //setting coolapsing toolbar and displaying Show's info
        Picasso.get().load(MainActivity.BASE_URL+show.getImageUrl()).into(showImage);

        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        collapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);
        collapsingToolbarLayout.setTitle(show.getTitle());

        if(show.getDescription().equals("")){
            showDesc.setText("No description");
        }else {
            showDesc.setText(show.getDescription());
        }

        Integer likes = show.getLikes();
        likeRatio.setText(likes.toString());
    }


    /**
     * Function that with API call gets all episodes from Internet. After acquiring list of all episodes
     * function saves all episodes in internal database.
     */
    private void fetchEpisodesFromInternet(){
        showsLoading();
        callEpisodes =  apiService.getEpisodes(show.getId());
        callEpisodes.enqueue(new Callback<GenericResponse<List<Episode>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse<List<Episode>>> call, @NonNull Response<GenericResponse<List<Episode>>> response) {
                hideLoading();
                if(response.isSuccessful()){
                    episodes = response.body().getResponseData();
                    displayEpisodes(episodes);
                    addShowIdToEpisodes(episodes);
                    insertEpisodesInDatabase(episodes);

                    Integer numberOfEpisodes = episodes.size();
                    episodeNmb.setText(numberOfEpisodes.toString());
                }else{
                    hideLoading();
                    if(call.isCanceled()){
                        //nothing
                    }else{
                        showErrorLoading("Connection problem occurred. Fetching data from database!");
                    }
                    fetchEpisodesFromDatabase();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Episode>>> call, Throwable t) {
                hideLoading();
                showErrorLoading("Unknown error while loading. Please try again!");
                fetchEpisodesFromDatabase();
            }
        });
    }


    /**
     * @param episodes List of episodes gotten from internet
     *
     * This function supplements every episode with ShowId,because episodes when gotten from internet don't
     * have it.
     */
    private void addShowIdToEpisodes(List<Episode> episodes){
        for(Episode episode : episodes){
            episode.setShowId(show.getId());
        }
    }


    private void insertEpisodesInDatabase(List<Episode> episodes){
        episodeRepository.insertEpisodes(episodes, new DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                //data inserted
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(EpisodesActivity.this,t.getMessage(),Toast.LENGTH_SHORT).show();
                showErrorLoading("An error occured while uploading episodes to database!");
            }
        });
    }

    private void initDatabaseExecutorForShow(){
        showRepository = new ShowRepositoryImpl(this);
    }

    private void initDatabaseExecutorForEpisode(){
        episodeRepository = new EpisodeRepositoryImpl(this,show.getId());
        initDatabaseExecutorForUser();
    }

    private void initDatabaseExecutorForUser(){
        userRepository = new UserRepositoryImpl(this,email);
    }

    private void fetchEpisodesFromDatabase(){
        showsLoading();
        episodeRepository.getEpisodes(new DatabaseCallback<List<Episode>>() {
            @Override
            public void onSuccess(List<Episode> data) {
                hideLoading();
                if(data.isEmpty()){
                    //data is empty
                }else{
                    episodes = data;
                    displayEpisodes(data);

                    Integer numberOfEpisodes = episodes.size();
                    episodeNmb.setText(numberOfEpisodes.toString());
                }
            }

            @Override
            public void onError(Throwable t) {
                hideLoading();
                showErrorLoading("Error occurred while loading data from database!");
            }
        });
    }


    private void loadEpisodes(){
        if(isInternetAvailable()){
            fetchEpisodesFromInternet();
        }else{
            fetchEpisodesFromDatabase();
        }
    }

    private void likeShowInternet(){
        callLike = apiService.likeShow(UserToken.user_token.getTokenString(),show.getId());
        callLike.enqueue(new Callback<GenericResponse<Show>>() {
            @Override
            public void onResponse(Call<GenericResponse<Show>> call, Response<GenericResponse<Show>> response) {
                if(response.isSuccessful()){
                    like.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                    like.setBackgroundResource(R.drawable.rounded_button2);
                    dislike.setEnabled(true);

                    if(user.getShowStatus().equals("dislike")){
                        dislike.setImageResource(R.drawable.ic_thumb_down);
                        dislike.setBackgroundResource(R.drawable.rounded_button);
                    }
                    refreshUserInfo("like");
                }
                else{
                    like.setEnabled(true);
                    dislike.setEnabled(true);
                    showErrorLoading("Connection timed out. Please try again later!");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Show>> call, Throwable t) {
                like.setEnabled(true);
                dislike.setEnabled(true);
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Connection problem occurred!");
                }
            }
        });
    }

    private void dislikeShowInternet(){
        callDislike = apiService.dislikeShow(UserToken.user_token.getTokenString(),show.getId());
        callDislike.enqueue(new Callback<GenericResponse<Show>>() {
            @Override
            public void onResponse(Call<GenericResponse<Show>> call, Response<GenericResponse<Show>> response) {
                if(response.isSuccessful()){
                    dislike.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                    dislike.setBackgroundResource(R.drawable.rounded_button2);
                    like.setEnabled(true);

                    if(user.getShowStatus().equals("like")){
                        like.setImageResource(R.drawable.ic_thumb_up);
                        like.setBackgroundResource(R.drawable.rounded_button);
                    }
                    refreshUserInfo("dislike");
                }else{
                    showErrorLoading("Connection timed out. Please try again!");
                    dislike.setEnabled(true);
                    like.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Show>> call, Throwable t) {
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Connection problem occurred!");
                }
                dislike.setEnabled(true);
                like.setEnabled(true);
            }
        });
    }

    private void initListners(){
        adapter.setListener(new EpisodeAdapter.OnEpisodeClickListener() {
            @Override
            public void onEpisodeClick(Episode episode) {
                Intent intent = new Intent(EpisodesActivity.this,EpisodeInfoActivity.class);
                intent.putExtra("episodeId",episode.getId());
                startActivity(intent);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EpisodesActivity.this,EnterNewEpisode.class);
                intent.putExtra("showId",show.getId());
                startActivityForResult(intent,1997);
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like.setEnabled(false);
                dislike.setEnabled(false);
                if(isInternetAvailable()){
                    likeShowInternet();
                }else{
                    showErrorLoading("No internet connection!");
                    like.setEnabled(true);
                    dislike.setEnabled(true);
                }
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dislike.setEnabled(false);
                like.setEnabled(false);
                if(isInternetAvailable()){
                    dislikeShowInternet();
                }else{
                    showErrorLoading("No internet connection!");
                    dislike.setEnabled(true);
                    like.setEnabled(true);
                }

            }
        });
    }

    private void refreshUserInfo(String showStatus){
        User user = new User(UserToken.email,show.getId(),showStatus);
        List<User> users = new ArrayList<>();
        users.add(user);
        userRepository.insertUsers(users, new DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                //data refreshed
                getShow();
            }

            @Override
            public void onError(Throwable t) {
                showErrorLoading("Error occured! Please try again!");
            }
        });
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void displayEpisodes(List<Episode> episodes){
        if(adapter != null){
            adapter.setEpisodeList(episodes);
        }else{
            initAdapter(episodes);
        }
    }

    private void initAdapter(List<Episode> episodes){
        adapter = new EpisodeAdapter(episodes);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
            if(requestCode == 1){
                if(resultCode == Activity.RESULT_OK){
                    adapter.notifyDataSetChanged();
                }
            }
            if(requestCode == 1997){
                if(resultCode == Activity.RESULT_OK){
                    loadEpisodes();
                }
            }
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

    private void getShowFromInternet(){
        showsLoading();
        callShow = apiService.getShow(showId);
        callShow.enqueue(new Callback<GenericResponse<Show>>() {
            @Override
            public void onResponse(Call<GenericResponse<Show>> call, Response<GenericResponse<Show>> response) {
                hideLoading();
                if(response.isSuccessful()){
                    show = response.body().getResponseData();
                    setDisplay();
                    initDatabaseExecutorForEpisode();

                    //initialisation of other elements after acquiring show information
                    initRecyclerView();
                    initAdapter(episodes);
                    initListners();
                    loadEpisodes();
                    loadUserInfo();
                }else{
                    showErrorLoading("An error occurred. Please try again!");
                    getShowFromDatabase();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Show>> call, Throwable t) {
                hideLoading();
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Connection problem occurred. Fetching data from database!");
                }
                getShowFromDatabase();
            }
        });
    }

    private void getShowFromDatabase(){
        showRepository.getShows(new DatabaseCallback<List<Show>>() {
            @Override
            public void onSuccess(List<Show> data) {
                show = getShowFromData(data);
                setDisplay();
                initDatabaseExecutorForEpisode();

                //initialisation of other elements after acquiring show information
                initRecyclerView();
                initAdapter(episodes);
                initListners();
                loadEpisodes();
                loadUserInfo();
            }

            @Override
            public void onError(Throwable t) {
                showErrorLoading("An error occurred. Please try again!");
            }
        });
    }

    @Override
    protected void onPause() {
        if(callShow != null){
            callShow.cancel();
        }
        if(callEpisodes != null){
            callEpisodes.cancel();
        }
        if(callDislike != null){
            callDislike.cancel();
        }
        if(callLike != null){
            callLike.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(callShow != null){
            callShow.cancel();
        }
        if(callEpisodes != null){
            callEpisodes.cancel();
        }
        if(callDislike != null){
            callDislike.cancel();
        }
        if(callLike != null){
            callLike.cancel();
        }
        super.onStop();
    }
}
