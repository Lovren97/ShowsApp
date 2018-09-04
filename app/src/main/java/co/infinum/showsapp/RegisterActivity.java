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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import co.infinum.showsapp.Network.GenericResponse;
import co.infinum.showsapp.Entities.RegisteredUser;
import co.infinum.showsapp.Entities.User;
import co.infinum.showsapp.Network.ApiService;
import co.infinum.showsapp.Network.InitApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {

    //variable definition
    private User user;
    private ApiService apiService = InitApiService.apiService;
    private ProgressDialog progressDialog;
    private TextInputLayout newEmailLayout;
    private EditText newPassword;
    private EditText confirmPassword;
    private TextInputLayout confirm;
    private TextInputLayout newPassLayout;
    private EditText newEmail;
    private ImageView backArrow;
    private Button registerBtn;
    private Call<GenericResponse<RegisteredUser>> callRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //variable initialisation
        newEmail = findViewById(R.id.newEmail);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        confirm = findViewById(R.id.confirmLayout);
        newPassLayout = findViewById(R.id.newPasswordLayout);
        newEmailLayout = findViewById(R.id.newEmailLayout);
        backArrow = findViewById(R.id.backArrowRegister);
        registerBtn = findViewById(R.id.registerBtn);

        initListeners();
    }

    private void registerUser(){
        showRegisteringIn();
        callRegister = apiService.registerUser(user);
        callRegister.enqueue(new Callback<GenericResponse<RegisteredUser>>() {
            @Override
            public void onResponse(Call<GenericResponse<RegisteredUser>> call, Response<GenericResponse<RegisteredUser>> response) {
                hideRegisteringIn();
                if(response.isSuccessful()){
                    if(response.code() == 201){
                        Intent intent = new Intent();
                        intent.putExtra("email",user.getEmail());
                        setResult(Activity.RESULT_OK,intent);
                        Toast.makeText(RegisterActivity.this,"Registration successful!",Toast.LENGTH_LONG).show();
                        finish();
                    }else if(response.code() == 200){
                        newEmailLayout.setError("E-mail already in use!");
                    }else{
                        showErrorRegisteringIn("Unexpected error occurred! Please try again!");
                    }
                }else{
                    showErrorRegisteringIn("Failed registration. Please try again!");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<RegisteredUser>> call, Throwable t) {
                hideRegisteringIn();
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorRegisteringIn("Connection problem occurred. Please try again! ");
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    private void showErrorRegisteringIn(String message){
        new AlertDialog.Builder(this)
                .setTitle("")
                .setMessage(message)
                .setPositiveButton("OK",null)
                .create()
                .show();
    }

    private void showRegisteringIn(){
        progressDialog = ProgressDialog.show(this,"","Registration in process...",true,false);
    }

    private void hideRegisteringIn(){
        if(progressDialog != null){
            progressDialog.dismiss();
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

    private boolean checkPass(){
        if(!confirmPassword.getText().toString().equals(newPassword.getText().toString())){
            confirm.setError("Passwords need to be identical!");
            return false;
        }else if(newPassword.getText().toString().trim().length() < 5){
            newPassLayout.setError("Password needs to be atleast 5 characters!");
            return false;
        }else if(newPassword.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty()){
            newPassLayout.setError("Password can't be empty!");
            return false;
        }else{
            return true;
        }
    }

    private void initListeners(){
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = new User(newEmail.getText().toString(),newPassword.getText().toString());
                hideKeyboard();
                if(isInternetAvailable()){
                    if(checkPass()){
                        if(isValidEmail(newEmail.getText().toString())){
                            registerUser();
                        }else {
                            newEmailLayout.setError("Invalid e-mail adress!");
                        }
                    }
                }else{
                    showErrorRegisteringIn("No internet connection!");
                }
            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        newEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(isValidEmail(newEmail.getText().toString())){
                    newEmailLayout.setErrorEnabled(false);
                }
            }
        });
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(newPassword.getText().toString().length() >= 5){
                    newPassLayout.setErrorEnabled(false);
                }
            }
        });
        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(confirmPassword.getText().toString().equals(newPassword.getText().toString())){
                    confirm.setErrorEnabled(false);
                }
            }
        });

    }

    private void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onPause() {
        if(callRegister != null){
            callRegister.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if(callRegister != null){
            callRegister.cancel();
        }
        super.onStop();
    }
}
