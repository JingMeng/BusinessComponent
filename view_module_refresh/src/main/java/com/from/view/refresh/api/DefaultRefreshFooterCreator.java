package com.from.view.refresh.api;

import android.content.Context;
import androidx.annotation.NonNull;

/**
 * 榛樿Footer鍒涘缓鍣? * Created by SCWANG on 2018/1/26.
 */
public interface DefaultRefreshFooterCreator {
    @NonNull
    RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout);
}

