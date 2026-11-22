package com.from.business.http.integration;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.from.business.http.cache.Cache;
import com.from.business.http.cache.CacheType;
import com.from.business.http.utils.Preconditions;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import dagger.Lazy;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.rx_cache2.internal.RxCache;
import retrofit2.Retrofit;

/**
 * ================================================
 * 鐢ㄦ潵绠＄悊缃戠粶璇锋眰灞?浠ュ強鏁版嵁缂撳瓨灞?浠ュ悗鍙兘娣诲姞鏁版嵁搴撹姹傚眰
 * 鎻愪緵缁?{@link  } 灞傚繀瑕佺殑 Api 鍋氭暟鎹鐞? * <p>
 * ================================================
 */
public class RepositoryManager implements IRepositoryManager {
    Lazy<Retrofit> mRetrofit;
    Lazy<RxCache> mRxCache;
    Application mApplication;
    Cache.Factory mCachefactory;
    private Cache<String, Object> mRetrofitServiceCache;
    private Cache<String, Object> mCacheServiceCache;

    public RepositoryManager() {
    }

    /**
     * 鏍规嵁浼犲叆鐨?Class 鑾峰彇瀵瑰簲鐨?Retrofit service
     *
     * @param serviceClass ApiService class
     * @param <T>          ApiService class
     * @return ApiService
     */
    @NonNull
    @Override
    public synchronized <T> T obtainRetrofitService(@NonNull Class<T> serviceClass) {
        return createWrapperService(serviceClass);
    }

    /**
     * 鏍规嵁 https://zhuanlan.zhihu.com/p/40097338 瀵?Retrofit 杩涜鐨勪紭鍖?     *
     * @param serviceClass ApiService class
     * @param <T>          ApiService class
     * @return ApiService
     */
    private <T> T createWrapperService(Class<T> serviceClass) {
        Preconditions.checkNotNull(serviceClass, "serviceClass == null");

        // 浜屾浠ｇ悊
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),
            new Class<?>[]{serviceClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                    throws Throwable {
                    // 姝ゅ鍦ㄨ皟鐢?serviceClass 涓殑鏂规硶鏃惰Е鍙?
                    if (method.getReturnType() == Observable.class) {
                        // 濡傛灉鏂规硶杩斿洖鍊兼槸 Observable 鐨勮瘽锛屽垯鍖呬竴灞傚啀杩斿洖锛?                        // 鍙寘涓€灞?defer 鐢卞閮ㄥ幓鎺у埗鑰楁椂鏂规硶浠ュ強缃戠粶璇锋眰鎵€澶勭嚎绋嬶紝
                        // 濡傛瀵瑰師椤圭洰鐨勫奖鍝嶄负 0锛屼笖鏇村彲鎺с€?                        return Observable.defer(() -> {
                            final T service = getRetrofitService(serviceClass);
                            // 鎵ц鐪熸鐨?Retrofit 鍔ㄦ€佷唬鐞嗙殑鏂规硶
                            return ((Observable) getRetrofitMethod(service, method)
                                .invoke(service, args));
                        });
                    } else if (method.getReturnType() == Single.class) {
                        // 濡傛灉鏂规硶杩斿洖鍊兼槸 Single 鐨勮瘽锛屽垯鍖呬竴灞傚啀杩斿洖銆?                        return Single.defer(() -> {
                            final T service = getRetrofitService(serviceClass);
                            // 鎵ц鐪熸鐨?Retrofit 鍔ㄦ€佷唬鐞嗙殑鏂规硶
                            return ((Single) getRetrofitMethod(service, method)
                                .invoke(service, args));
                        });
                    }

                    // 杩斿洖鍊间笉鏄?Observable 鎴?Single 鐨勮瘽涓嶅鐞嗐€?                    final T service = getRetrofitService(serviceClass);
                    return getRetrofitMethod(service, method).invoke(service, args);
                }
            });
    }

    /**
     * 鏍规嵁浼犲叆鐨?Class 鑾峰彇瀵瑰簲鐨?Retrofit service
     *
     * @param serviceClass ApiService class
     * @param <T>          ApiService class
     * @return ApiService
     */
    private <T> T getRetrofitService(Class<T> serviceClass) {
        if (mRetrofitServiceCache == null) {
            mRetrofitServiceCache = mCachefactory.build(CacheType.RETROFIT_SERVICE_CACHE);
        }
        Preconditions.checkNotNull(mRetrofitServiceCache,
            "Cannot return null from a Cache.Factory#build(int) method");
        T retrofitService = (T) mRetrofitServiceCache.get(serviceClass.getCanonicalName());
        if (retrofitService == null) {
            retrofitService = mRetrofit.get().create(serviceClass);
            mRetrofitServiceCache.put(serviceClass.getCanonicalName(), retrofitService);
        }
        return retrofitService;
    }

    private <T> Method getRetrofitMethod(T service, Method method) throws NoSuchMethodException {
        return service.getClass().getMethod(method.getName(), method.getParameterTypes());
    }

    /**
     * 鏍规嵁浼犲叆鐨?Class 鑾峰彇瀵瑰簲鐨?RxCache service
     *
     * @param cacheClass Cache class
     * @param <T>        Cache class
     * @return Cache
     */
    @NonNull
    @Override
    public synchronized <T> T obtainCacheService(@NonNull Class<T> cacheClass) {
        Preconditions.checkNotNull(cacheClass, "cacheClass == null");
        if (mCacheServiceCache == null) {
            mCacheServiceCache = mCachefactory.build(CacheType.CACHE_SERVICE_CACHE);
        }
        Preconditions.checkNotNull(mCacheServiceCache,
            "Cannot return null from a Cache.Factory#build(int) method");
        T cacheService = (T) mCacheServiceCache.get(cacheClass.getCanonicalName());
        if (cacheService == null) {
            cacheService = mRxCache.get().using(cacheClass);
            mCacheServiceCache.put(cacheClass.getCanonicalName(), cacheService);
        }
        return cacheService;
    }

    /**
     * 娓呯悊鎵€鏈夌紦瀛?     */
    @Override
    public void clearAllCache() {
        mRxCache.get().evictAll().subscribe();
    }

    @NonNull
    @Override
    public Context getContext() {
        return mApplication;
    }
}

