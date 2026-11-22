package com.from.business.http.cache;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.from.business.http.module.http.HttpConfigModule;

import java.util.Set;

/**
 * ================================================
 * 鐢ㄤ簬缂撳瓨妗嗘灦涓墍蹇呴渶鐨勭粍浠?寮€鍙戣€呭彲閫氳繃 {@link HttpConfigModule.Builder#cacheFactory(Factory)} 涓烘鏋舵彁渚涚紦瀛樼瓥鐣? * 寮€鍙戣€呬篃鍙互鐢ㄤ簬鑷繁鏃ュ父涓殑浣跨敤
 *
 * @see HttpConfigModule#provideCacheFactory(Application)
 * @see LruCache
 * ================================================
 */
public interface Cache<K, V> {

    interface Factory {

        /**
         * Returns a new cache
         *
         * @param type 妗嗘灦涓渶瑕佺紦瀛樼殑妯″潡绫诲瀷
         * @return {@link Cache}
         */
        @NonNull
        Cache build(CacheType type);
    }

    /**
     * 杩斿洖褰撳墠缂撳瓨宸插崰鐢ㄧ殑鎬?size
     *
     * @return {@code size}
     */
    int size();

    /**
     * 杩斿洖褰撳墠缂撳瓨鎵€鑳藉厑璁哥殑鏈€澶?size
     *
     * @return {@code maxSize}
     */
    int getMaxSize();

    /**
     * 杩斿洖杩欎釜 {@code key} 鍦ㄧ紦瀛樹腑瀵瑰簲鐨?{@code value}, 濡傛灉杩斿洖 {@code null} 璇存槑杩欎釜 {@code key} 娌℃湁瀵瑰簲鐨?{@code value}
     *
     * @param key {@code key}
     * @return {@code value}
     */
    @Nullable
    V get(K key);

    /**
     * 灏?{@code key} 鍜?{@code value} 浠ユ潯鐩殑褰㈠紡鍔犲叆缂撳瓨,濡傛灉杩欎釜 {@code key} 鍦ㄧ紦瀛樹腑宸茬粡鏈夊搴旂殑 {@code value}
     * 鍒欐 {@code value} 琚柊鐨?{@code value} 鏇挎崲骞惰繑鍥?濡傛灉涓?{@code null} 璇存槑鏄竴涓柊鏉＄洰
     *
     * @param key {@code key}
     * @param value {@code value}
     * @return 濡傛灉杩欎釜 {@code key} 鍦ㄥ鍣ㄤ腑宸茬粡鍌ㄥ瓨鏈?{@code value}, 鍒欒繑鍥炰箣鍓嶇殑 {@code value} 鍚﹀垯杩斿洖 {@code null}
     */
    @Nullable
    V put(K key, V value);

    /**
     * 绉婚櫎缂撳瓨涓繖涓?{@code key} 鎵€瀵瑰簲鐨勬潯鐩?骞惰繑鍥炴墍绉婚櫎鏉＄洰鐨?value
     * 濡傛灉杩斿洖涓?{@code null} 鍒欐湁鍙兘鏃跺洜涓鸿繖涓?{@code key} 瀵瑰簲鐨?value 涓?{@code null} 鎴栨潯鐩笉瀛樺湪
     *
     * @param key {@code key}
     * @return 濡傛灉杩欎釜 {@code key} 鍦ㄥ鍣ㄤ腑宸茬粡鍌ㄥ瓨鏈?{@code value} 骞朵笖鍒犻櫎鎴愬姛鍒欒繑鍥炲垹闄ょ殑 {@code value}, 鍚﹀垯杩斿洖 {@code null}
     */
    @Nullable
    V remove(K key);

    /**
     * 濡傛灉杩欎釜 {@code key} 鍦ㄧ紦瀛樹腑鏈夊搴旂殑 value 骞朵笖涓嶄负 {@code null}, 鍒欒繑鍥?{@code true}
     *
     * @param key {@code key}
     * @return {@code true} 涓哄湪瀹瑰櫒涓惈鏈夎繖涓?{@code key}, 鍚﹀垯涓?{@code false}
     */
    boolean containsKey(K key);

    /**
     * 杩斿洖褰撳墠缂撳瓨涓惈鏈夌殑鎵€鏈?{@code key}
     *
     * @return {@code keySet}
     */
    Set<K> keySet();

    /**
     * 娓呴櫎缂撳瓨涓墍鏈夌殑鍐呭
     */
    void clear();
}

