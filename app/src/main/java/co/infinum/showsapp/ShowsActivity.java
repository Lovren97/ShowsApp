package co.infinum.showsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import co.infinum.showsapp.Adapters.ShowAdapter;
import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Database.ShowRepositoryImpl;
import co.infinum.showsapp.Database.UserRepositoryImpl;
import co.infinum.showsapp.Entities.User;
import co.infinum.showsapp.Entities.UserToken;
import co.infinum.showsapp.Network.GenericResponse;
import co.infinum.showsapp.Entities.Show;
import co.infinum.showsapp.Network.ApiService;
import co.infinum.showsapp.Network.InitApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.infinum.showsapp.Entities.UserToken.email;

/**
 * Created by Ivan Lovrencic on 24.7.2018..
 */

public class ShowsActivity extends AppCompatActivity {

    //variable definition
    private ShowAdapter adapter;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ApiService apiService = InitApiService.apiService;
    private ShowRepositoryImpl showRepository;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView logoutBtn;
    private UserRepositoryImpl userRepository;
    private ImageView emptyState;
    private TextView message;
    private FloatingActionButton layout;
    private Call<GenericResponse<List<Show>>> callShows;
    private Call<GenericResponse<Show>> callShowId;
    private boolean changeLayout = true;


    public List<Show> shows = new ArrayList<>();
    private static boolean warning = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //variable initialisation
        recyclerView = findViewById(R.id.recyclerview);
        logoutBtn = findViewById(R.id.logOut);
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        layout = findViewById(R.id.changeMyLayout);
        emptyState = findViewById(R.id.emptyShows);
        message = findViewById(R.id.message);



        initRecyclerView(changeLayout);
        initAdapter(shows, changeLayout);
        initListeners();
        initDatabaseExecutor();
        loadShows();
    }

    /**
     * Function that saves User info in database. User info is necessery because I use it after when user
     * likes Show to verify he is liking it for the first time
     */
    private void saveUserInDatabase(){
        userRepository.getUsers(new DatabaseCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                boolean save = true;
                for(User user : data){
                    if(user.getEmail().equals(UserToken.email)){
                        //skip saving
                        save = false;
                        break;
                    }
                }
                if(save){
                    List<User> users = new ArrayList<>();
                    for(Show show : shows){
                        User newUser = new User(UserToken.email,show.getId(),"none");
                        users.add(newUser);
                    }

                    userRepository.insertUsers(users, new DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            //inserted
                        }

                        @Override
                        public void onError(Throwable t) {
                            showErrorLoading("Error occurred. PLease try again!");
                        }
                    });
                }
            }

            @Override
            public void onError(Throwable t) {
                showErrorLoading("Database error occurred! Please try again!");
            }
        });
    }

    /**
     * Function that with API call gets all SHOWS from internet. After acquiring list of ALL shows, function orderly
     * makes API calls for every ShowID to get all information about show, before saving it in internal database.
     */
    private void fetchDataFromInternet() {
        showsLoading();
        callShows = apiService.getShows();
        callShows.enqueue(new Callback<GenericResponse<List<Show>>>() {
            @Override
            public void onResponse(@NonNull Call<GenericResponse<List<Show>>> call, @NonNull Response<GenericResponse<List<Show>>> response) {
                if (response.isSuccessful()) {
                    shows = response.body().getResponseData();
                    displayShows(shows);
                    for (Show show : shows) {
                        fetchShowWithId(show.getId());
                    }
                    hideLoading();
                    saveUserInDatabase();

                    if(shows.size() > 0){
                        hideEmptyState();
                    }
                } else {
                    hideLoading();
                    showErrorLoading("Connection problem. Fetching data from database!");
                    fetchDataFromDatabase();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GenericResponse<List<Show>>> call, @NonNull Throwable t) {
                hideLoading();
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Connection problem occurred. Fetching data from database!");
                }
                fetchDataFromDatabase();
            }
        });
    }

    /**
     * @param id Show's ID
     *           Function that makes API call with specific Show ID to get all show information. After API call, it saves show info
     *           in internal database.
     */
    private void fetchShowWithId(String id) {
        callShowId = apiService.getShow(id);
        callShowId.enqueue(new Callback<GenericResponse<Show>>() {
            @Override
            public void onResponse(Call<GenericResponse<Show>> call, Response<GenericResponse<Show>> response) {
                if (response.isSuccessful()) {
                    refreshShows(response.body().getResponseData());
                    insertShowInDatabase(response.body().getResponseData());
                } else {
                    showErrorLoading("Error loading shows. Please try again!");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Show>> call, Throwable t) {
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Connection problem occurred. Fetching data from database!");
                }
            }
        });
    }

    private void fetchDataFromDatabase() {
        showRepository.getShows(new DatabaseCallback<List<Show>>() {
            @Override
            public void onSuccess(List<Show> data) {
                if (data.isEmpty()) {
                    //data is empty
                } else {
                    shows = data;
                    displayShows(data);
                    saveUserInDatabase();
                    hideEmptyState();
                }
            }
            @Override
            public void onError(Throwable t) {
                showErrorLoading("Error while loading database!");
            }
        });
    }

    private void hideEmptyState(){
        emptyState.setVisibility(View.GONE);
        message.setVisibility(View.GONE);
    }


    private void loadShows() {
        if (isInternetAvailable()) {
            fetchDataFromInternet();
        } else {
            //if user is first time using offline mode,app informs him that data could be outdated.
            if (warning) {
                showErrorLoading("You are currently browsing in offline mode.The data could be outdated!");
                warning = false;
            }
            fetchDataFromDatabase();
        }
    }


    private void displayShows(List<Show> shows) {
        if (adapter == null) {
            initAdapter(shows, changeLayout);
        } else {
            adapter.setShowList(shows);
        }

    }

    /**
     * @param show Refreshed Show info from internet
     *             Function that refreshes show list with freshly gotten info from internet
     */
    private void refreshShows(Show show) {
        for (Show temp : shows) {
            if (temp.getId().equals(show.getId())) {
                shows.set(shows.indexOf(temp), show);
            }
        }
    }

    private void insertShowInDatabase(Show show) {
        showRepository.insertShow(show, new DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                //data inserted
            }

            @Override
            public void onError(Throwable t) {
                showErrorLoading(t.getMessage());
            }
        });
    }

    private void initDatabaseExecutor() {
        showRepository = new ShowRepositoryImpl(this);
        userRepository = new UserRepositoryImpl(this, UserToken.email);
    }

    private void initListeners() {
        adapter.setListener(new ShowAdapter.OnShowClickListener() {
            @Override
            public void onShowClick(Show show) {
                Intent intent = new Intent(ShowsActivity.this, EpisodesActivity.class);
                intent.putExtra("show", show.getId());
                startActivityForResult(intent, 2);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
                logoutBtn.startAnimation(animation);

                new AlertDialog.Builder(ShowsActivity.this)
                        .setMessage("Are you sure you want to log out?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences loginPref = getSharedPreferences("login", MODE_PRIVATE);
                                Intent intent = new Intent(ShowsActivity.this, MainActivity.class);
                                intent.putExtra("email", loginPref.getString("email", null));
                                loginPref.edit().clear().apply();
                                startActivity(intent);
                                ShowsActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });


        /*
          Listener for button that changes Show layout.
          (Couldn't find icon that was used in Final task example for Grid layout)
         */
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLayout = !changeLayout;
                initRecyclerView(changeLayout);
                initAdapter(shows, changeLayout);
                displayShows(shows);
                if (!changeLayout) {
                    layout.setImageResource(R.drawable.ic_grid_on_black_24dp);
                } else {
                    layout.setImageResource(R.drawable.ic_filter_list_black_24dp);
                }
                layout.invalidate();
                initListeners();
                if (!isInternetAvailable()) {
                    loadShows();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadShows();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Overrided function that is called when user presses home button
     */
    @Override
    protected void onPause() {
        if(callShows != null){
            callShows.cancel();
        }
        if(callShowId != null){
            callShowId.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(callShowId != null){
            callShowId.cancel();
        }
        if(callShows != null){
            callShows.cancel();
        }
        super.onStop();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void showsLoading() {
        progressDialog = ProgressDialog.show(this, "", "Loading shows...", true, false);
    }

    private void hideLoading() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void showErrorLoading(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .create()
                .show();
    }


    private void initAdapter(List<Show> shows, boolean changeLayout) {
        adapter = new ShowAdapter(shows, changeLayout);
        recyclerView.setAdapter(adapter);
    }


    private void initRecyclerView(boolean changeLayout) {
        if (changeLayout) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }

    }

}
