package com.kma.taskmanagement.data.remote;

import static com.kma.taskmanagement.utils.Constants.BASE_URL;

import android.util.Log;

import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit = null;

    public static Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new CurlInterceptor(new Loggable() {
                @Override
                public void log(String message) {
                    Log.v("Ok2Curl", message);
                }
            })).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
