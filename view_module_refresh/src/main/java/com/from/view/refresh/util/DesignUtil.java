package com.from.view.refresh.util;

import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.View;
import android.view.ViewGroup;

import com.from.view.refresh.api.RefreshKernel;
import com.from.view.refresh.listener.CoordinatorLayoutListener;

/**
 * Design 鍏煎鍖呯己鐪佸皾璇? * Created by SCWANG on 2018/1/29.
 */

public class DesignUtil {

    public static void checkCoordinatorLayout(View content, RefreshKernel kernel, CoordinatorLayoutListener listener) {
        try {//try 涓嶈兘鍒犻櫎锛屼笉鐒朵細鍑虹幇鍏煎鎬ч棶棰?            if (content instanceof CoordinatorLayout) {
                kernel.getRefreshLayout().setEnableNestedScroll(false);
                wrapperCoordinatorLayout(((ViewGroup) content)/*, kernel.getRefreshLayout()*/,listener);
            }
        } catch (Throwable ignored) {
        }
    }

    private static void wrapperCoordinatorLayout(ViewGroup layout/*, final RefreshLayout refreshLayout*/, final CoordinatorLayoutListener listener) {
        for (int i = layout.getChildCount() - 1; i >= 0; i--) {
            View view = layout.getChildAt(i);
            if (view instanceof AppBarLayout) {
                ((AppBarLayout) view).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                    @Override
                    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                        listener.onCoordinatorUpdate(
                                verticalOffset >= 0,
                                /*refreshLayout.isEnableLoadMore() && */
                                (appBarLayout.getTotalScrollRange() + verticalOffset) <= 0);
                    }
                });
            }
        }
    }

}

