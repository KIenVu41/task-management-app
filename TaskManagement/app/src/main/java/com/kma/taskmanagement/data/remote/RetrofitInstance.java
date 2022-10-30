package com.kma.taskmanagement.data.remote;

import static com.kma.taskmanagement.utils.Constants.BASE_URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kma.taskmanagement.BuildConfig;
import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.Utils;
import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit = null;
    private static final int TIMEOUT = 10;
    /**
     * The overridden cache duration to keep data from GET requests.
     * Currently set to 10 minutes
     */
    private static final int CACHE_DURATION_MIN = 10;
    /**
     * The overridden cache duration to keep data from GET requests.
     * Currently set to 10 minutes
     */
    private static final long CACHE_DURATION_SEC = 600;

    /**
     * The overridden stale duration in seconds.
     * Currently set to 7 days
     */
    private static final int STALE_DURATION_DAYS = 7;

    /**
     * Max size of OkHTTP cache -- currently 50 MB
     */
    private static final int HTTP_CACHE_MAX_SIZE = 50 * 1024 * 1024;

    /**
     * The directory name to place cache files from OkHttp. This directory will be placed in the
     * app's internal cache directory (or external if this is a debug build)
     */
    private static final String HTTP_CACHE_DIR = "kma_tasks_cache";

    public static Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            Cache myCache = getHttpCache();
            OkHttpClient client = new OkHttpClient.Builder()
                    //.addInterceptor(interceptor)
                    .addInterceptor(rewriteRequestInterceptor)
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .cache(myCache)
                    .addNetworkInterceptor(REWRITE_RESPONSE_CACHE_CONTROL_INTERCEPTOR)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

    private static Cache getHttpCache() {
        final File cacheDir;
        if (BuildConfig.DEBUG) {
            cacheDir = new File(TaskApplication.getAppContext().getExternalCacheDir(), HTTP_CACHE_DIR);
        } else {
            cacheDir = new File(TaskApplication.getAppContext().getCacheDir(), HTTP_CACHE_DIR);
        }
        return new Cache(cacheDir, HTTP_CACHE_MAX_SIZE);
    }

    private static final Interceptor rewriteRequestInterceptor = new Interceptor() {
        @Override
        public Response intercept(final Chain chain) throws IOException {
            Request request = chain.request();

            if (request.cacheControl().noCache()) {
                return chain.proceed(request);
            }

            if (Utils.isNetworkConnected(TaskApplication.getAppContext())) {
                // the data can be reused for CACHE_DURATION_MIN
                request = request.newBuilder()
                        .cacheControl(new CacheControl.Builder().maxStale(CACHE_DURATION_MIN, TimeUnit.MINUTES).build())
                        .build();
            }
            else {
                // for offline
                request = request.newBuilder()
                        .cacheControl(new CacheControl.Builder().onlyIfCached()
                                .maxStale(STALE_DURATION_DAYS,
                                        TimeUnit.DAYS)
                                .build())
                        .build();
            }

            return chain.proceed(request);
        }
    };



    // this for only response interceptor is invoked for online responses
    private static final Interceptor REWRITE_RESPONSE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            int maxAge = originalResponse.cacheControl().maxAgeSeconds();
            if (maxAge <= 0) {
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Expires")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", String.format(Locale.ENGLISH, "max-age=%d, only-if-cached, max-stale=%d", CACHE_DURATION_SEC, 0))
                        .build();
            } else {
                return originalResponse;
            }
        }
    };


    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
