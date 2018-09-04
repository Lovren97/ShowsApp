package co.infinum.showsapp;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import co.infinum.showsapp.Entities.UserToken;
import co.infinum.showsapp.Network.ApiService;
import co.infinum.showsapp.Entities.Token;
import co.infinum.showsapp.Network.InitApiService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static co.infinum.showsapp.MainActivity.BASE_URL;

public class SplashActivity extends AppCompatActivity {

    //variable definition
    private TextView textView;
    private ImageView imageView;
    private ProgressBar progressBar;
    private boolean homeButton = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //variable initialisation
        textView = findViewById(R.id.showsss);
        imageView = findViewById(R.id.imagesss);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        //Initialization of static api service
        InitApiService.initApiService();

        //start of splash animation
        startBouncingAnimation();

    }


    private void startTextAnimation(){
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 30f);
        animator.setDuration(1000);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedValue = (float) valueAnimator.getAnimatedValue();
                textView.setTextSize(animatedValue);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }
            @Override
            public void onAnimationEnd(Animator animator) {
                startLoading();
            }
            @Override
            public void onAnimationCancel(Animator animator) {
            }
            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        textView.setTextColor(Color.BLACK);
        animator.start();

    }



    private void startBouncingAnimation(){
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.boune_translation);
        imageView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                startTextAnimation();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }


    /**
     * Function that displays circular progressBar
     */
    private void startLoading() {
        progressBar.setVisibility(View.VISIBLE);

        progressBar.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkIfRememberMeAccExists();
            }
        },2000);
    }


    /**
     * Function that checks if user has checked remember me checkbox
     *
     * If user has checked it, the app will show shows list,otherwise
     * it will start login screen
     */
    private void checkIfRememberMeAccExists(){
        SharedPreferences loginPref = getSharedPreferences("login", MODE_PRIVATE);
        homeButton = false;
        UserToken.email = loginPref.getString("email",null);
        if(loginPref.getBoolean("checkbox",false)){
            UserToken.user_token = new Token(loginPref.getString("token",null));
            Intent intent = new Intent(SplashActivity.this,ShowsActivity.class);
            startActivity(intent);
            finish();
        }else{
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    /**
     * Overriding method to handle if user presses Back button during animation screen
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    /**
     * Overriding method to handle if user presses Home button during animation screen
     */
    @Override
    protected void onStop() {
        super.onStop();
        if(homeButton){
            onBackPressed();
        }
    }
}
