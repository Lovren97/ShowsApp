package co.infinum.showsapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.infinum.showsapp.Adapters.CommentAdapter;
import co.infinum.showsapp.Database.CommentRepositoryImpl;
import co.infinum.showsapp.Database.DatabaseCallback;
import co.infinum.showsapp.Entities.Comment;
import co.infinum.showsapp.Entities.UserToken;
import co.infinum.showsapp.Network.ApiService;
import co.infinum.showsapp.Network.GenericResponse;
import co.infinum.showsapp.Network.InitApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ApiService apiService = InitApiService.apiService;
    private RecyclerView recyclerView;
    private CommentAdapter adapter;
    private List<Comment> comments = new ArrayList<>();
    private ProgressDialog progressDialog;
    private String episodeId;
    private Button postBtn;
    private EditText addComment;
    private NestedScrollView nestedScrollView;
    private CommentRepositoryImpl commentRepository;
    private Call<GenericResponse<Comment>> callPost;
    private Call<GenericResponse<List<Comment>>> callGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        toolbar = findViewById(R.id.commentsToolbar);
        recyclerView = findViewById(R.id.commentsRecyler);
        postBtn = findViewById(R.id.postBtn);
        addComment = findViewById(R.id.addComment);
        nestedScrollView = findViewById(R.id.nestedScroll);

        //toolbar setup
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black);
        setSupportActionBar(toolbar);

        //getting episode ID
        episodeId = getIntent().getStringExtra("episodeId");


        initListeners();
        initRecyclerView();
        initAdapter(comments);
        initDatabaseExecutor();
        loadComments();
    }

    private void initRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void initAdapter(List<Comment> comments){
        adapter = new CommentAdapter(comments);
        recyclerView.setAdapter(adapter);
    }

    private void initListeners(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(addComment.getText().toString().length() != 0){
                    if(isInternetAvailable()){
                        postComment();
                    }else{
                        showErrorLoading("No internet connection!");
                    }
                }
            }
        });

    }

    private void postComment(){
        Comment comment = new Comment(addComment.getText().toString(),episodeId);
        callPost = apiService.postComment(UserToken.user_token.getTokenString(),comment);
        callPost.enqueue(new Callback<GenericResponse<Comment>>() {
            @Override
            public void onResponse(Call<GenericResponse<Comment>> call, Response<GenericResponse<Comment>> response) {
                if(response.isSuccessful()){
                    comments.add(response.body().getResponseData());
                    displayComments(comments);
                }else{
                    showErrorLoading("An error occurred! Please try again!");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Comment>> call, Throwable t) {
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Connection problem occurred. Fetching data from database!");
                }
            }
        });
    }


    private void fetchCommentsFromInternet(){
        showsLoading();
        callGet = apiService.getComments(episodeId);
        callGet.enqueue(new Callback<GenericResponse<List<Comment>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Comment>>> call, Response<GenericResponse<List<Comment>>> response) {
                hideLoading();
                if(response.isSuccessful()){
                    comments = response.body().getResponseData();
                    displayComments(comments);

                    //add comments to database
                    insertCommentsIntoDatabase();
                }else{
                    if(call.isCanceled()){
                        //nothing
                    }else{
                        showErrorLoading("Connection problem occurred. Fetching data from database!");
                    }
                    fetchCommentsFromDatabase();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Comment>>> call, Throwable t) {
                hideLoading();
                showErrorLoading("Unknown error occurred. Please try again!");
                fetchCommentsFromDatabase();
            }
        });
    }

    private void loadComments(){
        if(isInternetAvailable()){
            fetchCommentsFromInternet();
        }else{
            fetchCommentsFromDatabase();
        }
    }

    private void fetchCommentsFromDatabase(){
        commentRepository.getComments(new DatabaseCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> data) {
                comments = data;
                displayComments(data);
            }

            @Override
            public void onError(Throwable t) {
                showErrorLoading("Unknown error occurred. Please try again!");
            }
        });
    }

    private void insertCommentsIntoDatabase(){
        commentRepository.insertComments(comments, new DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                //data inserted
            }

            @Override
            public void onError(Throwable t) {
                showErrorLoading("Unknown error occurred. Please try again!");
            }
        });
    }

    private void displayComments(final List<Comment> comments){
        if(comments.size() > 0){
            //disabling empty placeholder
            ImageView empty_place = findViewById(R.id.commentImage);
            TextView text  = findViewById(R.id.bzvText);
            TextView text2 = findViewById(R.id.bzvText2);

            empty_place.setVisibility(View.GONE);
            text.setVisibility(View.GONE);
            text2.setVisibility(View.GONE);
        }

        if(adapter != null){
            adapter.setComments(comments);
        }else{
            initAdapter(comments);
        }

        //Scrolling to last comment
        nestedScrollView.fullScroll(View.FOCUS_DOWN);

        //Refreshing edit text box
        addComment.setText("");
    }

    private void initDatabaseExecutor(){
        commentRepository = new CommentRepositoryImpl(this,episodeId);
    }

    private void showsLoading(){
        progressDialog = ProgressDialog.show(this,"","Loading comments...",true,false);
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

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        if(callGet != null){
            callGet.cancel();
        }
        if(callPost != null){
            callPost.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(callGet != null){
            callGet.cancel();
        }
        if(callPost != null){
            callPost.cancel();
        }
        super.onStop();
    }
}
