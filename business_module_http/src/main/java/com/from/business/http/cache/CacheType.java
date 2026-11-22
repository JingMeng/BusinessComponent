package com.from.business.http.cache;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import androidx.fragment.app.Fragment;

import com.from.business.http.component.HttpComponent;
import com.from.business.http.integration.RepositoryManager;

/**
 * ================================================
 * 鏋勫缓 {@link Cache} 鏃?浣跨敤 {@link CacheType} 涓０鏄庣殑绫诲瀷,鏉ュ尯鍒嗕笉鍚岀殑妯″潡
 * 浠庤€屼负涓嶅悓鐨勬ā鍧楁瀯寤轰笉鍚岀殑缂撳瓨绛栫暐
 *
 * @see Cache.Factory#build(CacheType)
 * ================================================
 */
public interface CacheType {
    int RETROFIT_SERVICE_CACHE_TYPE_ID = 0;
    int CACHE_SERVICE_CACHE_TYPE_ID = 1;
    int EXTRAS_TYPE_ID = 2;
    int ACTIVITY_CACHE_TYPE_ID = 3;
    int FRAGMENT_CACHE_TYPE_ID = 4;
    /**
     * {@link RepositoryManager}涓瓨鍌?Retrofit Service 鐨勫鍣?     */
    CacheType RETROFIT_SERVICE_CACHE = new CacheType() {
        private static final int MAX_SIZE = 150;
        private static final float MAX_SIZE_MULTIPLIER = 0.002f;

        @Override
        public int getCacheTypeId() {
            return RETROFIT_SERVICE_CACHE_TYPE_ID;
        }

        @Override
        public int calculateCacheSize(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int targetMemoryCacheSize = (int) (activityManager.getMemoryClass() * MAX_SIZE_MULTIPLIER * 1024);
            if (targetMemoryCacheSize >= MAX_SIZE) {
                return MAX_SIZE;
            }
            return targetMemoryCacheSize;
        }
    };

    /**
     * {@link RepositoryManager} 涓偍瀛?Cache Service 鐨勫鍣?     */
    CacheType CACHE_SERVICE_CACHE = new CacheType() {
        private static final int MAX_SIZE = 150;
        private static final float MAX_SIZE_MULTIPLIER = 0.002f;

        @Override
        public int getCacheTypeId() {
            return CACHE_SERVICE_CACHE_TYPE_ID;
        }

        @Override
        public int calculateCacheSize(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int targetMemoryCacheSize = (int) (activityManager.getMemoryClass() * MAX_SIZE_MULTIPLIER * 1024);
            if (targetMemoryCacheSize >= MAX_SIZE) {
                return MAX_SIZE;
            }
            return targetMemoryCacheSize;
        }
    };

    /**
     * {@link HttpComponent} 涓殑 extras
     */
    CacheType EXTRAS = new CacheType() {
        private static final int MAX_SIZE = 500;
        private static final float MAX_SIZE_MULTIPLIER = 0.005f;

        @Override
        public int getCacheTypeId() {
            return EXTRAS_TYPE_ID;
        }

        @Override
        public int calculateCacheSize(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int targetMemoryCacheSize = (int) (activityManager.getMemoryClass() * MAX_SIZE_MULTIPLIER * 1024);
            if (targetMemoryCacheSize >= MAX_SIZE) {
                return MAX_SIZE;
            }
            return targetMemoryCacheSize;
        }
    };

    /**
     * {@link Activity} 涓瓨鍌ㄦ暟鎹殑瀹瑰櫒
     */
    CacheType ACTIVITY_CACHE = new CacheType() {
        private static final int MAX_SIZE = 80;
        private static final float MAX_SIZE_MULTIPLIER = 0.0008f;

        @Override
        public int getCacheTypeId() {
            return ACTIVITY_CACHE_TYPE_ID;
        }

        @Override
        public int calculateCacheSize(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int targetMemoryCacheSize = (int) (activityManager.getMemoryClass() * MAX_SIZE_MULTIPLIER * 1024);
            if (targetMemoryCacheSize >= MAX_SIZE) {
                return MAX_SIZE;
            }
            return targetMemoryCacheSize;
        }
    };

    /**
     * {@link Fragment} 涓瓨鍌ㄦ暟鎹殑瀹瑰櫒
     */
    CacheType FRAGMENT_CACHE = new CacheType() {
        private static final int MAX_SIZE = 80;
        private static final float MAX_SIZE_MULTIPLIER = 0.0008f;

        @Override
        public int getCacheTypeId() {
            return FRAGMENT_CACHE_TYPE_ID;
        }

        @Override
        public int calculateCacheSize(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            int targetMemoryCacheSize = (int) (activityManager.getMemoryClass() * MAX_SIZE_MULTIPLIER * 1024);
            if (targetMemoryCacheSize >= MAX_SIZE) {
                return MAX_SIZE;
            }
            return targetMemoryCacheSize;
        }
    };

    /**
     * 杩斿洖妗嗘灦鍐呴渶瑕佺紦瀛樼殑妯″潡瀵瑰簲鐨?{@code id}
     *
     * @return
     */
    int getCacheTypeId();

    /**
     * 璁＄畻瀵瑰簲妯″潡闇€瑕佺殑缂撳瓨澶у皬
     *
     * @return
     */
    int calculateCacheSize(Context context);
}

