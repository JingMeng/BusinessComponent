package com.from.business.http.module;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.from.business.http.HttpHandler;
import com.from.business.http.log.RequestInterceptor;
import com.from.business.http.utils.DataHelper;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ================================================
 * 鎻愪緵涓€浜涗笁鏂瑰簱瀹㈡埛绔疄渚嬬殑 {@link  }
 * <p>
 * ================================================
 */
public abstract class ClientModule {
    private static final int TIME_OUT = 10;

    /**
     * 鎻愪緵 {@link Retrofit}
     *
     * @param application   {@link Application}
     * @param configuration {@link RetrofitConfiguration}
     * @param builder       {@link Retrofit.Builder}
     * @param client        {@link OkHttpClient}
     * @param httpUrl       {@link HttpUrl}
     * @param gson          {@link Gson}
     * @return {@link Retrofit}
     */
    static Retrofit provideRetrofit(Application application, @Nullable RetrofitConfiguration configuration, Retrofit.Builder builder, OkHttpClient client
        , HttpUrl httpUrl, Gson gson) {
        builder
            .baseUrl(httpUrl)//鍩熷悕
            .client(client);//璁剧疆 OkHttp

        if (configuration != null)
            configuration.configRetrofit(application, builder);

        builder
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//浣跨敤 RxJava
            .addConverterFactory(GsonConverterFactory.create(gson));//浣跨敤 Gson
        return builder.build();
    }

    /**
     * 鎻愪緵 {@link OkHttpClient}
     *
     * @param application     {@link Application}
     * @param configuration   {@link OkhttpConfiguration}
     * @param builder         {@link OkHttpClient.Builder}
     * @param intercept       {@link Interceptor}
     * @param interceptors    {@link List<Interceptor>}
     * @param handler         {@link HttpHandler}
     * @param executorService {@link ExecutorService}
     * @return {@link OkHttpClient}
     */
    static OkHttpClient provideClient(Application application, @Nullable OkhttpConfiguration configuration, OkHttpClient.Builder builder, Interceptor intercept
        , @Nullable List<Interceptor> interceptors, @Nullable HttpHandler handler, ExecutorService executorService) {
        builder
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(intercept);

        if (handler != null)
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    return chain.proceed(handler.onHttpRequestBefore(chain, chain.request()));
                }
            });

        //濡傛灉澶栭儴鎻愪緵浜?Interceptor 鐨勯泦鍚堝垯閬嶅巻娣诲姞
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                builder.addInterceptor(interceptor);
            }
        }

        //涓?OkHttp 璁剧疆榛樿鐨勭嚎绋嬫睜
        builder.dispatcher(new Dispatcher(executorService));

        if (configuration != null)
            configuration.configOkhttp(application, builder);
        return builder.build();
    }

    static Retrofit.Builder provideRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    static OkHttpClient.Builder provideClientBuilder() {
        return new OkHttpClient.Builder();
    }

    abstract Interceptor bindInterceptor(RequestInterceptor interceptor);

    /**
     * 鎻愪緵 {@link RxCache}
     *
     * @param application    {@link Application}
     * @param configuration  {@link RxCacheConfiguration}
     * @param cacheDirectory RxCache 缂撳瓨璺緞
     * @param gson           {@link Gson}
     * @return {@link RxCache}
     */
    static RxCache provideRxCache(Application application, @Nullable RxCacheConfiguration configuration
        , File cacheDirectory, Gson gson) {
        RxCache.Builder builder = new RxCache.Builder();
        RxCache rxCache = null;
        if (configuration != null) {
            rxCache = configuration.configRxCache(application, builder);
        }
        if (rxCache != null) return rxCache;
        return builder
            .persistence(cacheDirectory, new GsonSpeaker(gson));
    }

    /**
     * 闇€瑕佸崟鐙粰 {@link RxCache} 鎻愪緵瀛愮紦瀛樻枃浠?     *
     * @param cacheDir 妗嗘灦缂撳瓨鏂囦欢
     * @return {@link File}
     */
    static File provideRxCacheDirectory(File cacheDir) {
        File cacheDirectory = new File(cacheDir, "RxCache");
        return DataHelper.makeDirs(cacheDirectory);
    }

    /**
     * 鎻愪緵澶勭悊 RxJava 閿欒鐨勭鐞嗗櫒
     *
     * @param application {@link Application}
     * @param listener    {@link ResponseErrorListener}
     * @return {@link RxErrorHandler}
     */
    static RxErrorHandler proRxErrorHandler(Application application, ResponseErrorListener listener) {
        return RxErrorHandler
            .builder()
            .with(application)
            .responseErrorListener(listener)
            .build();
    }

    /**
     * {@link Retrofit} 鑷畾涔夐厤缃帴鍙?     */
    public interface RetrofitConfiguration {
        void configRetrofit(@NonNull Context context, @NonNull Retrofit.Builder builder);
    }

    /**
     * {@link OkHttpClient} 鑷畾涔夐厤缃帴鍙?     */
    public interface OkhttpConfiguration {
        void configOkhttp(@NonNull Context context, @NonNull OkHttpClient.Builder builder);
    }

    /**
     * {@link RxCache} 鑷畾涔夐厤缃帴鍙?     */
    public interface RxCacheConfiguration {
        /**
         * 鑻ユ兂鑷畾涔?RxCache 鐨勭紦瀛樻枃浠跺す鎴栬€呰В鏋愭柟寮? 濡傛敼鎴?FastJson
         * 璇?{@code return rxCacheBuilder.persistence(cacheDirectory, new FastJsonSpeaker());}, 鍚﹀垯璇?{@code return null;}
         *
         * @param context {@link Context}
         * @param builder {@link RxCache.Builder}
         * @return {@link RxCache}
         */
        RxCache configRxCache(@NonNull Context context, @NonNull RxCache.Builder builder);
    }
}

