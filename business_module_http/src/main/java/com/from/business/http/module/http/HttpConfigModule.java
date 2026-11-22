/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.from.business.http.module.http;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.from.business.http.BaseUrl;
import com.from.business.http.HttpHandler;
import com.from.business.http.cache.Cache;
import com.from.business.http.cache.CacheType;
import com.from.business.http.cache.IntelligentCache;
import com.from.business.http.cache.LruCache;
import com.from.business.http.log.DefaultFormatPrinter;
import com.from.business.http.log.FormatPrinter;
import com.from.business.http.log.RequestInterceptor;
import com.from.business.http.module.AppModule;
import com.from.business.http.module.ClientModule;
import com.from.business.http.utils.DataHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.internal.Util;

import static com.from.business.http.utils.Preconditions.checkNotNull;

/**
 * ================================================
 * 妗嗘灦鐙垱鐨勫缓閫犺€呮ā寮?{@link },鍙悜妗嗘灦涓敞鍏ュ閮ㄩ厤缃殑鑷畾涔夊弬鏁? * <p>
 * ================================================
 */
public class HttpConfigModule {
    private HttpUrl mApiUrl;
    private BaseUrl mBaseUrl;
    private HttpHandler mHandler;
    private List<Interceptor> mInterceptors;
    private ResponseErrorListener mErrorListener;
    private File mCacheFile;
    private ClientModule.RetrofitConfiguration mRetrofitConfiguration;
    private ClientModule.OkhttpConfiguration mOkhttpConfiguration;
    private ClientModule.RxCacheConfiguration mRxCacheConfiguration;
    private AppModule.GsonConfiguration mGsonConfiguration;
    private RequestInterceptor.Level mPrintHttpLogLevel;
    private FormatPrinter mFormatPrinter;
    private Cache.Factory mCacheFactory;
    private ExecutorService mExecutorService;

    private HttpConfigModule(Builder builder) {
        this.mApiUrl = builder.apiUrl;
        this.mBaseUrl = builder.baseUrl;
        this.mHandler = builder.handler;
        this.mInterceptors = builder.interceptors;
        this.mErrorListener = builder.responseErrorListener;
        this.mCacheFile = builder.cacheFile;
        this.mRetrofitConfiguration = builder.retrofitConfiguration;
        this.mOkhttpConfiguration = builder.okhttpConfiguration;
        this.mRxCacheConfiguration = builder.rxCacheConfiguration;
        this.mGsonConfiguration = builder.gsonConfiguration;
        this.mPrintHttpLogLevel = builder.printHttpLogLevel;
        this.mFormatPrinter = builder.formatPrinter;
        this.mCacheFactory = builder.cacheFactory;
        this.mExecutorService = builder.executorService;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    List<Interceptor> provideInterceptors() {
        return mInterceptors;
    }

    /**
     * 鎻愪緵 BaseUrl,榛樿浣跨敤 <"https://api.github.com/">
     *
     * @return
     */
    HttpUrl provideBaseUrl() {
        if (mBaseUrl != null) {
            HttpUrl httpUrl = mBaseUrl.url();
            if (httpUrl != null) {
                return httpUrl;
            }
        }
        return mApiUrl == null ? HttpUrl.parse("https://api.github.com/") : mApiUrl;
    }

    /**
     * 鎻愪緵澶勭悊 Http 璇锋眰鍜屽搷搴旂粨鏋滅殑澶勭悊绫?     *
     * @return
     */
    @Nullable
    HttpHandler provideGlobalHttpHandler() {
        return mHandler;
    }

    /**
     * 鎻愪緵缂撳瓨鏂囦欢
     */
    File provideCacheFile(Application application) {
        return mCacheFile == null ? DataHelper.getCacheFile(application) : mCacheFile;
    }

    /**
     * 鎻愪緵澶勭悊 RxJava 閿欒鐨勭鐞嗗櫒鐨勫洖璋?     *
     * @return
     */
    ResponseErrorListener provideResponseErrorListener() {
        return mErrorListener == null ? ResponseErrorListener.EMPTY : mErrorListener;
    }

    @Nullable
    ClientModule.RetrofitConfiguration provideRetrofitConfiguration() {
        return mRetrofitConfiguration;
    }

    @Nullable
    ClientModule.OkhttpConfiguration provideOkhttpConfiguration() {
        return mOkhttpConfiguration;
    }

    @Nullable
    ClientModule.RxCacheConfiguration provideRxCacheConfiguration() {
        return mRxCacheConfiguration;
    }

    @Nullable
    AppModule.GsonConfiguration provideGsonConfiguration() {
        return mGsonConfiguration;
    }

    RequestInterceptor.Level providePrintHttpLogLevel() {
        return mPrintHttpLogLevel == null ? RequestInterceptor.Level.ALL : mPrintHttpLogLevel;
    }

    FormatPrinter provideFormatPrinter() {
        return mFormatPrinter == null ? new DefaultFormatPrinter() : mFormatPrinter;
    }

    Cache.Factory provideCacheFactory(Application application) {
        return mCacheFactory == null ? new Cache.Factory() {
            @NonNull
            @Override
            public Cache build(CacheType type) {
                //鑻ユ兂鑷畾涔?LruCache 鐨?size, 鎴栬€呬笉鎯充娇鐢?LruCache, 鎯充娇鐢ㄨ嚜宸辫嚜瀹氫箟鐨勭瓥鐣?                //浣跨敤 GlobalConfigModule.Builder#cacheFactory() 鍗冲彲鎵╁睍
                switch (type.getCacheTypeId()) {
                    //Activity銆丗ragment 浠ュ強 Extras 浣跨敤 IntelligentCache (鍏锋湁 LruCache 鍜?鍙案涔呭瓨鍌ㄦ暟鎹殑 Map)
                    case CacheType.EXTRAS_TYPE_ID:
                    case CacheType.ACTIVITY_CACHE_TYPE_ID:
                    case CacheType.FRAGMENT_CACHE_TYPE_ID:
                        return new IntelligentCache(type.calculateCacheSize(application));
                    //鍏朵綑浣跨敤 LruCache (褰撹揪鍒版渶澶у閲忔椂鍙牴鎹?LRU 绠楁硶鎶涘純涓嶅悎瑙勬暟鎹?
                    default:
                        return new LruCache(type.calculateCacheSize(application));
                }
            }
        } : mCacheFactory;
    }

    /**
     * 杩斿洖涓€涓叏灞€鍏敤鐨勭嚎绋嬫睜,閫傜敤浜庡ぇ澶氭暟寮傛闇€姹傘€?     * 閬垮厤澶氫釜绾跨▼姹犲垱寤哄甫鏉ョ殑璧勬簮娑堣€椼€?     *
     * @return {@link Executor}
     */
    ExecutorService provideExecutorService() {
        return mExecutorService == null ? new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(), Util.threadFactory("Arms Executor", false)) : mExecutorService;
    }

    public static final class Builder {
        private HttpUrl apiUrl;
        private BaseUrl baseUrl;
        private HttpHandler handler;
        private List<Interceptor> interceptors;
        private ResponseErrorListener responseErrorListener;
        private File cacheFile;
        private ClientModule.RetrofitConfiguration retrofitConfiguration;
        private ClientModule.OkhttpConfiguration okhttpConfiguration;
        private ClientModule.RxCacheConfiguration rxCacheConfiguration;
        private AppModule.GsonConfiguration gsonConfiguration;
        private RequestInterceptor.Level printHttpLogLevel;
        private FormatPrinter formatPrinter;
        private Cache.Factory cacheFactory;
        private ExecutorService executorService;

        private Builder() {
        }

        public Builder baseurl(String baseUrl) {//鍩虹url
            if (TextUtils.isEmpty(baseUrl)) {
                throw new NullPointerException("BaseUrl can not be empty");
            }
            this.apiUrl = HttpUrl.parse(baseUrl);
            return this;
        }

        public Builder baseurl(BaseUrl baseUrl) {
            this.baseUrl = checkNotNull(baseUrl, BaseUrl.class.getCanonicalName() + "can not be null.");
            return this;
        }

        public Builder globalHttpHandler(HttpHandler handler) {//鐢ㄦ潵澶勭悊http鍝嶅簲缁撴灉
            this.handler = handler;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {//鍔ㄦ€佹坊鍔犱换鎰忎釜interceptor
            if (interceptors == null)
                interceptors = new ArrayList<>();
            this.interceptors.add(interceptor);
            return this;
        }

        public Builder responseErrorListener(ResponseErrorListener listener) {//澶勭悊鎵€鏈塕xJava鐨刼nError閫昏緫
            this.responseErrorListener = listener;
            return this;
        }

        public Builder cacheFile(File cacheFile) {
            this.cacheFile = cacheFile;
            return this;
        }

        public Builder retrofitConfiguration(ClientModule.RetrofitConfiguration retrofitConfiguration) {
            this.retrofitConfiguration = retrofitConfiguration;
            return this;
        }

        public Builder okhttpConfiguration(ClientModule.OkhttpConfiguration okhttpConfiguration) {
            this.okhttpConfiguration = okhttpConfiguration;
            return this;
        }

        public Builder rxCacheConfiguration(ClientModule.RxCacheConfiguration rxCacheConfiguration) {
            this.rxCacheConfiguration = rxCacheConfiguration;
            return this;
        }

        public Builder gsonConfiguration(AppModule.GsonConfiguration gsonConfiguration) {
            this.gsonConfiguration = gsonConfiguration;
            return this;
        }

        public Builder printHttpLogLevel(RequestInterceptor.Level printHttpLogLevel) {//鏄惁璁╂鏋舵墦鍗?Http 鐨勮姹傚拰鍝嶅簲淇℃伅
            this.printHttpLogLevel = checkNotNull(printHttpLogLevel, "The printHttpLogLevel can not be null, use RequestInterceptor.Level.NONE instead.");
            return this;
        }

        public Builder formatPrinter(FormatPrinter formatPrinter) {
            this.formatPrinter = checkNotNull(formatPrinter, FormatPrinter.class.getCanonicalName() + "can not be null.");
            return this;
        }

        public Builder cacheFactory(Cache.Factory cacheFactory) {
            this.cacheFactory = cacheFactory;
            return this;
        }

        public Builder executorService(ExecutorService executorService) {
            this.executorService = executorService;
            return this;
        }

        public HttpConfigModule build() {
            return new HttpConfigModule(this);
        }
    }
}

