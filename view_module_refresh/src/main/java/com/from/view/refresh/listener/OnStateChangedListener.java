package com.from.view.refresh.listener;


import androidx.annotation.NonNull;
import android.support.annotation.RestrictTo;

import com.from.view.refresh.api.RefreshLayout;
import com.from.view.refresh.constant.RefreshState;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;
import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static android.support.annotation.RestrictTo.Scope.SUBCLASSES;

/**
 * 鍒锋柊鐘舵€佹敼鍙樼洃鍚櫒
 * Created by SCWANG on 2017/5/26.
 */

public interface OnStateChangedListener {
    /**
     * 鐘舵€佹敼鍙樹簨浠?{@link RefreshState}
     * @param refreshLayout RefreshLayout
     * @param oldState 鏀瑰彉涔嬪墠鐨勭姸鎬?     * @param newState 鏀瑰彉涔嬪悗鐨勭姸鎬?     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState);
}

