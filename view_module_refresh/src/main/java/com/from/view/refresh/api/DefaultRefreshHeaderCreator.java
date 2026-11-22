package com.from.view.refresh.api;

import android.content.Context;
import androidx.annotation.NonNull;

/**
 * 榛樿Header鍒涘缓鍣? * Created by SCWANG on 2018/1/26.
 */
public interface DefaultRefreshHeaderCreator {
    @NonNull
    RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout);
}

