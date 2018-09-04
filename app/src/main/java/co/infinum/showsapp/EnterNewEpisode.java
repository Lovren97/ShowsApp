package co.infinum.showsapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import co.infinum.showsapp.Entities.Episode;
import co.infinum.showsapp.Entities.Media;
import co.infinum.showsapp.Entities.Show;
import co.infinum.showsapp.Entities.UserToken;
import co.infinum.showsapp.Network.ApiService;
import co.infinum.showsapp.Network.GenericResponse;
import co.infinum.showsapp.Network.InitApiService;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EnterNewEpisode extends AppCompatActivity {

    //variable definition
    private ApiService apiService = InitApiService.apiService;
    private EditText episodeName;
    private EditText episodeDesc;
    private TextView seasonDisplay;
    private ImageView addPhoto;
    private ImageView imageDisplay;
    private TextView textView;
    private String showId;
    private TextView seasonAndEpisode;
    private ImageView backArrow;
    private Button saveBtn;
    private Uri episodeUri;
    private ProgressDialog progressDialog;
    private Call<GenericResponse<Media>> callMedia;
    private Call<GenericResponse<Episode>> callEpisode;
    private int GET_FROM_GALLERY = 1;
    private int episodeNmb;
    private int episodeSeason;


    private static int PERMISSION_FOR_GALERY = 97;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_episode);

        //variable initialisation
        episodeName = findViewById(R.id.episodeName);
        episodeDesc = findViewById(R.id.episodeDesc);
        seasonAndEpisode = findViewById(R.id.seasonAdd);
        seasonDisplay = findViewById(R.id.seasonDisplay);
        imageDisplay = findViewById(R.id.imageDisplay);
        addPhoto = findViewById(R.id.photoAdd);
        textView = findViewById(R.id.addPhoto);
        backArrow = findViewById(R.id.backArrow2);
        saveBtn = findViewById(R.id.saveBtn);

        //getting showID
        showId = getIntent().getStringExtra("showId");
        initListeners();
    }

    private void initListeners(){
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChooserDialog();
            }
        });
        imageDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChooserDialog();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                backArrow.startAnimation(animation);

                onBackPressed();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(episodeDesc.getText().toString().length() < 50){

                }
                if(episodeName.getText().toString().equals("") || episodeDesc.getText().toString().equals("") || seasonDisplay.getText().toString().equals("Unknown")){
                    Toast.makeText(view.getContext(), "You have to fill up everything!", Toast.LENGTH_SHORT).show();
                }else{
                    if(episodeDesc.getText().toString().length() > 50){
                        if(episodeUri == null){
                            if(isInternetAvailable()){
                                addEpisode(null);
                            }else{
                                showErrorLoading("No internet connection!");
                            }
                        }else{
                            if(isInternetAvailable()){
                                addMedia();
                            }else {
                                showErrorLoading("No internet connection!");
                            }
                        }
                    }else{
                        Toast.makeText(EnterNewEpisode.this,"Description has to be at least 50 characters long!",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        seasonAndEpisode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
    }

    private void addMedia(){
        showsLoading();

        //creating a usable File to upload
        File image = new File(getPath(episodeUri));
        RequestBody requestBody = RequestBody.create(MediaType.parse(image.getName()+"/jpg"),image);

        //making an API call
        callMedia = apiService.addPicutre(UserToken.user_token.getTokenString(),requestBody);
        callMedia.enqueue(new Callback<GenericResponse<Media>>() {
            @Override
            public void onResponse(Call<GenericResponse<Media>> call, Response<GenericResponse<Media>> response) {
                hideLoading();
                if(response.isSuccessful()){
                    addEpisode(response.body().getResponseData().getId());
                }else{
                    showErrorLoading("An error occurred! Please try again!");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Media>> call, Throwable t) {
                hideLoading();
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Unknown error occured. Please try again!");
                }
            }
        });
    }

    private void addEpisode(String mediaId){
        showsLoading();

        //create Episode
        Integer episodeNumber = this.episodeNmb;
        Integer episodeSeason = this.episodeSeason;
        Episode episode = new Episode(showId,mediaId,episodeName.getText().toString(),episodeDesc.getText().toString(),episodeNumber.toString(),episodeSeason.toString(),true);

        callEpisode = apiService.addEpisode(UserToken.user_token.getTokenString(),episode);
        callEpisode.enqueue(new Callback<GenericResponse<Episode>>() {
            @Override
            public void onResponse(Call<GenericResponse<Episode>> call, Response<GenericResponse<Episode>> response) {
                hideLoading();
                if(response.isSuccessful()){
                    Toast.makeText(EnterNewEpisode.this,"Episode created",Toast.LENGTH_LONG).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }else{
                    showErrorLoading("Unknown error occurred! Please try again!");
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Episode>> call, Throwable t) {
                hideLoading();
                if(call.isCanceled()){
                    //nothing
                }else{
                    showErrorLoading("Unknown error occured! Please try again!");
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            episodeUri = selectedImage;
            displayImage(episodeUri);
        }
        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            episodeUri = data.getData();
            displayImage(episodeUri);
        }
    }

    @Override
    public void onBackPressed(){
        if(!(episodeDesc.getText().toString().equals("") && episodeName.getText().toString().equals(""))){
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?\nYour work won't be saved!")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            EnterNewEpisode.this.finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }else{
            super.onBackPressed();
        }
    }

    public void requestPremisionForCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(EnterNewEpisode.this, new String[]{Manifest.permission.CAMERA},MY_CAMERA_PERMISSION_CODE);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_FOR_GALERY );
        }

    }


    public void requestPremisionForImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_FOR_GALERY );
        }
    }

    public void openGallery(){
        startActivityForResult(Intent.createChooser(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI),"Select file:"),GET_FROM_GALLERY);
    }

    public void openCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void openChooserDialog(){
        final Dialog chooser = new Dialog(this);
        chooser.setContentView(R.layout.chooser_dialog);
        Button cameraBtn = chooser.findViewById(R.id.CameraBtn);
        Button galleryBtn = chooser.findViewById(R.id.GalleryBtn);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EnterNewEpisode.this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }else{
                    requestPremisionForCamera();
                }
                chooser.dismiss();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(EnterNewEpisode.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(EnterNewEpisode.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ){
                    openGallery();
                }else{
                    requestPremisionForImage();
                }
                chooser.dismiss();
            }
        });

        chooser.show();
    }



    /**
     * Dialog for choosing Season and episode.
     */
    public void openDialog(){
        final Dialog d = new Dialog(EnterNewEpisode.this);
        d.setTitle("Season and episode picker");
        d.setContentView(R.layout.numberpicker_dialog);
        final NumberPicker sp = d.findViewById(R.id.seasonPicker);
        final NumberPicker ep = d.findViewById(R.id.episodePicker);
        final Button saveBtn = d.findViewById(R.id.saveButton);
        final String[] returnString = new String[1];
        sp.setMaxValue(20); // max value 100
        sp.setMinValue(1);   // min value 0
        sp.setWrapSelectorWheel(false);
        ep.setMaxValue(99); // max value 100
        ep.setMinValue(1);   // min value 0
        ep.setWrapSelectorWheel(false);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                episodeSeason = sp.getValue();
                episodeNmb = ep.getValue();

                returnString[0] = "Season "+episodeSeason+" "+"Ep "+episodeNmb;
                seasonDisplay.setText(returnString[0]);
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_FOR_GALERY){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
        if(requestCode == MY_CAMERA_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("seasonAndEp",seasonDisplay.getText().toString());
        outState.putParcelable("picture",episodeUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        String seasonAndEps = savedInstanceState.getString("seasonAndEp","default");
        seasonDisplay.setText(seasonAndEps);

        episodeUri = savedInstanceState.getParcelable("picture");
        if(episodeUri != null){
            displayImage(episodeUri);
        }
    }

    public void displayImage(Uri uri){
        imageDisplay.setVisibility(View.VISIBLE);
        imageDisplay.setImageURI(uri);
        textView.setVisibility(View.GONE);
        addPhoto.setVisibility(View.GONE);
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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading episode...");
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(callEpisode != null){
                    callEpisode.cancel();
                }
                if(callMedia != null){
                    callMedia.cancel();
                }
            }
        });
        progressDialog.show();
        ///progressDialog = ProgressDialog.show(this,"","Uploading episode...",true,false,);
    }



    private void hideLoading(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
        if(callMedia != null){
            callMedia.cancel();
        }
        if(callEpisode != null){
            callEpisode.cancel();
        }
        super.onStop();
    }
}
