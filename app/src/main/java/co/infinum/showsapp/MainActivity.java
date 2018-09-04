package co.infinum.showsapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


import co.infinum.showsapp.Entities.UserToken;
import co.infinum.showsapp.Network.GenericResponse;
import co.infinum.showsapp.Entities.Token;
import co.infinum.showsapp.Entities.User;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Call;

import static co.infinum.showsapp.Entities.UserToken.email;
import static co.infinum.showsapp.Entities.UserToken.user_token;
import static co.infinum.showsapp.Network.InitApiService.apiService;


/**
 * This is my Login Activity
 */
public class MainActivity extends AppCompatActivity {

    //variable definition
    private User user;
    private ProgressDialog progressDialog;
    private EditText emailText;
    private EditText passwordText;
    private CheckBox checkBox;
    private TextInputLayout passwordLayout;
    private TextInputLayout emailLayout;
    private Button loginBtn;
    private TextView register;
    private Call<GenericResponse<Token>> callLogin;
    private boolean rememberMe = false;


    public static final String BASE_URL = "https://api.infinum.academy";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //variable initialisation
        emailText = findViewById(R.id.email);
        passwordText = findViewById(R.id.password);
        checkBox = findViewById(R.id.remeberMe);
        loginBtn = findViewById(R.id.loginBtn);
        register = findViewById(R.id.createAcc);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);


        initListeners();
        setFields();
    }

    /**
     * Function that recovers and fills user email adress after user logs out
     */
    private void setFields(){
        String email = getIntent().getStringExtra("email");
        if(email != null){
            emailText.setText(email);
        }
    }

    /**
     * Function that makes api call and checks if user already exists. If user already exists, app logs user in,
     * otherwise app displays error.
     */
    private void checkIfAccountExists(){
        if(isValidEmail(user.getEmail())){
            showLoggingIn();
            callLogin =  apiService.loginUser(user);
            callLogin.enqueue(new Callback<GenericResponse<Token>>() {
                @Override
                public void onResponse(Call<GenericResponse<Token>> call, Response<GenericResponse<Token>> response) {
                    hideLoggingIn();
                    if(response.isSuccessful()){
                        user_token = response.body().getResponseData();
                        email = user.getEmail();
                        if(rememberMe){
                            getSharedPreferences("login",MODE_PRIVATE)
                                    .edit()
                                    .putString("email",user.getEmail())
                                    .putString("password",user.getPassword())
                                    .putBoolean("checkbox",true)
                                    .putString("token",user_token.getTokenString())
                                    .apply();
                        }else{
                            getSharedPreferences("login",MODE_PRIVATE)
                                    .edit()
                                    .clear()
                                    .apply();
                        }
                        Intent intent = new Intent(MainActivity.this,ShowsActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        showErrorLoggingIn("Error logging in. Please try again!");
                    }
                }
                @Override
                public void onFailure(Call<GenericResponse<Token>> call, Throwable t) {
                    hideLoggingIn();
                    if(call.isCanceled()){
                        //nothing
                    }else{
                        showErrorLoggingIn("Connection problem occurred.Try again!");
                    }
                }
            });
        }else{
            emailLayout.setError("Invalid e-mail adress!");
        }

    }

    private void initListeners(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                user = new User(emailText.getText().toString(),passwordText.getText().toString());
                rememberMe = checkBox.isChecked();
                if(isInternetAvailable()){
                    if(checkPassword()){
                        checkIfAccountExists();
                    }else{
                       passwordLayout.setError("Password must have atleast 5 characters!");
                    }
                }else{
                    showErrorLoggingIn("No internet connection!");
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivityForResult(intent,1);
            }
        });

        emailText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(isValidEmail(emailText.getText().toString())){
                    emailLayout.setErrorEnabled(false);
                }
            }
        });

        passwordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(checkPassword()){
                    passwordLayout.setErrorEnabled(false);
                }
            }

        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    @Override
    protected void onPause() {
        if(callLogin != null){
            callLogin.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(callLogin != null){
            callLogin.cancel();
        }
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 1){
                String email = data.getStringExtra("email");
                emailText.setText(email);
                passwordText.setText("");
                checkBox.setChecked(false);
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

    private void showErrorLoggingIn(String message){
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(message)
                .setPositiveButton("OK",null)
                .create()
                .show();
    }

    private void showLoggingIn(){
        progressDialog = ProgressDialog.show(this,"","Logging in...",true,false);
    }

    private void hideLoggingIn(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }


    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private boolean checkPassword(){
        if(passwordText.getText().toString().trim().length() < 5){
            return false;
        }else{
            return true;
        }
    }
}


