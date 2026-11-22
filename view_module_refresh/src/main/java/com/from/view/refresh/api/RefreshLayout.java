package com.from.view.refresh.api;

import androidx.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.from.view.refresh.constant.RefreshState;
import com.from.view.refresh.impl.ScrollBoundaryDeciderAdapter;
import com.from.view.refresh.listener.OnLoadMoreListener;
import com.from.view.refresh.listener.OnMultiPurposeListener;
import com.from.view.refresh.listener.OnRefreshListener;
import com.from.view.refresh.listener.OnRefreshLoadMoreListener;
import com.from.view.refresh.listener.SimpleMultiPurposeListener;

/**
 * 鍒锋柊甯冨眬
 * interface of the refresh layout
 * Created by SCWANG on 2017/5/26.
 */
@SuppressWarnings({"UnusedReturnValue", "SameParameterValue", "unused"})
public interface RefreshLayout {

    /**
     * Set the Footer's height.
     * 璁剧疆 Footer 鐨勯珮搴?     * @param dp Density-independent Pixels 铏氭嫙鍍忕礌锛坧x闇€瑕佽皟鐢╬x2dp杞崲锛?     * @return RefreshLayout
     */
    RefreshLayout setFooterHeight(float dp);

//    /**
//     * 璁剧疆 Footer 楂樺害
//     * @param px 鍍忕礌
//     * @return RefreshLayout
//     */
//    RefreshLayout setFooterHeightPx(int px);

    /**
     * Set the Header's height.
     * 璁剧疆 Header 楂樺害
     * @param dp Density-independent Pixels 铏氭嫙鍍忕礌锛坧x闇€瑕佽皟鐢╬x2dp杞崲锛?     * @return RefreshLayout
     */
    RefreshLayout setHeaderHeight(float dp);

//    /**
//     * 璁剧疆 Header 楂樺害
//     * @param px 鍍忕礌
//     * @return RefreshLayout
//     */
//    RefreshLayout setHeaderHeightPx(int px);

    /**
     * Set the Header's start offset锛坰ee srlHeaderInsetStart in the RepastPracticeActivity XML in demo-app for the practical application锛?
     * 璁剧疆 Header 鐨勮捣濮嬪亸绉婚噺锛堜娇鐢ㄦ柟娉曞弬鑰?demo-app 涓殑 RepastPracticeActivity xml 涓殑 srlHeaderInsetStart锛?     * @param dp Density-independent Pixels 铏氭嫙鍍忕礌锛坧x闇€瑕佽皟鐢╬x2dp杞崲锛?     * @return RefreshLayout
     */
    RefreshLayout setHeaderInsetStart(float dp);

//    /**
//     * 璁剧疆 Header 璧峰鍋忕Щ閲?//     * @param insetPx 鍍忕礌
//     * @return RefreshLayout
//     */
//    RefreshLayout setHeaderInsetStartPx(int insetPx);

    /**
     * Set the Header's start offset.
     * 璁剧疆 Footer 璧峰鍋忕Щ閲忥紙鐢ㄦ埛鍜?setHeaderInsetStart 涓€鏍凤級
     * @see RefreshLayout#setHeaderInsetStart(float)
     * @param dp Density-independent Pixels 铏氭嫙鍍忕礌锛坧x闇€瑕佽皟鐢╬x2dp杞崲锛?     * @return RefreshLayout
     */
    RefreshLayout setFooterInsetStart(float dp);

//    /**
//     * 璁剧疆 Footer 璧峰鍋忕Щ閲?//     * @param insetPx 鍍忕礌
//     * @return RefreshLayout
//     */
//    RefreshLayout setFooterInsetStartPx(int insetPx);

    /**
     * Set the damping effect.
     * 鏄剧ず鎷栧姩楂樺害/鐪熷疄鎷栧姩楂樺害 姣旂巼锛堥粯璁?.5锛岄樆灏兼晥鏋滐級
     * @param rate ratio = (The drag height of the view)/(The actual drag height of the finger)
     *             姣旂巼 = 瑙嗗浘鎷栧姩楂樺害 / 鎵嬫寚鎷栧姩楂樺害
     * @return RefreshLayout
     */
    RefreshLayout setDragRate(@FloatRange(from = 0, to = 1) float rate);

    /**
     * Set the ratio of the maximum height to drag header.
     * 璁剧疆涓嬫媺鏈€澶ч珮搴﹀拰Header楂樺害鐨勬瘮鐜囷紙灏嗕細褰卞搷鍙互涓嬫媺鐨勬渶澶ч珮搴︼級
     * @param rate ratio = (the maximum height to drag header)/(the height of header)
     *             姣旂巼 = 涓嬫媺鏈€澶ч珮搴?/ Header鐨勯珮搴?     * @return RefreshLayout
     */
    RefreshLayout setHeaderMaxDragRate(@FloatRange(from = 1, to = 10) float rate);

    /**
     * Set the ratio of the maximum height to drag footer.
     * 璁剧疆涓婃媺鏈€澶ч珮搴﹀拰Footer楂樺害鐨勬瘮鐜囷紙灏嗕細褰卞搷鍙互涓婃媺鐨勬渶澶ч珮搴︼級
     * @param rate ratio = (the maximum height to drag footer)/(the height of footer)
     *             姣旂巼 = 涓嬫媺鏈€澶ч珮搴?/ Footer鐨勯珮搴?     * @return RefreshLayout
     */
    RefreshLayout setFooterMaxDragRate(@FloatRange(from = 1, to = 10) float rate);

    /**
     * Set the ratio at which the refresh is triggered.
     * 璁剧疆 瑙﹀彂鍒锋柊璺濈 涓?HeaderHeight 鐨勬瘮鐜?     * @param rate 瑙﹀彂鍒锋柊璺濈 涓?HeaderHeight 鐨勬瘮鐜?     * @return RefreshLayout
     */
    RefreshLayout setHeaderTriggerRate(@FloatRange(from = 0, to = 1.0) float rate);

    /**
     * Set the ratio at which the load more is triggered.
     * 璁剧疆 瑙﹀彂鍔犺浇璺濈 涓?FooterHeight 鐨勬瘮鐜?     * @param rate 瑙﹀彂鍔犺浇璺濈 涓?FooterHeight 鐨勬瘮鐜?     * @return RefreshLayout
     */
    RefreshLayout setFooterTriggerRate(@FloatRange(from = 0, to = 1.0) float rate);

    /**
     * Set the rebound interpolator.
     * 璁剧疆鍥炲脊鏄剧ず鎻掑€煎櫒 [鏀炬墜鏃跺洖寮瑰姩鐢?缁撴潫鏃舵敹缂╁姩鐢籡
     * @param interpolator 鍔ㄧ敾鎻掑€煎櫒
     * @return RefreshLayout
     */
    RefreshLayout setReboundInterpolator(@NonNull Interpolator interpolator);

    /**
     * Set the duration of the rebound animation.
     * 璁剧疆鍥炲脊鍔ㄧ敾鏃堕暱 [鏀炬墜鏃跺洖寮瑰姩鐢?缁撴潫鏃舵敹缂╁姩鐢籡
     * @param duration 鏃堕暱
     * @return RefreshLayout
     */
    RefreshLayout setReboundDuration(int duration);

    /**
     * Set the footer of RefreshLayout.
     * 璁剧疆鎸囧畾鐨?Footer
     * @param footer RefreshFooter 鍒锋柊灏惧反
     * @return RefreshLayout
     */
    RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer);

    /**
     * Set the footer of RefreshLayout.
     * 璁剧疆鎸囧畾鐨?Footer
     * @param footer RefreshFooter 鍒锋柊灏惧反
     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              瀹藉害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               楂樺害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    RefreshLayout setRefreshFooter(@NonNull RefreshFooter footer, int width, int height);

    /**
     * Set the header of RefreshLayout.
     * 璁剧疆鎸囧畾鐨?Header
     * @param header RefreshHeader 鍒锋柊澶?     * @return RefreshLayout
     */
    RefreshLayout setRefreshHeader(@NonNull RefreshHeader header);

    /**
     * Set the header of RefreshLayout.
     * 璁剧疆鎸囧畾鐨?Header
     * @param header RefreshHeader 鍒锋柊澶?     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              瀹藉害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               楂樺害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    RefreshLayout setRefreshHeader(@NonNull RefreshHeader header, int width, int height);

    /**
     * Set the content of RefreshLayout锛圫uitable for non-XML pages, not suitable for replacing empty layouts锛夈€?     * 璁剧疆鎸囧畾鐨?Content锛堥€傜敤浜庨潪XML椤甸潰锛屼笉閫傚悎鐢ㄦ浛鎹㈢┖甯冨眬锛?     * @param content View 鍐呭瑙嗗浘
     * @return RefreshLayout
     */
    RefreshLayout setRefreshContent(@NonNull View content);

    /**
     * Set the content of RefreshLayout锛圫uitable for non-XML pages, not suitable for replacing empty layouts锛?
     * 璁剧疆鎸囧畾鐨?Content锛堥€傜敤浜庨潪XML椤甸潰锛屼笉閫傚悎鐢ㄦ浛鎹㈢┖甯冨眬锛?     * @param content View 鍐呭瑙嗗浘
     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              瀹藉害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               楂樺害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    RefreshLayout setRefreshContent(@NonNull View content, int width, int height);

    /**
     * Whether to enable pull-down refresh (enabled by default).
     * 鏄惁鍚敤涓嬫媺鍒锋柊锛堥粯璁ゅ惎鐢級
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableRefresh(boolean enabled);

    /**
     * Set whether to enable pull-up loading more (enabled by default).
     * 璁剧疆鏄惁鍚敤涓婃媺鍔犺浇鏇村锛堥粯璁ゅ惎鐢級
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableLoadMore(boolean enabled);

    /**
     * Sets whether to listen for the list to trigger a load event when scrolling to the bottom (default true).
     * 璁剧疆鏄惁鐩戝惉鍒楄〃鍦ㄦ粴鍔ㄥ埌搴曢儴鏃惰Е鍙戝姞杞戒簨浠讹紙榛樿true锛?     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableAutoLoadMore(boolean enabled);

    /**
     * Set whether to pull down the content while pulling down the header.
     * 璁剧疆鏄惁鍚湪涓嬫媺 Header 鐨勫悓鏃朵笅鎷夊唴瀹?     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableHeaderTranslationContent(boolean enabled);

    /**
     * Set whether to pull up the content while pulling up the header.
     * 璁剧疆鏄惁鍚湪涓婃媺 Footer 鐨勫悓鏃朵笂鎷夊唴瀹?     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableFooterTranslationContent(boolean enabled);

    /**
     * Set whether to enable cross-border rebound function.
     * 璁剧疆鏄惁鍚敤瓒婄晫鍥炲脊
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableOverScrollBounce(boolean enabled);

    /**
     * Set whether to enable the pure scroll mode.
     * 璁剧疆鏄惁寮€鍚函婊氬姩妯″紡
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnablePureScrollMode(boolean enabled);

    /**
     * Set whether to scroll the content to display new data after loading more complete.
     * 璁剧疆鏄惁鍦ㄥ姞杞芥洿澶氬畬鎴愪箣鍚庢粴鍔ㄥ唴瀹规樉绀烘柊鏁版嵁
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableScrollContentWhenLoaded(boolean enabled);

    /**
     * Set whether to scroll the content to display new data after the refresh is complete.
     * 鏄惁鍦ㄥ埛鏂板畬鎴愪箣鍚庢粴鍔ㄥ唴瀹规樉绀烘柊鏁版嵁
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableScrollContentWhenRefreshed(boolean enabled);

    /**
     * Set whether to pull up and load more when the content is not full of one page.
     * 璁剧疆鍦ㄥ唴瀹逛笉婊′竴椤电殑鏃跺€欙紝鏄惁鍙互涓婃媺鍔犺浇鏇村
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableLoadMoreWhenContentNotFull(boolean enabled);

    /**
     * Set whether to enable cross-border drag (imitation iphone effect).
     * 璁剧疆鏄惁鍚敤瓒婄晫鎷栧姩锛堜豢鑻规灉鏁堟灉锛?     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableOverScrollDrag(boolean enabled);

    /**
     * Set whether or not Footer follows the content after there is no more data.
     * 璁剧疆鏄惁鍦ㄦ病鏈夋洿澶氭暟鎹箣鍚?Footer 璺熼殢鍐呭
     * @deprecated use {@link RefreshLayout#setEnableFooterFollowWhenNoMoreData(boolean)}
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Deprecated
    RefreshLayout setEnableFooterFollowWhenLoadFinished(boolean enabled);

    /**
     * Set whether or not Footer follows the content after there is no more data.
     * 璁剧疆鏄惁鍦ㄦ病鏈夋洿澶氭暟鎹箣鍚?Footer 璺熼殢鍐呭
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableFooterFollowWhenNoMoreData(boolean enabled);

    /**
     * Set whether to clip header when the Header is in the FixedBehind state.
     * 璁剧疆鏄惁鍦ㄥ綋 Header 澶勪簬 FixedBehind 鐘舵€佺殑鏃跺€欏壀瑁侀伄鎸?Header
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableClipHeaderWhenFixedBehind(boolean enabled);

    /**
     * Set whether to clip footer when the Footer is in the FixedBehind state.
     * 璁剧疆鏄惁鍦ㄥ綋 Footer 澶勪簬 FixedBehind 鐘舵€佺殑鏃跺€欏壀瑁侀伄鎸?Footer
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableClipFooterWhenFixedBehind(boolean enabled);

    /**
     * Setting whether nesting scrolling is enabled (default off + smart on).
     * 璁剧疆鏄細鍚﹀惎鐢ㄥ祵濂楁粴鍔ㄥ姛鑳斤紙榛樿鍏抽棴+鏅鸿兘寮€鍚級
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    RefreshLayout setEnableNestedScroll(boolean enabled);

//    /**
//     * Sets whether to enable pure nested scrolling mode
//     * Smart scrolling supports both [nested scrolling] and [traditional scrolling] modes
//     * With nested scrolling enabled, traditional mode also works when necessary
//     * However, sometimes interference and conflict can occur. If you find this conflict, you can try to turn on [pure nested scrolling] mode and [traditional mode] off
//     * 璁剧疆鏄惁寮€鍚€愮函宓屽婊氬姩銆戞ā寮?//     * Smart 鐨勬粴鍔ㄦ敮鎸?銆愬祵濂楁粴鍔ㄣ€?+ 銆愪紶缁熸粴鍔ㄣ€?涓ょ妯″紡
//     * 鍦ㄥ紑鍚?銆愬祵濂楁粴鍔ㄣ€?鐨勬儏鍐典笅锛屻€愪紶缁熸ā寮忋€戜篃浼氬湪蹇呰鐨勬椂鍊欏彂鎸ヤ綔鐢?//     * 浣嗘槸鏈夋椂鍊欎篃浼氬彂鐢熷共鎵板拰鍐茬獊锛屽鏋滄偍鍙戠幇浜嗚繖涓啿绐侊紝鍙互灏濊瘯寮€鍚?銆愮函宓屽婊氬姩銆戞ā寮忥紝銆愪紶缁熸ā寮忋€戝叧闂?//     * @param enabled 鏄惁鍚敤
//     * @return RefreshLayout
//     */
//    RefreshLayout setEnableNestedScrollOnly(boolean enabled);

    /**
     * Set whether to enable the action content view when refreshing.
     * 璁剧疆鏄惁寮€鍚湪鍒锋柊鏃跺€欑姝㈡搷浣滃唴瀹硅鍥?     * @param disable 鏄惁绂佹
     * @return RefreshLayout
     */
    RefreshLayout setDisableContentWhenRefresh(boolean disable);

    /**
     * Set whether to enable the action content view when loading.
     * 璁剧疆鏄惁寮€鍚湪鍔犺浇鏃跺€欑姝㈡搷浣滃唴瀹硅鍥?     * @param disable 鏄惁绂佹
     * @return RefreshLayout
     */
    RefreshLayout setDisableContentWhenLoading(boolean disable);

    /**
     * Set refresh listener separately.
     * 鍗曠嫭璁剧疆鍒锋柊鐩戝惉鍣?     * @param listener OnRefreshListener 鍒锋柊鐩戝惉鍣?     * @return RefreshLayout
     */
    RefreshLayout setOnRefreshListener(OnRefreshListener listener);

    /**
     * Set load more listener separately.
     * 鍗曠嫭璁剧疆鍔犺浇鐩戝惉鍣?     * @param listener OnLoadMoreListener 鍔犺浇鐩戝惉鍣?     * @return RefreshLayout
     */
    RefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener);

    /**
     * Set refresh and load listeners at the same time.
     * 鍚屾椂璁剧疆鍒锋柊鍜屽姞杞界洃鍚櫒
     * @param listener OnRefreshLoadMoreListener 鍒锋柊鍔犺浇鐩戝惉鍣?     * @return RefreshLayout
     */
    RefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener);

    /**
     * Set up a multi-function listener.
     * Recommended {@link SimpleMultiPurposeListener}
     * 璁剧疆澶氬姛鑳界洃鍚櫒
     * 寤鸿浣跨敤 {@link SimpleMultiPurposeListener}
     * @param listener OnMultiPurposeListener 澶氬姛鑳界洃鍚櫒
     * @return RefreshLayout
     */
    RefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener);

    /**
     * Set the scroll boundary Decider, Can customize when you can refresh.
     * Recommended {@link ScrollBoundaryDeciderAdapter}
     * 璁剧疆婊氬姩杈圭晫鍒ゆ柇鍣?     * 寤鸿浣跨敤 {@link ScrollBoundaryDeciderAdapter}
     * @param boundary ScrollBoundaryDecider 鍒ゆ柇鍣?     * @return RefreshLayout
     */
    RefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider boundary);

    /**
     * Set theme color int (primaryColor and accentColor).
     * 璁剧疆涓婚棰滆壊
     * @param primaryColors ColorInt 涓婚棰滆壊
     * @return RefreshLayout
     */
    RefreshLayout setPrimaryColors(@ColorInt int... primaryColors);

    /**
     * Set theme color id (primaryColor and accentColor).
     * 璁剧疆涓婚棰滆壊
     * @param primaryColorId ColorRes 涓婚棰滆壊ID
     * @return RefreshLayout
     */
    RefreshLayout setPrimaryColorsId(@ColorRes int... primaryColorId);

    /**
     * finish refresh.
     * 瀹屾垚鍒锋柊
     * @return RefreshLayout
     */
    RefreshLayout finishRefresh();

    /**
     * finish refresh.
     * 瀹屾垚鍒锋柊
     * @param delayed 寮€濮嬪欢鏃?     * @return RefreshLayout
     */
    RefreshLayout finishRefresh(int delayed);

    /**
     * finish refresh.
     * 瀹屾垚鍔犺浇
     * @param success 鏁版嵁鏄惁鎴愬姛鍒锋柊 锛堜細褰卞搷鍒颁笂娆℃洿鏂版椂闂寸殑鏀瑰彉锛?     * @return RefreshLayout
     */
    RefreshLayout finishRefresh(boolean success);

    /**
     * finish refresh.
     * 瀹屾垚鍒锋柊
     * @param delayed 寮€濮嬪欢鏃?     * @param success 鏁版嵁鏄惁鎴愬姛鍒锋柊 锛堜細褰卞搷鍒颁笂娆℃洿鏂版椂闂寸殑鏀瑰彉锛?     * @return RefreshLayout
     */
    RefreshLayout finishRefresh(int delayed, boolean success);

    /**
     * finish load more.
     * 瀹屾垚鍔犺浇
     * @return RefreshLayout
     */
    RefreshLayout finishLoadMore();

    /**
     * finish load more.
     * 瀹屾垚鍔犺浇
     * @param delayed 寮€濮嬪欢鏃?     * @return RefreshLayout
     */
    RefreshLayout finishLoadMore(int delayed);

    /**
     * finish load more.
     * 瀹屾垚鍔犺浇
     * @param success 鏁版嵁鏄惁鎴愬姛
     * @return RefreshLayout
     */
    RefreshLayout finishLoadMore(boolean success);

    /**
     * finish load more.
     * 瀹屾垚鍔犺浇
     * @param delayed 寮€濮嬪欢鏃?     * @param success 鏁版嵁鏄惁鎴愬姛
     * @param noMoreData 鏄惁鏈夋洿澶氭暟鎹?     * @return RefreshLayout
     */
    RefreshLayout finishLoadMore(int delayed, boolean success, boolean noMoreData);

    /**
     * finish load more with no more data.
     * 瀹屾垚鍔犺浇骞舵爣璁版病鏈夋洿澶氭暟鎹?     * @return RefreshLayout
     */
    RefreshLayout finishLoadMoreWithNoMoreData();

    /**
     * Close the Header or Footer, can't replace finishRefresh and finishLoadMore.
     * 鍏抽棴 Header 鎴栬€?Footer
     * @return RefreshLayout
     */
    RefreshLayout closeHeaderOrFooter();

    /**
     * Restore the original state after finishLoadMoreWithNoMoreData.
     * 鎭㈠娌℃湁鏇村鏁版嵁鐨勫師濮嬬姸鎬?     * @deprecated use {@link RefreshLayout#resetNoMoreData()}
     * @param noMoreData 鏄惁鏈夋洿澶氭暟鎹?     * @return RefreshLayout
     */
    @Deprecated
    RefreshLayout setNoMoreData(boolean noMoreData);

    /**
     * Restore the original state after finishLoadMoreWithNoMoreData.
     * 鎭㈠娌℃湁鏇村鏁版嵁鐨勫師濮嬬姸鎬?     * @return RefreshLayout
     */
    RefreshLayout resetNoMoreData();

    /**
     * Get header of RefreshLayout
     * 鑾峰彇褰撳墠 Header
     * @return RefreshLayout
     */
    @Nullable
    RefreshHeader getRefreshHeader();

    /**
     * Get footer of RefreshLayout
     * 鑾峰彇褰撳墠 Footer
     * @return RefreshLayout
     */
    @Nullable
    RefreshFooter getRefreshFooter();

    /**
     * Get the current state of RefreshLayout
     * 鑾峰彇褰撳墠鐘舵€?     * @return RefreshLayout
     */
    RefreshState getState();

    /**
     * Get the ViewGroup of RefreshLayout
     * 鑾峰彇瀹炰綋甯冨眬瑙嗗浘
     * @return ViewGroup
     */
    ViewGroup getLayout();

    /**
     * Display refresh animation and trigger refresh event.
     * 鏄剧ず鍒锋柊鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    boolean autoRefresh();

    /**
     * Display refresh animation and trigger refresh event, Delayed start.
     * 鏄剧ず鍒锋柊鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢锛屽欢鏃跺惎鍔?     * @param delayed 寮€濮嬪欢鏃?     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Deprecated
    boolean autoRefresh(int delayed);

    /**
     * Display refresh animation without triggering events.
     * 鏄剧ず鍒锋柊鍔ㄧ敾锛屼笉瑙﹀彂浜嬩欢
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    boolean autoRefreshAnimationOnly();

    /**
     * Display refresh animation, Multifunction.
     * 鏄剧ず鍒锋柊鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢
     * @param delayed 寮€濮嬪欢鏃?     * @param duration 鎷栨嫿鍔ㄧ敾鎸佺画鏃堕棿
     * @param dragRate 鎷夋嫿鐨勯珮搴︽瘮鐜囷紙瑕佹眰 鈮?1 锛?     * @param animationOnly animation only 鍙湁鍔ㄧ敾
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    boolean autoRefresh(int delayed, int duration, float dragRate, boolean animationOnly);

    /**
     * Display load more animation and trigger load more event.
     * 鏄剧ず鍔犺浇鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    boolean autoLoadMore();

    /**
     * Display load more animation and trigger load more event, Delayed start.
     * 鏄剧ず鍔犺浇鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢, 寤舵椂鍚姩
     * @param delayed 寮€濮嬪欢鏃?     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Deprecated
    boolean autoLoadMore(int delayed);

    /**
     * Display load more animation without triggering events.
     * 鏄剧ず鍔犺浇鍔ㄧ敾锛屼笉瑙﹀彂浜嬩欢
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    boolean autoLoadMoreAnimationOnly();

    /**
     * Display load more animation and trigger load more event, Delayed start.
     * 鏄剧ず鍔犺浇鍔ㄧ敾, 澶氬姛鑳介€夐」
     * @param delayed 寮€濮嬪欢鏃?     * @param duration 鎷栨嫿鍔ㄧ敾鎸佺画鏃堕棿
     * @param dragRate 鎷夋嫿鐨勯珮搴︽瘮鐜囷紙瑕佹眰 鈮?1 锛?     * @param animationOnly 鏄惁鍙槸鏄剧ず鍔ㄧ敾锛屼笉鍥炶皟
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    boolean autoLoadMore(int delayed, int duration, float dragRate, boolean animationOnly);


//    /**
//     * 鏄惁姝ｅ湪鍒锋柊
//     * @deprecated 鍚庣画鐗堟湰灏嗕細绉婚櫎
//     *      浣跨敤 {@link #getState()} == {@link RefreshState#Refreshing} 浠ｆ浛
//     * @return RefreshLayout
//     */
//    @Deprecated
//    boolean isRefreshing();
//
//    /**
//     * 鏄惁姝ｅ湪鍔犺浇
//     * @deprecated 鍚庣画鐗堟湰灏嗕細绉婚櫎
//     *      浣跨敤 {@link #getState()} == {@link RefreshState#Loading} 浠ｆ浛
//     * @return RefreshLayout
//     */
//    @Deprecated
//    boolean isLoading();
//
//    /**
//     * 鎭㈠娌℃湁鏇村鏁版嵁鐨勫師濮嬬姸鎬?//     * @deprecated 璇蜂娇鐢▄@link RefreshLayout#setNoMoreData(boolean)}
//     * @return RefreshLayout
//     */
//    @Deprecated
//    RefreshLayout resetNoMoreData();

}

