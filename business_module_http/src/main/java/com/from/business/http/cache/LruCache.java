package com.from.business.http.cache;

import android.app.Application;
import androidx.annotation.Nullable;

import com.from.business.http.module.http.HttpConfigModule;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * ================================================
 * LRU 鍗?Least Recently Used,鏈€杩戞渶灏戜娇鐢?涔熷氨鏄,褰撶紦瀛樻弧浜?浼氫紭鍏堟窐姹伴偅浜涙渶杩戞渶涓嶅父璁块棶鐨勬暟鎹? * 姝ょ缂撳瓨绛栫暐涓烘鏋堕粯璁ゆ彁渚?鍙嚜琛屽疄鐜板叾浠栫紦瀛樼瓥鐣?濡傜鐩樼紦瀛?涓烘鏋舵垨寮€鍙戣€呮彁渚涚紦瀛樼殑鍔熻兘
 *
 * @see HttpConfigModule#provideCacheFactory(Application)
 * @see Cache
 * ================================================
 */
public class LruCache<K, V> implements Cache<K, V> {
    private final LinkedHashMap<K, V> cache = new LinkedHashMap<>(100, 0.75f, true);
    private final int initialMaxSize;
    private int maxSize;
    private int currentSize = 0;

    /**
     * Constructor for LruCache.
     *
     * @param size 杩欎釜缂撳瓨鐨勬渶澶?size,杩欎釜 size 鎵€浣跨敤鐨勫崟浣嶅繀椤诲拰 {@link #getItemSize(Object)} 鎵€浣跨敤鐨勫崟浣嶄竴鑷?
     */
    public LruCache(int size) {
        this.initialMaxSize = size;
        this.maxSize = size;
    }

    /**
     * 璁剧疆涓€涓郴鏁板簲鐢ㄤ簬褰撴椂鏋勯€犲嚱鏁颁腑鎵€浼犲叆鐨?size, 浠庤€屽緱鍒颁竴涓柊鐨?{@link #maxSize}
     * 骞朵細绔嬪嵆璋冪敤 {@link #evict} 寮€濮嬫竻闄ゆ弧瓒虫潯浠剁殑鏉＄洰
     *
     * @param multiplier 绯绘暟
     */
    public synchronized void setSizeMultiplier(float multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("Multiplier must be >= 0");
        }
        maxSize = Math.round(initialMaxSize * multiplier);
        evict();
    }

    /**
     * 杩斿洖姣忎釜 {@code item} 鎵€鍗犵敤鐨?size,榛樿涓?,杩欎釜 size 鐨勫崟浣嶅繀椤诲拰鏋勯€犲嚱鏁版墍浼犲叆鐨?size 涓€鑷?     * 瀛愮被鍙互閲嶅啓杩欎釜鏂规硶浠ラ€傚簲涓嶅悓鐨勫崟浣?姣斿璇?bytes
     *
     * @param item 姣忎釜 {@code item} 鎵€鍗犵敤鐨?size
     * @return 鍗曚釜 item 鐨?{@code size}
     */
    protected int getItemSize(V item) {
        return 1;
    }

    /**
     * 褰撶紦瀛樹腑鏈夎椹遍€愮殑鏉＄洰鏃?浼氬洖璋冩鏂规硶,榛樿绌哄疄鐜?瀛愮被鍙互閲嶅啓杩欎釜鏂规硶
     *
     * @param key   琚┍閫愭潯鐩殑 {@code key}
     * @param value 琚┍閫愭潯鐩殑 {@code value}
     */
    protected void onItemEvicted(K key, V value) {
        // optional override
    }

    /**
     * 杩斿洖褰撳墠缂撳瓨鎵€鑳藉厑璁哥殑鏈€澶?size
     *
     * @return {@code maxSize}
     */
    @Override
    public synchronized int getMaxSize() {
        return maxSize;
    }

    /**
     * 杩斿洖褰撳墠缂撳瓨宸插崰鐢ㄧ殑鎬?size
     *
     * @return {@code size}
     */
    @Override
    public synchronized int size() {
        return currentSize;
    }

    /**
     * 濡傛灉杩欎釜 {@code key} 鍦ㄧ紦瀛樹腑鏈夊搴旂殑 {@code value} 骞朵笖涓嶄负 {@code null},鍒欒繑鍥?true
     *
     * @param key 鐢ㄦ潵鏄犲皠鐨?{@code key}
     * @return {@code true} 涓哄湪瀹瑰櫒涓惈鏈夎繖涓?{@code key}, 鍚﹀垯涓?{@code false}
     */
    @Override
    public synchronized boolean containsKey(K key) {
        return cache.containsKey(key);
    }

    /**
     * 杩斿洖褰撳墠缂撳瓨涓惈鏈夌殑鎵€鏈?{@code key}
     *
     * @return {@code keySet}
     */
    @Override
    public synchronized Set<K> keySet() {
        return cache.keySet();
    }

    /**
     * 杩斿洖杩欎釜 {@code key} 鍦ㄧ紦瀛樹腑瀵瑰簲鐨?{@code value}, 濡傛灉杩斿洖 {@code null} 璇存槑杩欎釜 {@code key} 娌℃湁瀵瑰簲鐨?{@code value}
     *
     * @param key 鐢ㄦ潵鏄犲皠鐨?{@code key}
     * @return {@code value}
     */
    @Override
    @Nullable
    public synchronized V get(K key) {
        return cache.get(key);
    }

    /**
     * 灏?{@code key} 鍜?{@code value} 浠ユ潯鐩殑褰㈠紡鍔犲叆缂撳瓨,濡傛灉杩欎釜 {@code key} 鍦ㄧ紦瀛樹腑宸茬粡鏈夊搴旂殑 {@code value}
     * 鍒欐 {@code value} 琚柊鐨?{@code value} 鏇挎崲骞惰繑鍥?濡傛灉涓?{@code null} 璇存槑鏄竴涓柊鏉＄洰
     * <p>
     * 濡傛灉 {@link #getItemSize} 杩斿洖鐨?size 澶т簬鎴栫瓑浜庣紦瀛樻墍鑳藉厑璁哥殑鏈€澶?size, 鍒欎笉鑳藉悜缂撳瓨涓坊鍔犳鏉＄洰
     * 姝ゆ椂浼氬洖璋?{@link #onItemEvicted(Object, Object)} 閫氱煡姝ゆ柟娉曞綋鍓嶈椹遍€愮殑鏉＄洰
     *
     * @param key   閫氳繃杩欎釜 {@code key} 娣诲姞鏉＄洰
     * @param value 闇€瑕佹坊鍔犵殑 {@code value}
     * @return 濡傛灉杩欎釜 {@code key} 鍦ㄥ鍣ㄤ腑宸茬粡鍌ㄥ瓨鏈?{@code value}, 鍒欒繑鍥炰箣鍓嶇殑 {@code value} 鍚﹀垯杩斿洖 {@code null}
     */
    @Override
    @Nullable
    public synchronized V put(K key, V value) {
        final int itemSize = getItemSize(value);
        if (itemSize >= maxSize) {
            onItemEvicted(key, value);
            return null;
        }

        final V result = cache.put(key, value);
        if (value != null) {
            currentSize += getItemSize(value);
        }
        if (result != null) {
            currentSize -= getItemSize(result);
        }
        evict();

        return result;
    }

    /**
     * 绉婚櫎缂撳瓨涓繖涓?{@code key} 鎵€瀵瑰簲鐨勬潯鐩?骞惰繑鍥炴墍绉婚櫎鏉＄洰鐨?{@code value}
     * 濡傛灉杩斿洖涓?{@code null} 鍒欐湁鍙兘鏃跺洜涓鸿繖涓?{@code key} 瀵瑰簲鐨?{@code value} 涓?{@code null} 鎴栨潯鐩笉瀛樺湪
     *
     * @param key 浣跨敤杩欎釜 {@code key} 绉婚櫎瀵瑰簲鐨勬潯鐩?     * @return 濡傛灉杩欎釜 {@code key} 鍦ㄥ鍣ㄤ腑宸茬粡鍌ㄥ瓨鏈?{@code value} 骞朵笖鍒犻櫎鎴愬姛鍒欒繑鍥炲垹闄ょ殑 {@code value}, 鍚﹀垯杩斿洖 {@code null}
     */
    @Override
    @Nullable
    public synchronized V remove(K key) {
        final V value = cache.remove(key);
        if (value != null) {
            currentSize -= getItemSize(value);
        }
        return value;
    }

    /**
     * 娓呴櫎缂撳瓨涓墍鏈夌殑鍐呭
     */
    @Override
    public void clear() {
        trimToSize(0);
    }

    /**
     * 褰撴寚瀹氱殑 size 灏忎簬褰撳墠缂撳瓨宸插崰鐢ㄧ殑鎬?size 鏃?浼氬紑濮嬫竻闄ょ紦瀛樹腑鏈€杩戞渶灏戜娇鐢ㄧ殑鏉＄洰
     *
     * @param size {@code size}
     */
    protected synchronized void trimToSize(int size) {
        Map.Entry<K, V> last;
        while (currentSize > size) {
            last = cache.entrySet().iterator().next();
            final V toRemove = last.getValue();
            currentSize -= getItemSize(toRemove);
            final K key = last.getKey();
            cache.remove(key);
            onItemEvicted(key, toRemove);
        }
    }

    /**
     * 褰撶紦瀛樹腑宸插崰鐢ㄧ殑鎬?size 澶т簬鎵€鑳藉厑璁哥殑鏈€澶?size ,浼氫娇鐢? {@link #trimToSize(int)} 寮€濮嬫竻闄ゆ弧瓒虫潯浠剁殑鏉＄洰
     */
    private void evict() {
        trimToSize(maxSize);
    }
}


