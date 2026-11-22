package com.from.business.http.cache;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.from.business.http.utils.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ================================================
 * {@link IntelligentCache} 鍚湁鍙皢鏁版嵁姘镐箙瀛樺偍鑷冲唴瀛樹腑鐨勫瓨鍌ㄥ鍣?{@link #mMap}, 鍜屽綋杈惧埌鏈€澶у閲忔椂鍙牴鎹?LRU
 * 绠楁硶鎶涘純涓嶅悎瑙勬暟鎹殑瀛樺偍瀹瑰櫒 {@link #mCache}
 * <p>
 * {@link IntelligentCache} 鍙牴鎹偍浼犲叆鐨?{@code key} 鏅鸿兘鐨勫垽鏂偍闇€瑕佸皢鏁版嵁瀛樺偍鑷冲摢涓瓨鍌ㄥ鍣? 浠庤€岄拡瀵规暟鎹? * 鐨勪笉鍚岀壒鎬ц繘琛屼笉鍚岀殑瀛樺偍浼樺寲
 * <p>
 * 璋冪敤 {@link IntelligentCache#put(Object, Object)} 鏂规硶, 浣跨敤 {@link #KEY_KEEP} + {@code key} 浣滀负 key 浼犲叆鐨? * {@code value} 鍙瓨鍌ㄨ嚦 {@link #mMap} (鏁版嵁姘镐箙瀛樺偍鑷冲唴瀛樹腑, 閫傚悎姣旇緝閲嶈鐨勬暟鎹? 涓? 鍚﹀垯鍌ㄥ瓨鑷?{@link #mCache}
 * <p>
 * ================================================
 */
public class IntelligentCache<V> implements Cache<String, V> {
    private final Map<String, V> mMap;//鍙皢鏁版嵁姘镐箙瀛樺偍鑷冲唴瀛樹腑鐨勫瓨鍌ㄥ鍣?    private final Cache<String, V> mCache;//褰撹揪鍒版渶澶у閲忔椂鍙牴鎹?LRU 绠楁硶鎶涘純涓嶅悎瑙勬暟鎹殑瀛樺偍瀹瑰櫒
    public static final String KEY_KEEP = "Keep=";

    public IntelligentCache(int size) {
        this.mMap = new HashMap<>();
        this.mCache = new LruCache<>(size);
    }

    /**
     * 灏?{@link #mMap} 鍜?{@link #mCache} 鐨?{@code size} 鐩稿姞鍚庤繑鍥?     *
     * @return 鐩稿姞鍚庣殑 {@code size}
     */
    @Override
    public synchronized int size() {
        return mMap.size() + mCache.size();
    }

    /**
     * 灏?{@link #mMap} 鍜?{@link #mCache} 鐨?{@code maxSize} 鐩稿姞鍚庤繑鍥?     *
     * @return 鐩稿姞鍚庣殑 {@code maxSize}
     */
    @Override
    public synchronized int getMaxSize() {
        return mMap.size() + mCache.getMaxSize();
    }

    /**
     * 濡傛灉鍦?{@code key} 涓娇鐢?{@link #KEY_KEEP} 浣滀负鍏跺墠缂€, 鍒欐搷浣?{@link #mMap}, 鍚﹀垯鎿嶄綔 {@link #mCache}
     *
     * @param key {@code key}
     * @return {@code value}
     */
    @Nullable
    @Override
    public synchronized V get(String key) {
        if (key.startsWith(KEY_KEEP)) {
            return mMap.get(key);
        }
        return mCache.get(key);
    }

    /**
     * 濡傛灉鍦?{@code key} 涓娇鐢?{@link #KEY_KEEP} 浣滀负鍏跺墠缂€, 鍒欐搷浣?{@link #mMap}, 鍚﹀垯鎿嶄綔 {@link #mCache}
     *
     * @param key   {@code key}
     * @param value {@code value}
     * @return 濡傛灉杩欎釜 {@code key} 鍦ㄥ鍣ㄤ腑宸茬粡鍌ㄥ瓨鏈?{@code value}, 鍒欒繑鍥炰箣鍓嶇殑 {@code value} 鍚﹀垯杩斿洖 {@code null}
     */
    @Nullable
    @Override
    public synchronized V put(String key, V value) {
        if (key.startsWith(KEY_KEEP)) {
            return mMap.put(key, value);
        }
        return mCache.put(key, value);
    }

    /**
     * 濡傛灉鍦?{@code key} 涓娇鐢?{@link #KEY_KEEP} 浣滀负鍏跺墠缂€, 鍒欐搷浣?{@link #mMap}, 鍚﹀垯鎿嶄綔 {@link #mCache}
     *
     * @param key {@code key}
     * @return 濡傛灉杩欎釜 {@code key} 鍦ㄥ鍣ㄤ腑宸茬粡鍌ㄥ瓨鏈?{@code value} 骞朵笖鍒犻櫎鎴愬姛鍒欒繑鍥炲垹闄ょ殑 {@code value}, 鍚﹀垯杩斿洖 {@code null}
     */
    @Nullable
    @Override
    public synchronized V remove(String key) {
        if (key.startsWith(KEY_KEEP)) {
            return mMap.remove(key);
        }
        return mCache.remove(key);
    }

    /**
     * 濡傛灉鍦?{@code key} 涓娇鐢?{@link #KEY_KEEP} 浣滀负鍏跺墠缂€, 鍒欐搷浣?{@link #mMap}, 鍚﹀垯鎿嶄綔 {@link #mCache}
     *
     * @param key {@code key}
     * @return {@code true} 涓哄湪瀹瑰櫒涓惈鏈夎繖涓?{@code key}, 鍚﹀垯涓?{@code false}
     */
    @Override
    public synchronized boolean containsKey(String key) {
        if (key.startsWith(KEY_KEEP)) {
            return mMap.containsKey(key);
        }
        return mCache.containsKey(key);
    }

    /**
     * 灏?{@link #mMap} 鍜?{@link #mCache} 鐨?{@code keySet} 鍚堝苟杩斿洖
     *
     * @return 鍚堝苟鍚庣殑 {@code keySet}
     */
    @Override
    public synchronized Set<String> keySet() {
        Set<String> set = mCache.keySet();
        set.addAll(mMap.keySet());
        return set;
    }

    /**
     * 娓呯┖ {@link #mMap} 鍜?{@link #mCache} 瀹瑰櫒
     */
    @Override
    public void clear() {
        mCache.clear();
        mMap.clear();
    }

    /**
     * 浣跨敤姝ゆ柟娉曡繑鍥炵殑鍊间綔涓?key, 鍙互灏嗘暟鎹案涔呭瓨鍌ㄨ嚦鍐呭瓨涓?     *
     * @param key {@code key}
     * @return Keep= + {@code key}
     */
    @NonNull
    public static String getKeyOfKeep(@NonNull String key) {
        Preconditions.checkNotNull(key, "key == null");
        return IntelligentCache.KEY_KEEP + key;
    }
}

