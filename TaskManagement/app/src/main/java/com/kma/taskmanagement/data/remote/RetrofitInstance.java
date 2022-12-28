package com.kma.taskmanagement.data.remote;

import static com.kma.taskmanagement.utils.Constants.BASE_URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

import com.kma.taskmanagement.BuildConfig;
import com.kma.taskmanagement.R;
import com.kma.taskmanagement.TaskApplication;
import com.kma.taskmanagement.utils.Constants;
import com.kma.taskmanagement.utils.SharedPreferencesUtil;
import com.kma.taskmanagement.utils.Utils;
import com.moczul.ok2curl.CurlInterceptor;
import com.moczul.ok2curl.logger.Loggable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static Retrofit retrofit = null;
    private static final int TIMEOUT = 10;
    private static OkHttpClient.Builder httpClientBuilder = null;

    public static AuthService authService;
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

    public static synchronized Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            TokenAuthenticator tokenAuthenticator = new TokenAuthenticator();
            httpClientBuilder = new OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        //.addInterceptor(rewriteRequestInterceptor)
                        .followRedirects(false)
                        .followSslRedirects(false)
                        //.authenticator(tokenAuthenticator)
                        .connectTimeout(1, TimeUnit.MINUTES)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }
                        })
                        .writeTimeout(15, TimeUnit.SECONDS);



            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClientBuilder.build())
                    .build();

            authService = createService(AuthService.class);
        }
        return retrofit;
    }

    private static void initSSL(Context context) {

        SSLContext sslContext = null;
        try {
            sslContext = createCertificate(TaskApplication.getAppContext().getResources().openRawResource(R.raw.local_cert));
        } catch (CertificateException | IOException | KeyStoreException | KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if(sslContext!=null){
            httpClientBuilder.sslSocketFactory(sslContext.getSocketFactory(), systemDefaultTrustManager());
        }

    }

    private static SSLContext createCertificate(InputStream trustedCertificateIS) throws CertificateException, IOException, KeyStoreException, KeyManagementException, NoSuchAlgorithmException{

        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate ca;
        try {
            ca = cf.generateCertificate(trustedCertificateIS);
        } finally {
            trustedCertificateIS.close();
        }

        // creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // creating a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;

    }

    private static X509TrustManager systemDefaultTrustManager() {

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (GeneralSecurityException e) {
            throw new AssertionError(); // The system has no TLS. Just give up.
        }

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

    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        try (InputStream cert = context.getResources().openRawResource(R.raw.local_cert)) {
            ca = cf.generateCertificate(cert);
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        return sslContext;
    }

    public static <S> S createService(Class<S> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
