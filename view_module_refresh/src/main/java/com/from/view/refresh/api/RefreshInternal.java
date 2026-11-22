package com.from.view.refresh.api;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.view.View;

import com.from.view.refresh.constant.SpinnerStyle;
import com.from.view.refresh.listener.OnStateChangedListener;

import static android.support.annotation.RestrictTo.Scope.LIBRARY;
import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static android.support.annotation.RestrictTo.Scope.SUBCLASSES;

/**
 * 鍒锋柊鍐呴儴缁勪欢
 * Created by SCWANG on 2017/5/26.
 */
public interface RefreshInternal extends OnStateChangedListener {
    /**
     * 鑾峰彇瀹炰綋瑙嗗浘
     * @return 瀹炰綋瑙嗗浘
     */
    @NonNull
    View getView();

    /**
     * 鑾峰彇鍙樻崲鏂瑰紡 {@link SpinnerStyle} 蹇呴』杩斿洖 闈炵┖
     * @return 鍙樻崲鏂瑰紡
     */
    @NonNull
    SpinnerStyle getSpinnerStyle();

    /**
     * 璁剧疆涓婚棰滆壊
     * @param colors 瀵瑰簲Xml涓厤缃殑 srlPrimaryColor srlAccentColor
     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void setPrimaryColors(@ColorInt int... colors);

    /**
     * 灏哄瀹氫箟瀹屾垚 锛堝鏋滈珮搴︿笉鏀瑰彉锛堜唬鐮佷慨鏀癸細setHeader锛夛紝鍙皟鐢ㄤ竴娆? 鍦≧efreshLayout#onMeasure涓皟鐢級
     * @param kernel RefreshKernel
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 鏈€澶ф嫋鍔ㄩ珮搴?     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight);
    /**
     * 鎵嬫寚鎷栧姩涓嬫媺锛堜細杩炵画澶氭璋冪敤锛屾坊鍔爄sDragging骞跺彇浠ｄ箣鍓嶇殑onPulling銆乷nReleasing锛?     * @param isDragging true 鎵嬫寚姝ｅ湪鎷栧姩 false 鍥炲脊鍔ㄧ敾
     * @param percent 涓嬫媺鐨勭櫨鍒嗘瘮 鍊?= offset/footerHeight (0 - percent - (footerHeight+maxDragHeight) / footerHeight )
     * @param offset 涓嬫媺鐨勫儚绱犲亸绉婚噺  0 - offset - (footerHeight+maxDragHeight)
     * @param height 楂樺害 HeaderHeight or FooterHeight
     * @param maxDragHeight 鏈€澶ф嫋鍔ㄩ珮搴?     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight);

    /**
     * 閲婃斁鏃跺埢锛堣皟鐢ㄤ竴娆★紝灏嗕細瑙﹀彂鍔犺浇锛?     * @param refreshLayout RefreshLayout
     * @param height 楂樺害 HeaderHeight or FooterHeight
     * @param maxDragHeight 鏈€澶ф嫋鍔ㄩ珮搴?     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight);

    /**
     * 寮€濮嬪姩鐢?     * @param refreshLayout RefreshLayout
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 鏈€澶ф嫋鍔ㄩ珮搴?     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight);

    /**
     * 鍔ㄧ敾缁撴潫
     * @param refreshLayout RefreshLayout
     * @param success 鏁版嵁鏄惁鎴愬姛鍒锋柊鎴栧姞杞?     * @return 瀹屾垚鍔ㄧ敾鎵€闇€鏃堕棿 濡傛灉杩斿洖 Integer.MAX_VALUE 灏嗗彇娑堟湰娆″畬鎴愪簨浠讹紝缁х画淇濇寔鍘熸湁鐘舵€?     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    int onFinish(@NonNull RefreshLayout refreshLayout, boolean success);

    /**
     * 姘村钩鏂瑰悜鐨勬嫋鍔?     * @param percentX 涓嬫媺鏃讹紝鎵嬫寚姘村钩鍧愭爣瀵瑰睆骞曠殑鍗犳瘮锛? - percentX - 1锛?     * @param offsetX 涓嬫媺鏃讹紝鎵嬫寚姘村钩鍧愭爣瀵瑰睆骞曠殑鍋忕Щ锛? - offsetX - LayoutWidth锛?     * @param offsetMax 鏈€澶х殑鍋忕Щ閲?     */
    @RestrictTo({LIBRARY,LIBRARY_GROUP,SUBCLASSES})
    void onHorizontalDrag(float percentX, int offsetX, int offsetMax);

    /**
     * 鏄惁鏀寔姘村钩鏂瑰悜鐨勬嫋鍔紙灏嗕細褰卞搷鍒皁nHorizontalDrag鐨勮皟鐢級
     * @return 姘村钩鎷栧姩闇€瑕佹秷鑰楁洿澶氱殑鏃堕棿鍜岃祫婧愶紝鎵€浠ュ鏋滀笉鏀寔璇疯繑鍥瀎alse
     */
    boolean isSupportHorizontalDrag();
}

