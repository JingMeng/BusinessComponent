package com.from.view.refresh.api;

import android.animation.ValueAnimator;
import androidx.annotation.NonNull;

import com.from.view.refresh.constant.RefreshState;

/**
 * 鍒锋柊甯冨眬鏍稿績鍔熻兘鎺ュ彛
 * 涓哄姛鑳藉鏉傜殑 Header 鎴栬€?Footer 寮€鏀剧殑鎺ュ彛
 * Created by SCWANG on 2017/5/26.
 */
@SuppressWarnings({"unused", "UnusedReturnValue", "SameParameterValue"})
public interface RefreshKernel {

    @NonNull
    RefreshLayout getRefreshLayout();
    @NonNull
    RefreshContent getRefreshContent();

    RefreshKernel setState(@NonNull RefreshState state);

    //<editor-fold desc="瑙嗗浘浣嶇Щ Spinner">

    /**
     * 寮€濮嬫墽琛屼簩鏋佸埛鏂?     * @param open 鏄惁灞曞紑
     * @return RefreshKernel
     */
    RefreshKernel startTwoLevel(boolean open);

    /**
     * 缁撴潫鍏抽棴浜屾瀬鍒锋柊
     * @return RefreshKernel
     */
    RefreshKernel finishTwoLevel();

    /**
     * 绉诲姩瑙嗗浘鍒版寚瀹氫綅缃?     * moveSpinner 鐨勫彇鍚嶆潵鑷?璋锋瓕瀹樻柟鐨?{@link androidx.swiperefreshlayout.widget.SwipeRefreshLayout}
     * @param spinner 浣嶇疆 (px)
     * @param isDragging true 鎵嬫寚姝ｅ湪鎷栧姩 false 鍥炲脊鍔ㄧ敾鎵ц
     * @return RefreshKernel
     */
    RefreshKernel moveSpinner(int spinner, boolean isDragging);

    /**
     * 鎵ц鍔ㄧ敾浣胯鍥句綅绉诲埌鎸囧畾鐨?浣嶇疆
     * moveSpinner 鐨勫彇鍚嶆潵鑷?璋锋瓕瀹樻柟鐨?{@link androidx.swiperefreshlayout.widget.SwipeRefreshLayout}
     * @param endSpinner 鎸囧畾鐨勭粨鏉熶綅缃?(px)
     * @return ValueAnimator 濡傛灉娌℃湁鎵ц鍔ㄧ敾 null
     */
    ValueAnimator animSpinner(int endSpinner);

    //</editor-fold>

    //<editor-fold desc="璇锋眰浜嬩欢">

    /**
     * 鎸囧畾鍦ㄤ笅鎷夋椂鍊欎负 Header 鎴?Footer 缁樺埗鑳屾櫙
     * @param internal Header Footer 璋冪敤鏃朵紶 this
     * @param backgroundColor 鑳屾櫙棰滆壊
     * @return RefreshKernel
     */
    RefreshKernel requestDrawBackgroundFor(@NonNull RefreshInternal internal, int backgroundColor);
    /**
     * 璇锋眰浜嬩欢
     * @param internal Header Footer 璋冪敤鏃朵紶 this
     * @param request 璇锋眰
     * @return RefreshKernel
     */
    RefreshKernel requestNeedTouchEventFor(@NonNull RefreshInternal internal, boolean request);
    /**
     * 璇锋眰璁剧疆榛樿鍐呭婊氬姩璁剧疆
     * @param internal Header Footer 璋冪敤鏃朵紶 this
     * @param translation 绉诲姩
     * @return RefreshKernel
     */
    RefreshKernel requestDefaultTranslationContentFor(@NonNull RefreshInternal internal, boolean translation);
    /**
     * 璇锋眰閲嶆柊娴嬮噺 headerHeight 鎴?footerHeight , 瑕佹眰 height 楂樺害涓?WRAP_CONTENT
     * @param internal Header Footer 璋冪敤鏃朵紶 this
     * @return RefreshKernel
     */
    RefreshKernel requestRemeasureHeightFor(@NonNull RefreshInternal internal);
    /**
     * 璁剧疆浜屾ゼ鍥炲脊鏃堕暱
     * @param duration 浜屾ゼ鍥炲脊鏃堕暱
     * @return RefreshKernel
     */
    RefreshKernel requestFloorDuration(int duration);
    //</editor-fold>
}

