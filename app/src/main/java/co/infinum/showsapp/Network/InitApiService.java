package co.infinum.showsapp.Network;

import co.infinum.showsapp.SplashActivity;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static co.infinum.showsapp.MainActivity.BASE_URL;

/**
 * Created by Ivan Lovrencic on 3.8.2018..
 */

public class InitApiService {

    public static ApiService apiService;

    public static void initApiService(){
        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create())
                .client(createOkHttpClient())
                .build()
                .create(ApiService.class);
    }

    private static OkHttpClient createOkHttpClient(){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

}
