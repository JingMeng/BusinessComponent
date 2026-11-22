package com.from.business.http.module;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.from.business.http.cache.Cache;
import com.from.business.http.cache.CacheType;
import com.from.business.http.integration.IRepositoryManager;
import com.from.business.http.integration.RepositoryManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * ================================================
 * 鎻愪緵涓€浜涙鏋跺繀椤荤殑瀹炰緥鐨?{@link  }
 * <p>
 * ================================================
 */
public abstract class AppModule {
    static Gson provideGson(Application application, @Nullable GsonConfiguration configuration) {
        GsonBuilder builder = new GsonBuilder();
        if (configuration != null)
            configuration.configGson(application, builder);
        return builder.create();
    }

    abstract IRepositoryManager bindRepositoryManager(RepositoryManager repositoryManager);

    static Cache<String, Object> provideExtras(Cache.Factory cacheFactory) {
        return cacheFactory.build(CacheType.EXTRAS);
    }

    public interface GsonConfiguration {
        void configGson(@NonNull Context context, @NonNull GsonBuilder builder);
    }
}

