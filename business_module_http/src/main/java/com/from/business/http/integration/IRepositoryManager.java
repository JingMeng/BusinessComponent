package com.from.business.http.integration;

import android.content.Context;
import androidx.annotation.NonNull;

/**
 * ================================================
 * 鐢ㄦ潵绠＄悊缃戠粶璇锋眰灞?浠ュ強鏁版嵁缂撳瓨灞?浠ュ悗鍙兘娣诲姞鏁版嵁搴撹姹傚眰
 * <p>
 * ================================================
 */
public interface IRepositoryManager {
    /**
     * 鏍规嵁浼犲叆鐨?Class 鑾峰彇瀵瑰簲鐨?Retrofit service
     *
     * @param service Retrofit service class
     * @param <T>     Retrofit service 绫诲瀷
     * @return Retrofit service
     */
    @NonNull
    <T> T obtainRetrofitService(@NonNull Class<T> service);

    /**
     * 鏍规嵁浼犲叆鐨?Class 鑾峰彇瀵瑰簲鐨?RxCache service
     *
     * @param cache RxCache service class
     * @param <T>   RxCache service 绫诲瀷
     * @return RxCache service
     */
    @NonNull
    <T> T obtainCacheService(@NonNull Class<T> cache);

    /**
     * 娓呯悊鎵€鏈夌紦瀛?     */
    void clearAllCache();

    /**
     * 鑾峰彇 {@link Context}
     *
     * @return {@link Context}
     */
    @NonNull
    Context getContext();
}

