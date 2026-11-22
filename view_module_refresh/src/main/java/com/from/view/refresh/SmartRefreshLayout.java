package com.from.view.refresh;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.ColorInt;
import android.support.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;
import android.widget.TextView;

import com.from.view.refresh.api.DefaultRefreshFooterCreator;
import com.from.view.refresh.api.DefaultRefreshHeaderCreator;
import com.from.view.refresh.api.DefaultRefreshInitializer;
import com.from.view.refresh.api.RefreshContent;
import com.from.view.refresh.api.RefreshFooter;
import com.from.view.refresh.api.RefreshHeader;
import com.from.view.refresh.api.RefreshInternal;
import com.from.view.refresh.api.RefreshKernel;
import com.from.view.refresh.api.RefreshLayout;
import com.from.view.refresh.api.ScrollBoundaryDecider;
import com.from.view.refresh.constant.DimensionStatus;
import com.from.view.refresh.constant.RefreshState;
import com.from.view.refresh.constant.SpinnerStyle;
import com.from.view.refresh.footer.ClassicsFooter;
import com.from.view.refresh.header.ClassicsHeader;
import com.from.view.refresh.impl.RefreshContentWrapper;
import com.from.view.refresh.impl.RefreshFooterWrapper;
import com.from.view.refresh.impl.RefreshHeaderWrapper;
import com.from.view.refresh.listener.OnLoadMoreListener;
import com.from.view.refresh.listener.OnMultiPurposeListener;
import com.from.view.refresh.listener.OnRefreshListener;
import com.from.view.refresh.listener.OnRefreshLoadMoreListener;
import com.from.view.refresh.listener.OnStateChangedListener;
import com.from.view.refresh.util.DelayedRunnable;
import com.from.view.refresh.util.DensityUtil;
import com.from.view.refresh.util.SmartUtil;
import com.from.view.refresh.util.ViscousFluidInterpolator;

import java.util.ArrayList;
import java.util.List;

import static android.view.MotionEvent.obtain;
import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.getSize;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.from.view.refresh.util.DensityUtil.dp2px;
import static com.from.view.refresh.util.SmartUtil.getColor;
import static com.from.view.refresh.util.SmartUtil.isContentView;
import static java.lang.System.currentTimeMillis;

/**
 * 鏅鸿兘鍒锋柊甯冨眬
 * Intelligent RefreshLayout
 * Created by SCWANG on 2017/5/26.
 */
@SuppressLint("RestrictedApi")
@SuppressWarnings({"unused", "WeakerAccess"})
public class SmartRefreshLayout extends ViewGroup implements RefreshLayout, NestedScrollingParent/*, NestedScrollingChild*/ {

    //<editor-fold desc="灞炴€у彉閲?property and variable">

    //<editor-fold desc="婊戝姩灞炴€?>
    protected int mTouchSlop;
    protected int mSpinner;//褰撳墠鐨?Spinner
    protected int mLastSpinner;//鏈€鍚庣殑锛岀殑Spinner
    protected int mTouchSpinner;//瑙︽懜鏃跺€欙紝鐨凷pinner
    protected int mFloorDuration = 250;//浜屾ゼ灞曞紑鏃堕暱
    protected int mReboundDuration = 250;//鍥炲脊鍔ㄧ敾鏃堕暱
    protected int mScreenHeightPixels;//灞忓箷楂樺害
    protected float mTouchX;
    protected float mTouchY;
    protected float mLastTouchX;//鐢ㄤ簬瀹炵幇Header鐨勫乏鍙虫嫋鍔ㄦ晥鏋?    protected float mLastTouchY;//鐢ㄤ簬瀹炵幇澶氱偣瑙︽懜
    protected float mDragRate = .5f;
    protected char mDragDirection = 'n';//鎷栧姩鐨勬柟鍚?none-n horizontal-h vertical-v
    protected boolean mIsBeingDragged;//鏄惁姝ｅ湪鎷栧姩
    protected boolean mSuperDispatchTouchEvent;//鐖剁被鏄惁澶勭悊瑙︽懜浜嬩欢
    protected int mFixedHeaderViewId = View.NO_ID;//鍥哄畾鍦ㄥご閮ㄧ殑瑙嗗浘Id
    protected int mFixedFooterViewId = View.NO_ID;//鍥哄畾鍦ㄥ簳閮ㄧ殑瑙嗗浘Id
    protected int mHeaderTranslationViewId = View.NO_ID;//涓嬫媺Header鍋忕Щ鐨勮鍥綢d
    protected int mFooterTranslationViewId = View.NO_ID;//涓嬫媺Footer鍋忕Щ鐨勮鍥綢d

    protected int mMinimumVelocity;
    protected int mMaximumVelocity;
    protected int mCurrentVelocity;
    protected Scroller mScroller;
    protected VelocityTracker mVelocityTracker;
    protected Interpolator mReboundInterpolator;

    //</editor-fold>

    //<editor-fold desc="鍔熻兘灞炴€?>
    protected int[] mPrimaryColors;
    protected boolean mEnableRefresh = true;
    protected boolean mEnableLoadMore = false;
    protected boolean mEnableClipHeaderWhenFixedBehind = true;//褰?Header FixedBehind 鏃跺€欐槸鍚﹀壀瑁侀伄鎸?Header
    protected boolean mEnableClipFooterWhenFixedBehind = true;//褰?Footer FixedBehind 鏃跺€欐槸鍚﹀壀瑁侀伄鎸?Footer
    protected boolean mEnableHeaderTranslationContent = true;//鏄惁鍚敤鍐呭瑙嗗浘鎷栧姩鏁堟灉
    protected boolean mEnableFooterTranslationContent = true;//鏄惁鍚敤鍐呭瑙嗗浘鎷栧姩鏁堟灉
    protected boolean mEnableFooterFollowWhenNoMoreData = false;//鏄惁鍦ㄥ叏閮ㄥ姞杞界粨鏉熶箣鍚嶧ooter璺熼殢鍐呭 1.0.4-6
    protected boolean mEnablePreviewInEditMode = true;//鏄惁鍦ㄧ紪杈戞ā寮忎笅寮€鍚瑙堝姛鑳?    protected boolean mEnableOverScrollBounce = true;//鏄惁鍚敤瓒婄晫鍥炲脊
    protected boolean mEnableOverScrollDrag = false;//鏄惁鍚敤瓒婄晫鎷栧姩锛堜豢鑻规灉鏁堟灉锛?.0.4-6
    protected boolean mEnableAutoLoadMore = true;//鏄惁鍦ㄥ垪琛ㄦ粴鍔ㄥ埌搴曢儴鏃惰嚜鍔ㄥ姞杞芥洿澶?    protected boolean mEnablePureScrollMode = false;//鏄惁寮€鍚函婊氬姩妯″紡
    protected boolean mEnableScrollContentWhenLoaded = true;//鏄惁鍦ㄥ姞杞芥洿澶氬畬鎴愪箣鍚庢粴鍔ㄥ唴瀹规樉绀烘柊鏁版嵁
    protected boolean mEnableScrollContentWhenRefreshed = true;//鏄惁鍦ㄥ埛鏂板畬鎴愪箣鍚庢粴鍔ㄥ唴瀹规樉绀烘柊鏁版嵁
    protected boolean mEnableLoadMoreWhenContentNotFull = true;//鍦ㄥ唴瀹逛笉婊′竴椤电殑鏃跺€欙紝鏄惁鍙互涓婃媺鍔犺浇鏇村
//    protected boolean mEnableNestedScrollingOnly = false;//鏄惁鍙惎鐢ㄥ祵濂楁粴鍔ㄦā寮忥紝鍏抽棴浼犲姛婊氬姩妯″紡锛堝惎鐢ㄤ箣鍚庯紝宓屽婊氬姩涔熻嚜鍔ㄨ寮€鍚紝榛樿濡傛灉寮€鍚祵濂楁粴鍔ㄧ殑璇濓紝浼犲姛婊氬姩妯″紡蹇呰鏃朵篃杈呭姪婊氬姩锛?    protected boolean mDisableContentWhenRefresh = false;//鏄惁寮€鍚湪鍒锋柊鏃跺€欑姝㈡搷浣滃唴瀹硅鍥?    protected boolean mDisableContentWhenLoading = false;//鏄惁寮€鍚湪鍒锋柊鏃跺€欑姝㈡搷浣滃唴瀹硅鍥?    protected boolean mFooterNoMoreData = false;//鏁版嵁鏄惁鍏ㄩ儴鍔犺浇瀹屾垚锛屽鏋滃畬鎴愬氨涓嶈兘鍦ㄨЕ鍙戝姞杞戒簨浠?
    protected boolean mManualLoadMore = false;//鏄惁鎵嬪姩璁剧疆杩嘗oadMore锛岀敤浜庢櫤鑳藉紑鍚?//    protected boolean mManualNestedScrolling = false;//鏄惁鎵嬪姩璁剧疆杩?NestedScrolling锛岀敤浜庢櫤鑳藉紑鍚?    protected boolean mManualHeaderTranslationContent = false;//鏄惁鎵嬪姩璁剧疆杩囧唴瀹硅鍥炬嫋鍔ㄦ晥鏋?    protected boolean mManualFooterTranslationContent = false;//鏄惁鎵嬪姩璁剧疆杩囧唴瀹硅鍥炬嫋鍔ㄦ晥鏋?    //</editor-fold>

    //<editor-fold desc="鐩戝惉灞炴€?>
    protected OnRefreshListener mRefreshListener;
    protected OnLoadMoreListener mLoadMoreListener;
    protected OnMultiPurposeListener mOnMultiPurposeListener;
    protected ScrollBoundaryDecider mScrollBoundaryDecider;
    //</editor-fold>

    //<editor-fold desc="宓屽婊氬姩">
    protected int mTotalUnconsumed;
    protected boolean mNestedInProgress;
    protected int[] mParentOffsetInWindow = new int[2];
    protected NestedScrollingChildHelper mNestedChild = new NestedScrollingChildHelper(this);
    protected NestedScrollingParentHelper mNestedParent = new NestedScrollingParentHelper(this);
    //</editor-fold>

    //<editor-fold desc="鍐呴儴瑙嗗浘">

    protected int mHeaderHeight;        //澶撮儴楂樺害 鍜?澶撮儴楂樺害鐘舵€?    protected DimensionStatus mHeaderHeightStatus = DimensionStatus.DefaultUnNotify;
    protected int mFooterHeight;        //搴曢儴楂樺害 鍜?搴曢儴楂樺害鐘舵€?    protected DimensionStatus mFooterHeightStatus = DimensionStatus.DefaultUnNotify;

    protected int mHeaderInsetStart;    // Header 璧峰浣嶇疆鍋忕Щ
    protected int mFooterInsetStart;    // Footer 璧峰浣嶇疆鍋忕Щ

    protected float mHeaderMaxDragRate = 2.5f;  //鏈€澶ф嫋鍔ㄦ瘮鐜?鏈€澶ч珮搴?Header楂樺害)
    protected float mFooterMaxDragRate = 2.5f;  //鏈€澶ф嫋鍔ㄦ瘮鐜?鏈€澶ч珮搴?Footer楂樺害)
    protected float mHeaderTriggerRate = 1.0f;  //瑙﹀彂鍒锋柊璺濈 涓?HeaderHeight 鐨勬瘮鐜?    protected float mFooterTriggerRate = 1.0f;  //瑙﹀彂鍔犺浇璺濈 涓?FooterHeight 鐨勬瘮鐜?
    protected RefreshInternal mRefreshHeader;     //涓嬫媺澶撮儴瑙嗗浘
    protected RefreshInternal mRefreshFooter;     //涓婃媺搴曢儴瑙嗗浘
    protected RefreshContent mRefreshContent;   //鏄剧ず鍐呭瑙嗗浘
    //</editor-fold>

    protected Paint mPaint;
    protected Handler mHandler;
    protected RefreshKernel mKernel = new RefreshKernelImpl();
    protected List<DelayedRunnable> mListDelayedRunnable;

    protected RefreshState mState = RefreshState.None;          //涓荤姸鎬?    protected RefreshState mViceState = RefreshState.None;      //鍓姸鎬侊紙涓荤姸鎬佸埛鏂版椂鍊欑殑婊氬姩鐘舵€侊級

    protected long mLastOpenTime = 0;                           //涓婁竴娆?鍒锋柊鎴栬€呭姞杞?鏃堕棿

    protected int mHeaderBackgroundColor = 0;                   //涓篐eader缁樺埗绾壊鑳屾櫙
    protected int mFooterBackgroundColor = 0;

    protected boolean mHeaderNeedTouchEventWhenRefreshing;      //涓烘父鎴廐eader鎻愪緵鐙珛浜嬩欢
    protected boolean mFooterNeedTouchEventWhenLoading;

    protected boolean mFooterLocked = false;//Footer 姝ｅ湪loading 鐨勬椂鍊欐槸鍚﹂攣浣?鍒楄〃涓嶈兘鍚戜笂婊氬姩


    protected static DefaultRefreshFooterCreator sFooterCreator = null;
    protected static DefaultRefreshHeaderCreator sHeaderCreator = null;
    protected static DefaultRefreshInitializer sRefreshInitializer = null;

    //</editor-fold>

    //<editor-fold desc="鏋勯€犳柟娉?construction methods">
    public SmartRefreshLayout(Context context) {
        this(context, null);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmartRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        super.setClipToPadding(false);

        DensityUtil density = new DensityUtil();
        ViewConfiguration configuration = ViewConfiguration.get(context);

        mScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();
        mScreenHeightPixels = context.getResources().getDisplayMetrics().heightPixels;
        mReboundInterpolator = new ViscousFluidInterpolator();
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

        mFooterHeight = density.dip2px(60);
        mHeaderHeight = density.dip2px(100);

        mNestedChild.setNestedScrollingEnabled(true);//榛樿寮€鍚祵濂楁粴鍔?
        if (sRefreshInitializer != null) {
            sRefreshInitializer.initialize(context, this);//璋冪敤鍏ㄥ眬鍒濆鍖?        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout);

        mNestedChild.setNestedScrollingEnabled(ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableNestedScrolling, mNestedChild.isNestedScrollingEnabled()));
        mDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlDragRate, mDragRate);
        mHeaderMaxDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderMaxDragRate, mHeaderMaxDragRate);
        mFooterMaxDragRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterMaxDragRate, mFooterMaxDragRate);
        mHeaderTriggerRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlHeaderTriggerRate, mHeaderTriggerRate);
        mFooterTriggerRate = ta.getFloat(R.styleable.SmartRefreshLayout_srlFooterTriggerRate, mFooterTriggerRate);
        mEnableRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableRefresh, mEnableRefresh);
        mReboundDuration = ta.getInt(R.styleable.SmartRefreshLayout_srlReboundDuration, mReboundDuration);
        mEnableLoadMore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMore, mEnableLoadMore);
        mHeaderHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderHeight, mHeaderHeight);
        mFooterHeight = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterHeight, mFooterHeight);
        mHeaderInsetStart = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlHeaderInsetStart, mHeaderInsetStart);
        mFooterInsetStart = ta.getDimensionPixelOffset(R.styleable.SmartRefreshLayout_srlFooterInsetStart, mFooterInsetStart);
        mDisableContentWhenRefresh = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenRefresh, mDisableContentWhenRefresh);
        mDisableContentWhenLoading = ta.getBoolean(R.styleable.SmartRefreshLayout_srlDisableContentWhenLoading, mDisableContentWhenLoading);
        mEnableHeaderTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent, mEnableHeaderTranslationContent);
        mEnableFooterTranslationContent = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent, mEnableFooterTranslationContent);
        mEnablePreviewInEditMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePreviewInEditMode, mEnablePreviewInEditMode);
        mEnableAutoLoadMore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableAutoLoadMore, mEnableAutoLoadMore);
        mEnableOverScrollBounce = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollBounce, mEnableOverScrollBounce);
        mEnablePureScrollMode = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnablePureScrollMode, mEnablePureScrollMode);
        mEnableScrollContentWhenLoaded = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenLoaded, mEnableScrollContentWhenLoaded);
        mEnableScrollContentWhenRefreshed = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableScrollContentWhenRefreshed, mEnableScrollContentWhenRefreshed);
        mEnableLoadMoreWhenContentNotFull = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadMoreWhenContentNotFull, mEnableLoadMoreWhenContentNotFull);
        mEnableFooterFollowWhenNoMoreData = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenLoadFinished, mEnableFooterFollowWhenNoMoreData);
        mEnableFooterFollowWhenNoMoreData = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableFooterFollowWhenNoMoreData, mEnableFooterFollowWhenNoMoreData);
        mEnableClipHeaderWhenFixedBehind = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipHeaderWhenFixedBehind, mEnableClipHeaderWhenFixedBehind);
        mEnableClipFooterWhenFixedBehind = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableClipFooterWhenFixedBehind, mEnableClipFooterWhenFixedBehind);
        mEnableOverScrollDrag = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableOverScrollDrag, mEnableOverScrollDrag);
//        mEnableNestedScrollingOnly = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableNestedScrollingOnly, mEnableNestedScrollingOnly);
        mFixedHeaderViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedHeaderViewId, mFixedHeaderViewId);
        mFixedFooterViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFixedFooterViewId, mFixedFooterViewId);
        mHeaderTranslationViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlHeaderTranslationViewId, mHeaderTranslationViewId);
        mFooterTranslationViewId = ta.getResourceId(R.styleable.SmartRefreshLayout_srlFooterTranslationViewId, mFooterTranslationViewId);

        if (mEnablePureScrollMode && !ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableOverScrollDrag)) {
            mEnableOverScrollDrag = true;
        }

        mManualLoadMore = mManualLoadMore || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableLoadMore);
        mManualHeaderTranslationContent = mManualHeaderTranslationContent || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableHeaderTranslationContent);
        mManualFooterTranslationContent = mManualFooterTranslationContent || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableFooterTranslationContent);
//        mManualNestedScrolling = mManualNestedScrolling || ta.hasValue(R.styleable.SmartRefreshLayout_srlEnableNestedScrolling);
        mHeaderHeightStatus = ta.hasValue(R.styleable.SmartRefreshLayout_srlHeaderHeight) ? DimensionStatus.XmlLayoutUnNotify : mHeaderHeightStatus;
        mFooterHeightStatus = ta.hasValue(R.styleable.SmartRefreshLayout_srlFooterHeight) ? DimensionStatus.XmlLayoutUnNotify : mFooterHeightStatus;

        int accentColor = ta.getColor(R.styleable.SmartRefreshLayout_srlAccentColor, 0);
        int primaryColor = ta.getColor(R.styleable.SmartRefreshLayout_srlPrimaryColor, 0);
        if (primaryColor != 0) {
            if (accentColor != 0) {
                mPrimaryColors = new int[]{primaryColor, accentColor};
            } else {
                mPrimaryColors = new int[]{primaryColor};
            }
        } else if (accentColor != 0) {
            mPrimaryColors = new int[]{0, accentColor};
        }

        ta.recycle();

    }
    //</editor-fold>

    //<editor-fold desc="鐢熷懡鍛ㄦ湡 life cycle">

    /**
     * 閲嶅啓 onFinishInflate 鏉ュ畬鎴?smart 鐨勭壒瀹氬姛鑳?     * 1.鏅鸿兘瀵绘壘 Xml 涓畾涔夌殑 Content銆丠eader銆丗ooter
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int count = super.getChildCount();
        if (count > 3) {
            throw new RuntimeException("鏈€澶氬彧鏀寔3涓瓙View锛孧ost only support three sub view");
        }

        int contentLevel = 0;
        int indexContent = -1;
        for (int i = 0; i < count; i++) {
            View view = super.getChildAt(i);
            if (isContentView(view) && (contentLevel < 2 || i == 1)) {
                indexContent = i;
                contentLevel = 2;
            } else if (!(view instanceof RefreshInternal) && contentLevel < 1) {
                indexContent = i;
                contentLevel = i > 0 ? 1 : 0;
            }
        }

        int indexHeader = -1;
        int indexFooter = -1;
        if (indexContent >= 0) {
            mRefreshContent = new RefreshContentWrapper(super.getChildAt(indexContent));
            if (indexContent == 1) {
                indexHeader = 0;
                if (count == 3) {
                    indexFooter = 2;
                }
            } else if (count == 2) {
                indexFooter = 1;
            }
        }

        for (int i = 0; i < count; i++) {
            View view = super.getChildAt(i);
            if (i == indexHeader || (i != indexFooter && indexHeader == -1 && mRefreshHeader == null && view instanceof RefreshHeader)) {
                mRefreshHeader = (view instanceof RefreshHeader) ? (RefreshHeader) view : new RefreshHeaderWrapper(view);
            } else if (i == indexFooter || (indexFooter == -1 && view instanceof RefreshFooter)) {
                mEnableLoadMore = (mEnableLoadMore || !mManualLoadMore);
                mRefreshFooter = (view instanceof RefreshFooter) ? (RefreshFooter) view : new RefreshFooterWrapper(view);
//            } else if (mRefreshContent == null) {
//                mRefreshContent = new RefreshContentWrapper(view);
            }
        }

    }

    /**
     * 閲嶅啓 onAttachedToWindow 鏉ュ畬鎴?smart 鐨勭壒瀹氬姛鑳?     * 1.娣诲姞榛樿鎴栬€呭叏灞€璁剧疆鐨?Header 鍜?Footer 锛堢己鐪佹儏鍐典笅鎵嶄細锛?     * 2.鍋?Content 涓虹┖鏃剁殑 TextView 鎻愮ず
     * 3.鏅鸿兘寮€鍚?宓屽婊氬姩 NestedScrollingEnabled
     * 4.鍒濆鍖?涓婚棰滆壊 鍜?璋冩暣 Header Footer Content 鐨勬樉绀洪『搴?     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final View thisView = this;
        if (!thisView.isInEditMode()) {

            if (mHandler == null) {
                mHandler = new Handler();
            }

            if (mListDelayedRunnable != null) {
                for (DelayedRunnable runnable : mListDelayedRunnable) {
                    mHandler.postDelayed(runnable, runnable.delayMillis);
                }
                mListDelayedRunnable.clear();
                mListDelayedRunnable = null;
            }

            if (mRefreshHeader == null) {
                if (sHeaderCreator != null) {
                    setRefreshHeader(sHeaderCreator.createRefreshHeader(thisView.getContext(), this));
                } else {
                    setRefreshHeader(new ClassicsHeader(thisView.getContext()));
                }
            }
            if (mRefreshFooter == null) {
                if (sFooterCreator != null) {
                    setRefreshFooter(sFooterCreator.createRefreshFooter(thisView.getContext(), this));
                } else {
                    boolean old = mEnableLoadMore;
                    setRefreshFooter(new ClassicsFooter(thisView.getContext()));
                    mEnableLoadMore = old;
                }
            } else {
                mEnableLoadMore = mEnableLoadMore || !mManualLoadMore;
            }

            if (mRefreshContent == null) {
                for (int i = 0, len = getChildCount(); i < len; i++) {
                    View view = getChildAt(i);
                    if ((mRefreshHeader == null || view != mRefreshHeader.getView())&&
                            (mRefreshFooter == null || view != mRefreshFooter.getView())) {
                        mRefreshContent = new RefreshContentWrapper(view);
                    }
                }
            }
            if (mRefreshContent == null) {
                final int padding = dp2px(20);
                final TextView errorView = new TextView(thisView.getContext());
                errorView.setTextColor(0xffff6600);
                errorView.setGravity(Gravity.CENTER);
                errorView.setTextSize(20);
                errorView.setText(R.string.srl_content_empty);
                super.addView(errorView, MATCH_PARENT, MATCH_PARENT);
                mRefreshContent = new RefreshContentWrapper(errorView);
                mRefreshContent.getView().setPadding(padding, padding, padding, padding);
            }

            View fixedHeaderView = mFixedHeaderViewId > 0 ? thisView.findViewById(mFixedHeaderViewId) : null;
            View fixedFooterView = mFixedFooterViewId > 0 ? thisView.findViewById(mFixedFooterViewId) : null;

            mRefreshContent.setScrollBoundaryDecider(mScrollBoundaryDecider);
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull);
            mRefreshContent.setUpComponent(mKernel, fixedHeaderView, fixedFooterView);

            if (mSpinner != 0) {
                notifyStateChanged(RefreshState.None);
                mRefreshContent.moveSpinner(mSpinner = 0, mHeaderTranslationViewId, mFooterTranslationViewId);
            }

//            if (!mManualNestedScrolling && !isNestedScrollingEnabled()) {
//                post(new Runnable() {
//                    @Override
//                    public void run() {
//                        final View thisView = SmartRefreshLayout.this;
//                        for (ViewParent parent = thisView.getParent() ; parent != null ; ) {
//                            if (parent instanceof NestedScrollingParent) {
//                                View target = SmartRefreshLayout.this;
//                                //noinspection RedundantCast
//                                if (((NestedScrollingParent)parent).onStartNestedScroll(target,target,ViewCompat.SCROLL_AXIS_VERTICAL)) {
//                                    setNestedScrollingEnabled(true);
//                                    mManualNestedScrolling = false;
//                                    break;
//                                }
//                            }
//                            if (parent instanceof View) {
//                                View thisParent = (View) parent;
//                                parent = thisParent.getParent();
//                            } else {
//                                break;
//                            }
//                        }
//                    }
//                });
//            }
        }

        if (mPrimaryColors != null) {
            if (mRefreshHeader != null) {
                mRefreshHeader.setPrimaryColors(mPrimaryColors);
            }
            if (mRefreshFooter != null) {
                mRefreshFooter.setPrimaryColors(mPrimaryColors);
            }
        }

        //閲嶆柊鎺掑簭
        if (mRefreshContent != null) {
            super.bringChildToFront(mRefreshContent.getView());
        }
        if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
            super.bringChildToFront(mRefreshHeader.getView());
        }
        if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
            super.bringChildToFront(mRefreshFooter.getView());
        }

    }

    /**
     * 娴嬮噺 Header Footer Content
     * 1.娴嬮噺浠ｇ爜鐪嬭捣鏉ュ緢澶嶆潅锛屾椂鍥犱负 Header Footer 鏈夊洓绉嶆媺浼稿彉鎹㈡牱寮?{@link SpinnerStyle}锛屾瘡涓€绉嶆牱寮忔湁鑷繁鐨勬祴閲忔柟娉?     * 2.鎻愪緵棰勮娴嬮噺锛屽彲浠ュ湪缂栬緫 XML 鐨勬椂鍊欑洿鎺ラ瑙?锛坕sInEditMode锛?     * 3.鎭㈠姘村钩瑙︽懜浣嶇疆缂撳瓨 mLastTouchX 鍒板睆骞曚腑澶?     * @param widthMeasureSpec 姘村钩娴嬮噺鍙傛暟
     * @param heightMeasureSpec 绔栫洿娴嬮噺鍙傛暟
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec,final int heightMeasureSpec) {
        int minimumHeight = 0;
        final View thisView = this;
        final boolean needPreview = thisView.isInEditMode() && mEnablePreviewInEditMode;

        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);

            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                final View headerView = mRefreshHeader.getView();
                final LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
                int height = mHeaderHeight;

                if (mHeaderHeightStatus.ordinal() < DimensionStatus.XmlLayoutUnNotify.ordinal()) {
                    if (lp.height > 0) {
                        height =  lp.height + lp.bottomMargin + lp.topMargin;
                        if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlExactUnNotify)) {
                            mHeaderHeight = lp.height + lp.bottomMargin + lp.topMargin;
                            mHeaderHeightStatus = DimensionStatus.XmlExactUnNotify;
                        }
                    } else if (lp.height == WRAP_CONTENT && (mRefreshHeader.getSpinnerStyle() != SpinnerStyle.MatchLayout || !mHeaderHeightStatus.notified)) {
                        final int maxHeight = Math.max(getSize(heightMeasureSpec) - lp.bottomMargin - lp.topMargin, 0);
                        headerView.measure(widthSpec, makeMeasureSpec(maxHeight, AT_MOST));
                        final int measuredHeight = headerView.getMeasuredHeight();
                        if (measuredHeight > 0) {
                            height = -1;
                            if (measuredHeight != (maxHeight) && mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlWrapUnNotify)) {
                                mHeaderHeight = measuredHeight + lp.bottomMargin + lp.topMargin;
                                mHeaderHeightStatus = DimensionStatus.XmlWrapUnNotify;
                            }
                        }
                    }
                }

                if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.MatchLayout) {
                    height = getSize(heightMeasureSpec);
                } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale && !needPreview) {
                    height = Math.max(0, isEnableRefreshOrLoadMore(mEnableRefresh) ? mSpinner : 0);
                }

                if (height != -1) {
                    headerView.measure(widthSpec, makeMeasureSpec(Math.max(height - lp.bottomMargin - lp.topMargin, 0), EXACTLY));
                }

                if (!mHeaderHeightStatus.notified) {
                    mHeaderHeightStatus = mHeaderHeightStatus.notified();
                    mRefreshHeader.onInitialized(mKernel, mHeaderHeight, (int) (mHeaderMaxDragRate * mHeaderHeight));
                }

                if (needPreview && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                    minimumHeight += headerView.getMeasuredHeight();
                }
            }

            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final View footerView = mRefreshFooter.getView();
                final LayoutParams lp = (LayoutParams) footerView.getLayoutParams();
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width);
                int height = mFooterHeight;

                if (mFooterHeightStatus.ordinal() < DimensionStatus.XmlLayoutUnNotify.ordinal()) {
                    if (lp.height > 0) {
                        height = lp.height + lp.topMargin + lp.bottomMargin;
                        if (mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlExactUnNotify)) {
                            mFooterHeight = lp.height + lp.topMargin + lp.bottomMargin;
                            mFooterHeightStatus = DimensionStatus.XmlExactUnNotify;
                        }
                    } else if (lp.height == WRAP_CONTENT && (mRefreshFooter.getSpinnerStyle() != SpinnerStyle.MatchLayout || !mFooterHeightStatus.notified)) {
                        int maxHeight = Math.max(getSize(heightMeasureSpec) - lp.bottomMargin - lp.topMargin, 0);
                        footerView.measure(widthSpec, makeMeasureSpec(maxHeight, AT_MOST));
                        int measuredHeight = footerView.getMeasuredHeight();
                        if (measuredHeight > 0) {
                            height = -1;
                            if (measuredHeight != (maxHeight) && mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlWrapUnNotify)) {
                                mFooterHeight = measuredHeight + lp.topMargin + lp.bottomMargin;
                                mFooterHeightStatus = DimensionStatus.XmlWrapUnNotify;
                            }
                        }
                    }
                }

                if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.MatchLayout) {
                    height = getSize(heightMeasureSpec);
                } else if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale && !needPreview) {
                    height = Math.max(0, isEnableRefreshOrLoadMore(mEnableLoadMore) ? -mSpinner : 0);
                }

                if (height != -1) {
                    footerView.measure(widthSpec, makeMeasureSpec(Math.max(height - lp.bottomMargin - lp.topMargin, 0), EXACTLY));
                }

                if (!mFooterHeightStatus.notified) {
                    mFooterHeightStatus = mFooterHeightStatus.notified();
                    mRefreshFooter.onInitialized(mKernel, mFooterHeight, (int) (mFooterMaxDragRate * mFooterHeight));
                }

                if (needPreview && isEnableRefreshOrLoadMore(mEnableLoadMore)) {
                    minimumHeight += footerView.getMeasuredHeight();
                }
            }

            if (mRefreshContent != null && mRefreshContent.getView() == child) {
                final View contentView = mRefreshContent.getView();
                final LayoutParams lp = (LayoutParams) contentView.getLayoutParams();
                final boolean showHeader = (mRefreshHeader != null && isEnableRefreshOrLoadMore(mEnableRefresh) && isEnableTranslationContent(mEnableHeaderTranslationContent, mRefreshHeader));
                final boolean showFooter = (mRefreshFooter != null && isEnableRefreshOrLoadMore(mEnableLoadMore) && isEnableTranslationContent(mEnableFooterTranslationContent, mRefreshFooter));
                final int widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        thisView.getPaddingLeft() + thisView.getPaddingRight() +  lp.leftMargin + lp.rightMargin, lp.width);
                final int heightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                        thisView.getPaddingTop() + thisView.getPaddingBottom() + lp.topMargin + lp.bottomMargin +
                                ((needPreview && showHeader) ? mHeaderHeight : 0) +
                                ((needPreview && showFooter) ? mFooterHeight : 0), lp.height);
                contentView.measure(widthSpec, heightSpec);
                minimumHeight += contentView.getMeasuredHeight();
            }
        }

        super.setMeasuredDimension(
                View.resolveSize(super.getSuggestedMinimumWidth(), widthMeasureSpec),
                View.resolveSize(minimumHeight, heightMeasureSpec));

        mLastTouchX = thisView.getMeasuredWidth() / 2;
    }

    /**
     * 甯冨眬 Header Footer Content
     * 1.甯冨眬浠ｇ爜鐪嬭捣鏉ョ浉瀵圭畝鍗曪紝鏃跺洜涓烘祴閲忕殑鏃跺€欙紝宸茬粡鍋氫簡澶嶆潅鐨勮绠楋紝甯冨眬鐨勬椂鍊欙紝鐩存帴鎸夌収娴嬮噺缁撴灉锛屽竷灞€灏卞彲浠ヤ簡
     * @param changed 鏄惁鏀瑰彉
     * @param l 宸?     * @param t 涓?     * @param r 鍙?     * @param b 涓?     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final View thisView = this;
        final int paddingLeft = thisView.getPaddingLeft();
        final int paddingTop = thisView.getPaddingTop();
        final int paddingBottom = thisView.getPaddingBottom();

        for (int i = 0, len = super.getChildCount(); i < len; i++) {
            View child = super.getChildAt(i);

            if (mRefreshContent != null && mRefreshContent.getView() == child) {
                boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableRefresh) && mRefreshHeader != null;
                final View contentView = mRefreshContent.getView();
                final LayoutParams lp = (LayoutParams) contentView.getLayoutParams();
                int left = paddingLeft + lp.leftMargin;
                int top = paddingTop + lp.topMargin;
                int right = left + contentView.getMeasuredWidth();
                int bottom = top + contentView.getMeasuredHeight();
                if (isPreviewMode && (isEnableTranslationContent(mEnableHeaderTranslationContent, mRefreshHeader))) {
                    top = top + mHeaderHeight;
                    bottom = bottom + mHeaderHeight;
                }

                contentView.layout(left, top, right, bottom);
            }
            if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
                boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableRefresh);
                final View headerView = mRefreshHeader.getView();
                final LayoutParams lp = (LayoutParams) headerView.getLayoutParams();
                int left = lp.leftMargin;
                int top = lp.topMargin + mHeaderInsetStart;
                int right = left + headerView.getMeasuredWidth();
                int bottom = top + headerView.getMeasuredHeight();
                if (!isPreviewMode) {
                    if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        top = top - mHeaderHeight;
                        bottom = bottom - mHeaderHeight;
                        /*
                         * SpinnerStyle.Scale  headerView.getMeasuredHeight() 宸茬粡閲嶅澶勭悊
                         **/
//                    } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale && mSpinner > 0) {
//                        bottom = top + Math.max(Math.max(0, isEnableRefreshOrLoadMore(mEnableRefresh) ? mSpinner : 0) - lp.bottomMargin - lp.topMargin, 0);
                    }
                }
                headerView.layout(left, top, right, bottom);
            }
            if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
                final boolean isPreviewMode = thisView.isInEditMode() && mEnablePreviewInEditMode && isEnableRefreshOrLoadMore(mEnableLoadMore);
                final View footerView = mRefreshFooter.getView();
                final LayoutParams lp = (LayoutParams) footerView.getLayoutParams();
                final SpinnerStyle style = mRefreshFooter.getSpinnerStyle();
                int left = lp.leftMargin;
                int top = lp.topMargin + thisView.getMeasuredHeight() - mFooterInsetStart;

                if (style == SpinnerStyle.MatchLayout) {
                    top = lp.topMargin - mFooterInsetStart;
                } else if (isPreviewMode
                        || style == SpinnerStyle.FixedFront
                        || style == SpinnerStyle.FixedBehind) {
                    top = top - mFooterHeight;
                } else if (style == SpinnerStyle.Scale && mSpinner < 0) {
                    top = top - Math.max(isEnableRefreshOrLoadMore(mEnableLoadMore) ? -mSpinner : 0, 0);
                }

                int right = left + footerView.getMeasuredWidth();
                int bottom = top + footerView.getMeasuredHeight();
                footerView.layout(left, top, right, bottom);
            }
        }
    }

    /**
     * 閲嶅啓 onDetachedFromWindow 鏉ュ畬鎴?smart 鐨勭壒瀹氬姛鑳?     * 1.鎭㈠鍘熷鐘舵€?     * 2.娓呴櫎鍔ㄧ敾鏁版嵁 锛堥槻姝㈠唴瀛樻硠闇诧級
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mKernel.moveSpinner(0, true);
        notifyStateChanged(RefreshState.None);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        if (mListDelayedRunnable != null) {
            mListDelayedRunnable.clear();
            mListDelayedRunnable = null;
        }
        mManualLoadMore = true;
//        mManualNestedScrolling = true;
        animationRunnable = null;
        if (reboundAnimator != null) {
            reboundAnimator.removeAllListeners();
            reboundAnimator.removeAllUpdateListeners();
            reboundAnimator.cancel();
            reboundAnimator = null;
        }
    }

    /**
     * 閲嶅啓 drawChild 鏉ュ畬鎴?smart 鐨勭壒瀹氬姛鑳?     * 1.涓?Header 鍜?Footer 缁樺埗鑳屾櫙 锛堣缃簡鑳屾櫙鎵嶇粯鍒讹級
     * 2.涓?Header 鍜?Footer 鍦?FixedBehind 鏍峰紡鏃讹紝鍋氬壀瑁佸姛鑳?锛坢EnableClipHeaderWhenFixedBehind=true 鎵嶅仛锛?     * @param canvas 缁樺埗鍙戝竷
     * @param child 闇€瑕佺粯鍒剁殑瀛怴iew
     * @param drawingTime 缁樺埗鑰楁椂
     */
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        final View thisView = this;
        final View contentView = mRefreshContent != null ? mRefreshContent.getView() : null;
        if (mRefreshHeader != null && mRefreshHeader.getView() == child) {
            if (!isEnableRefreshOrLoadMore(mEnableRefresh) || (!mEnablePreviewInEditMode && thisView.isInEditMode())) {
                return true;
            }
            if (contentView != null) {
                int bottom = Math.max(contentView.getTop() + contentView.getPaddingTop() + mSpinner, child.getTop());
                if (mHeaderBackgroundColor != 0 && mPaint != null) {
                    mPaint.setColor(mHeaderBackgroundColor);
                    if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale) {
                        bottom = child.getBottom();
                    } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                        bottom = child.getBottom() + mSpinner;
                    }
                    canvas.drawRect(child.getLeft(), child.getTop(), child.getRight(), bottom, mPaint);
                }
                if (mEnableClipHeaderWhenFixedBehind && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), child.getTop(), child.getRight(), bottom);
                    boolean ret = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return ret;
                }
            }
        }
        if (mRefreshFooter != null && mRefreshFooter.getView() == child) {
            if (!isEnableRefreshOrLoadMore(mEnableLoadMore) || (!mEnablePreviewInEditMode && thisView.isInEditMode())) {
                return true;
            }
            if (contentView != null) {
                int top = Math.min(contentView.getBottom() - contentView.getPaddingBottom() + mSpinner, child.getBottom());
                if (mFooterBackgroundColor != 0 && mPaint != null) {
                    mPaint.setColor(mFooterBackgroundColor);
                    if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale) {
                        top = child.getTop();
                    } else if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
                        top = child.getTop() + mSpinner;
                    }
                    canvas.drawRect(child.getLeft(), top, child.getRight(), child.getBottom(), mPaint);
                }
                if (mEnableClipFooterWhenFixedBehind && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                    canvas.save();
                    canvas.clipRect(child.getLeft(), top, child.getRight(), child.getBottom());
                    boolean ret = super.drawChild(canvas, child, drawingTime);
                    canvas.restore();
                    return ret;
                }
            }

        }
        return super.drawChild(canvas, child, drawingTime);
    }


    //<editor-fold desc="鎯€ц绠?>
    protected boolean mVerticalPermit = false;                  //绔栫洿閫氫俊璇侊紙鐢ㄤ簬鐗规畩浜嬩欢鐨勬潈闄愬垽瀹氾級

    /**
     * 閲嶅啓 computeScroll 鏉ュ畬鎴?smart 鐨勭壒瀹氬姛鑳?     * 1.瓒婄晫鍥炲脊
     * 2.杈圭晫纰版挒
     */
    @Override
    public void computeScroll() {
        int lastCurY = mScroller.getCurrY();
        if (mScroller.computeScrollOffset()) {
            int finalY = mScroller.getFinalY();
            if ((finalY < 0 && (mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableRefresh)) && mRefreshContent.canRefresh())
                    || (finalY > 0 && (mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableLoadMore)) && mRefreshContent.canLoadMore())) {
                if(mVerticalPermit) {
                    float velocity;
                    if (Build.VERSION.SDK_INT >= 14) {
                        velocity = finalY > 0 ? -mScroller.getCurrVelocity() : mScroller.getCurrVelocity();
                    } else {
                        velocity = 1f * (mScroller.getCurrY() - finalY) / Math.max((mScroller.getDuration() - mScroller.timePassed()), 1);
                    }
                    animSpinnerBounce(velocity);
                }
                mScroller.forceFinished(true);
            } else {
                mVerticalPermit = true;//鎵撳紑绔栫洿閫氳璇?                final View thisView = this;
                thisView.invalidate();
            }
        }
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="婊戝姩鍒ゆ柇 judgement of slide">
    protected MotionEvent mFalsifyEvent = null;

    /**
     * 浜嬩欢鍒嗗彂 锛堟墜鍔挎牳蹇冿級
     * 1.澶氱偣瑙︽懜
     * 2.鏃犵紳琛旀帴鍐呭婊氬姩
     * @param e 浜嬩欢
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {

        //<editor-fold desc="澶氱偣瑙︽懜璁＄畻浠ｇ爜">
        //---------------------------------------------------------------------------
        //澶氱偣瑙︽懜璁＄畻浠ｇ爜
        //---------------------------------------------------------------------------
        final int action = e.getActionMasked();
        final boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        final int skipIndex = pointerUp ? e.getActionIndex() : -1;

        // Determine focal point
        float sumX = 0, sumY = 0;
        final int count = e.getPointerCount();
        for (int i = 0; i < count; i++) {
            if (skipIndex == i) continue;
            sumX += e.getX(i);
            sumY += e.getY(i);
        }
        final int div = pointerUp ? count - 1 : count;
        final float touchX = sumX / div;
        final float touchY = sumY / div;
        if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_POINTER_DOWN)
                && mIsBeingDragged) {
            mTouchY += touchY - mLastTouchY;
        }
        mLastTouchX = touchX;
        mLastTouchY = touchY;
        //---------------------------------------------------------------------------
        //</editor-fold>



        //---------------------------------------------------------------------------
        //宓屽婊氬姩妯″紡杈呭姪
        //---------------------------------------------------------------------------
        final View thisView = this;
        if (mNestedInProgress) {//宓屽婊氬姩鏃讹紝琛ュ厖绔栫洿鏂瑰悜涓嶆粴鍔紝浣嗘槸姘村钩鏂瑰悜婊氬姩锛岄渶瑕侀€氱煡 onHorizontalDrag
            int totalUnconsumed = mTotalUnconsumed;
            boolean ret = super.dispatchTouchEvent(e);
            //noinspection ConstantConditions
            if (action == MotionEvent.ACTION_MOVE) {
                if (totalUnconsumed == mTotalUnconsumed) {
                    final int offsetX = (int) mLastTouchX;
                    final int offsetMax = thisView.getWidth();
                    final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                    if (isEnableRefreshOrLoadMore(mEnableRefresh) && mSpinner > 0 && mRefreshHeader != null && mRefreshHeader.isSupportHorizontalDrag()) {
                        mRefreshHeader.onHorizontalDrag(percentX, offsetX, offsetMax);
                    } else if (isEnableRefreshOrLoadMore(mEnableLoadMore) && mSpinner < 0 && mRefreshFooter != null && mRefreshFooter.isSupportHorizontalDrag()) {
                        mRefreshFooter.onHorizontalDrag(percentX, offsetX, offsetMax);
                    }
                }
            }
            return ret;
        } else if (!thisView.isEnabled()
                || (!isEnableRefreshOrLoadMore(mEnableRefresh) && !isEnableRefreshOrLoadMore(mEnableLoadMore) && !mEnableOverScrollDrag)
                || (mHeaderNeedTouchEventWhenRefreshing && ((mState.isOpening || mState.isFinishing) && mState.isHeader))
                || (mFooterNeedTouchEventWhenLoading && ((mState.isOpening || mState.isFinishing) && mState.isFooter))) {
            return super.dispatchTouchEvent(e);
        }

        if (interceptAnimatorByAction(action) || mState.isFinishing
                || (mState == RefreshState.Loading && mDisableContentWhenLoading)
                || (mState == RefreshState.Refreshing && mDisableContentWhenRefresh)) {
            return false;
        }

//        if (mEnableNestedScrollingOnly && mNestedChild.isNestedScrollingEnabled()) {
//            return super.dispatchTouchEvent(e);
//        }
        //-------------------------------------------------------------------------//



        //---------------------------------------------------------------------------
        //浼犵粺妯″紡婊氬姩
        //---------------------------------------------------------------------------
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                /*----------------------------------------------------*/
                /*                   閫熷害杩借釜鍒濆鍖?                   */
                /*----------------------------------------------------*/
                mCurrentVelocity = 0;
                mVelocityTracker.addMovement(e);
                mScroller.forceFinished(true);
                /*----------------------------------------------------*/
                /*                   瑙︽懜浜嬩欢鍒濆鍖?                   */
                /*----------------------------------------------------*/
                mTouchX = touchX;
                mTouchY = touchY;
                mLastSpinner = 0;
                mTouchSpinner = mSpinner;
                mIsBeingDragged = false;
                /*----------------------------------------------------*/
                mSuperDispatchTouchEvent = super.dispatchTouchEvent(e);
                if (mState == RefreshState.TwoLevel && mTouchY < 5 * thisView.getMeasuredHeight() / 6) {
                    mDragDirection = 'h';//浜岀骇鍒锋柊鏍囪姘村钩婊氬姩鏉ョ姝㈡嫋鍔?                    return mSuperDispatchTouchEvent;
                }
                if (mRefreshContent != null) {
                    //涓?RefreshContent 浼犻€掑綋鍓嶈Е鎽镐簨浠剁殑鍧愭爣锛岀敤浜庢櫤鑳藉垽鏂搴斿潗鏍囦綅缃甐iew鐨勬粴鍔ㄨ竟鐣屽拰鐩稿叧淇℃伅
                    mRefreshContent.onActionDown(e);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = touchX - mTouchX;
                float dy = touchY - mTouchY;
                mVelocityTracker.addMovement(e);//閫熷害杩借釜
                if (!mIsBeingDragged && mDragDirection != 'h' && mRefreshContent != null) {//娌℃湁鎷栧姩涔嬪墠锛屾娴? canRefresh canLoadMore 鏉ュ紑鍚嫋鍔?                    if (mDragDirection == 'v' || (Math.abs(dy) >= mTouchSlop && Math.abs(dx) < Math.abs(dy))) {//婊戝姩鍏佽鏈€澶ц搴︿负45搴?                        mDragDirection = 'v';
                        if (dy > 0 && (mSpinner < 0 || ((mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableRefresh)) && mRefreshContent.canRefresh()))) {
                            mIsBeingDragged = true;
                            mTouchY = touchY - mTouchSlop;//璋冩暣 mTouchSlop 鍋忓樊
                        } else if (dy < 0 && (mSpinner > 0 || ((mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableLoadMore)) && ((mState==RefreshState.Loading&&mFooterLocked)||mRefreshContent.canLoadMore())))) {
                            mIsBeingDragged = true;
                            mTouchY = touchY + mTouchSlop;//璋冩暣 mTouchSlop 鍋忓樊
                        }
                        if (mIsBeingDragged) {
                            dy = touchY - mTouchY;//璋冩暣 mTouchSlop 鍋忓樊 閲嶆柊璁＄畻 dy
                            if (mSuperDispatchTouchEvent) {//濡傛灉鐖剁被鎷︽埅浜嗕簨浠讹紝鍙戦€佷竴涓彇娑堜簨浠堕€氱煡
                                e.setAction(MotionEvent.ACTION_CANCEL);
                                super.dispatchTouchEvent(e);
                            }
                            if (mSpinner > 0 || (mSpinner == 0 && dy > 0)) {
                                mKernel.setState(RefreshState.PullDownToRefresh);
                            } else {
                                mKernel.setState(RefreshState.PullUpToLoad);
                            }
                            ViewParent parent = thisView.getParent();
                            if (parent != null) {
                                //淇闂 https://github.com/scwang90/SmartRefreshLayout/issues/580
                                parent.requestDisallowInterceptTouchEvent(true);//閫氱煡鐖舵帶浠朵笉瑕佹嫤鎴簨浠?                            }
                        }
                    } else if (Math.abs(dx) >= mTouchSlop && Math.abs(dx) > Math.abs(dy) && mDragDirection != 'v') {
                        mDragDirection = 'h';//鏍囪涓烘按骞虫嫋鍔紝灏嗘棤娉曞啀娆¤Е鍙?涓嬫媺鍒锋柊 涓婃媺鍔犺浇
                    }
                }
                if (mIsBeingDragged) {
                    int spinner = (int) dy + mTouchSpinner;
                    if ((mViceState.isHeader && (spinner < 0 || mLastSpinner < 0)) || (mViceState.isFooter && (spinner > 0 || mLastSpinner > 0))) {
                        mLastSpinner = spinner;
                        long time = e.getEventTime();
                        if (mFalsifyEvent == null) {
                            mFalsifyEvent = obtain(time, time, MotionEvent.ACTION_DOWN, mTouchX + dx, mTouchY, 0);
                            super.dispatchTouchEvent(mFalsifyEvent);
                        }
                        MotionEvent em = obtain(time, time, MotionEvent.ACTION_MOVE, mTouchX + dx, mTouchY + spinner, 0);
                        super.dispatchTouchEvent(em);
                        if (mFooterLocked && dy > mTouchSlop && mSpinner < 0) {
                            mFooterLocked = false;//鍐呭鍚戜笅婊氬姩鏃?瑙ｉ攣Footer 鐨勯攣瀹?                        }
                        if (spinner > 0 && ((mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableRefresh)) && mRefreshContent.canRefresh())) {
                            mTouchY = mLastTouchY = touchY;
                            mTouchSpinner = spinner = 0;
                            mKernel.setState(RefreshState.PullDownToRefresh);
                        } else if (spinner < 0 && ((mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableLoadMore)) && mRefreshContent.canLoadMore())) {
                            mTouchY = mLastTouchY = touchY;
                            mTouchSpinner = spinner = 0;
                            mKernel.setState(RefreshState.PullUpToLoad);
                        }
                        if ((mViceState.isHeader && spinner < 0) || (mViceState.isFooter && spinner > 0)) {
                            if (mSpinner != 0) {
                                moveSpinnerInfinitely(0);
                            }
                            return true;
                        } else if (mFalsifyEvent != null) {
                            mFalsifyEvent = null;
                            em.setAction(MotionEvent.ACTION_CANCEL);
                            super.dispatchTouchEvent(em);
                        }
                        em.recycle();
                    }
                    moveSpinnerInfinitely(spinner);
                    return true;
                } else if (mFooterLocked && dy > mTouchSlop && mSpinner < 0) {
                    mFooterLocked = false;//鍐呭鍚戜笅婊氬姩鏃?瑙ｉ攣Footer 鐨勯攣瀹?                }
                break;
            case MotionEvent.ACTION_UP://鍚戜笂鎶捣鏃跺鐞嗛€熷害杩借釜
                mVelocityTracker.addMovement(e);
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                mCurrentVelocity = (int) mVelocityTracker.getYVelocity();
                startFlingIfNeed(null);
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.clear();//娓呯┖閫熷害杩借釜鍣?                mDragDirection = 'n';//鍏抽棴鎷栧姩鏂瑰悜
                if (mFalsifyEvent != null) {
                    mFalsifyEvent.recycle();
                    mFalsifyEvent = null;
                    long time = e.getEventTime();
                    MotionEvent ec = obtain(time, time, action, mTouchX, touchY, 0);
                    super.dispatchTouchEvent(ec);
                    ec.recycle();
                }
                overSpinner();
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;//鍏抽棴鎷栧姩鐘舵€?                    return true;
                }
                break;
        }
        //-------------------------------------------------------------------------//
        return super.dispatchTouchEvent(e);
    }

    /**
     * 鍦ㄥ繀瑕佺殑鏃跺€?寮€濮?Fling 妯″紡
     * @param flingVelocity 閫熷害
     * @return true 鍙互鎷︽埅 宓屽婊氬姩鐨?Fling
     */
    protected boolean startFlingIfNeed(Float flingVelocity) {
        final float velocity = flingVelocity == null ? mCurrentVelocity : flingVelocity;
        if (Math.abs(velocity) > mMinimumVelocity) {
            if (velocity * mSpinner < 0) {
                if (mState.isOpening) {
                    if (mState != RefreshState.TwoLevel && mState != mViceState) {
                        /*
                         * 瑙ｅ喅鍒锋柊鏃讹紝鎯€т涪澶遍棶棰?                         * 閫熷害鏂瑰悜鍖归厤骞朵笖涓嶈兘鏄簩妤兼墦寮€鐘舵€?                         * 鍓搷浣滅姸鎬?loading refreshing noMoreData
                         */
                        animationRunnable = new FlingRunnable(velocity).start();
                        return true;
                    }
                } else if (mSpinner > mHeaderHeight * mHeaderTriggerRate || -mSpinner > mFooterHeight * mFooterTriggerRate) {
                    return true;//鎷︽埅宓屽婊氬姩鏃讹紝鍗冲皢鍒锋柊鎴栬€呭姞杞界殑 Fling
                }
            }
            if ((velocity < 0 && ((mEnableOverScrollBounce && (mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableLoadMore))) || (mState == RefreshState.Loading && mSpinner >= 0) || (mEnableAutoLoadMore&&isEnableRefreshOrLoadMore(mEnableLoadMore))))
                    || (velocity > 0 && ((mEnableOverScrollBounce && (mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableRefresh))) || (mState == RefreshState.Refreshing && mSpinner <= 0)))) {
                mVerticalPermit = false;//鍏抽棴绔栫洿閫氳璇?                mScroller.fling(0, 0, 0, (int) -velocity, 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                mScroller.computeScrollOffset();
                final View thisView = this;
                thisView.invalidate();
            }
        }
        return false;
    }

    /**
     * 鍦ㄥ姩鐢绘墽琛屾椂锛岃Е鎽稿睆骞曪紝鎵撴柇鍔ㄧ敾锛岃浆涓烘嫋鍔ㄧ姸鎬?     * @param action MotionEvent
     * @return 鏄惁鎴愬姛鎵撴柇
     */
    protected boolean interceptAnimatorByAction(int action) {
        if (action == MotionEvent.ACTION_DOWN) {
            if (reboundAnimator != null) {
                if (mState.isFinishing || mState == RefreshState.TwoLevelReleased) {
                    return true;
                }
                if (mState == RefreshState.PullDownCanceled) {
                    mKernel.setState(RefreshState.PullDownToRefresh);
                } else if (mState == RefreshState.PullUpCanceled) {
                    mKernel.setState(RefreshState.PullUpToLoad);
                }
                reboundAnimator.cancel();
                reboundAnimator = null;
            }
            animationRunnable = null;
        }
        return reboundAnimator != null;
    }

//    /*
//     * 杩欐浠ｇ爜鏉ヨ嚜璋锋瓕瀹樻柟鐨?SwipeRefreshLayout
//     * 搴旂敤鍦烘櫙宸茬粡鍦ㄨ嫳鏂囨敞閲婁腑瑙ｉ噴娓呮
//     * 澶ч儴鍒嗙涓夋柟涓嬫媺鍒锋柊搴撻兘淇濈暀浜嗚繖娈典唬鐮侊紝鏈簱涔熶笉渚嬪
//     */
//    @Override
//    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//        // if this is a List < L or another view that doesn't support nested
//        // scrolling, ignore this request so that the vertical scroll event
//        // isn't stolen
//        View target = mRefreshContent.getScrollableView();
//        if ((android.os.Build.VERSION.SDK_INT >= 21 || !(target instanceof AbsListView))
//                && (target == null || ViewCompat.isNestedScrollingEnabled(target))) {
//            super.requestDisallowInterceptTouchEvent(disallowIntercept);
//            //} else {
//            // Nope.
//        }
//    }

    //</editor-fold>

    //<editor-fold desc="鐘舵€佹洿鏀?state changes">

    /**
     * 璁剧疆骞堕€氱煡鐘舵€佹敼鍙?锛坰etState锛?     * @param state 鐘舵€?     */
    protected void notifyStateChanged(RefreshState state) {
        final RefreshState oldState = mState;
        if (oldState != state) {
            mState = state;
            mViceState = state;
            final OnStateChangedListener refreshHeader = mRefreshHeader;
            final OnStateChangedListener refreshFooter = mRefreshFooter;
            final OnStateChangedListener refreshListener = mOnMultiPurposeListener;
            if (refreshHeader != null) {
                refreshHeader.onStateChanged(this, oldState, state);
            }
            if (refreshFooter != null) {
                refreshFooter.onStateChanged(this, oldState, state);
            }
            if (refreshListener != null) {
                refreshListener.onStateChanged(this, oldState, state);
            }
        }
    }

    /**
     * 鐩存帴灏嗙姸鎬佽缃负 Loading 姝ｅ湪鍔犺浇
     * @param triggerLoadMoreEvent 鏄惁瑙﹀彂鍔犺浇鍥炶皟
     */
    protected void setStateDirectLoading(boolean triggerLoadMoreEvent) {
        if (mState != RefreshState.Loading) {
            mLastOpenTime = currentTimeMillis();
//            if (mState != RefreshState.LoadReleased) {
//                if (mState != RefreshState.ReleaseToLoad) {
//                    if (mState != RefreshState.PullUpToLoad) {
//                        mKernel.setState(RefreshState.PullUpToLoad);
//                    }
//                    mKernel.setState(RefreshState.ReleaseToLoad);
//                }
//                notifyStateChanged(RefreshState.LoadReleased);
//                if (mRefreshFooter != null) {
//                    mRefreshFooter.onReleased(this, mFooterHeight, (int) (mFooterMaxDragRate * mFooterHeight));
//                }
//            }
            mFooterLocked = true;//Footer 姝ｅ湪loading 鐨勬椂鍊欐槸鍚﹂攣浣?鍒楄〃涓嶈兘鍚戜笂婊氬姩
            notifyStateChanged(RefreshState.Loading);
            if (mLoadMoreListener != null) {
                if (triggerLoadMoreEvent) {
                    mLoadMoreListener.onLoadMore(this);
                }
            } else if (mOnMultiPurposeListener == null) {
                finishLoadMore(2000);//濡傛灉娌℃湁浠讳綍鍔犺浇鐩戝惉鍣紝涓ょ涔嬪悗鑷姩鍏抽棴
            }
            if (mRefreshFooter != null) {
                mRefreshFooter.onStartAnimator(this, mFooterHeight, (int) (mFooterMaxDragRate * mFooterHeight));
            }
            if (mOnMultiPurposeListener != null && mRefreshFooter instanceof RefreshFooter) {
                final OnLoadMoreListener listener = mOnMultiPurposeListener;
                if (listener != null && triggerLoadMoreEvent) {
                    listener.onLoadMore(this);
                }
                mOnMultiPurposeListener.onFooterStartAnimator((RefreshFooter) mRefreshFooter, mFooterHeight, (int) (mFooterMaxDragRate * mFooterHeight));
            }
        }
    }

    /**
     * 璁剧疆鐘舵€佷负 Loading 姝ｅ湪鍔犺浇
     */
    protected void setStateLoading() {
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setStateDirectLoading(true);
            }
        };
        notifyStateChanged(RefreshState.LoadReleased);
        ValueAnimator animator = mKernel.animSpinner(-mFooterHeight);
        if (animator != null) {
            animator.addListener(listener);
        }
        if (mRefreshFooter != null) {
            //onReleased 鐨勬墽琛岄『搴忓畾鍦?animSpinner 涔嬪悗 onAnimationEnd 涔嬪墠
            // 杩欐牱 onReleased 鍐呴儴 鍙互鍋氬嚭 瀵?鍓嶉潰 animSpinner 鐨勮鐩?鎿嶄綔
            mRefreshFooter.onReleased(this, mFooterHeight, (int) (mFooterMaxDragRate * mFooterHeight));
        }
        if (mOnMultiPurposeListener != null && mRefreshFooter instanceof RefreshFooter) {
            //鍚?mRefreshFooter.onReleased 涓€鑷?            mOnMultiPurposeListener.onFooterReleased((RefreshFooter) mRefreshFooter, mFooterHeight, (int) (mFooterMaxDragRate * mFooterHeight));
        }
        if (animator == null) {
            //onAnimationEnd 浼氭敼鍙樼姸鎬佷负 loading 蹇呴』鍦?onReleased 涔嬪悗璋冪敤
            listener.onAnimationEnd(null);
        }
    }

    /**
     * 璁剧疆鐘舵€佷负 Refreshing 姝ｅ湪鍒锋柊
     */
    protected void setStateRefreshing() {
        AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLastOpenTime = currentTimeMillis();
                notifyStateChanged(RefreshState.Refreshing);
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh(SmartRefreshLayout.this);
                } else if (mOnMultiPurposeListener == null) {
                    finishRefresh(3000);
                }
                if (mRefreshHeader != null) {
                    mRefreshHeader.onStartAnimator(SmartRefreshLayout.this, mHeaderHeight,  (int) (mHeaderMaxDragRate * mHeaderHeight));
                }
                if (mOnMultiPurposeListener != null && mRefreshHeader instanceof RefreshHeader) {
                    mOnMultiPurposeListener.onRefresh(SmartRefreshLayout.this);
                    mOnMultiPurposeListener.onHeaderStartAnimator((RefreshHeader) mRefreshHeader, mHeaderHeight,  (int) (mHeaderMaxDragRate * mHeaderHeight));
                }
            }
        };
        notifyStateChanged(RefreshState.RefreshReleased);
        ValueAnimator animator = mKernel.animSpinner(mHeaderHeight);
        if (animator != null) {
            animator.addListener(listener);
        }
        if (mRefreshHeader != null) {
            //onReleased 鐨勬墽琛岄『搴忓畾鍦?animSpinner 涔嬪悗 onAnimationEnd 涔嬪墠
            // 杩欐牱 onRefreshReleased鍐呴儴 鍙互鍋氬嚭 瀵?鍓嶉潰 animSpinner 鐨勮鐩?鎿嶄綔
            mRefreshHeader.onReleased(this, mHeaderHeight,  (int) (mHeaderMaxDragRate * mHeaderHeight));
        }
        if (mOnMultiPurposeListener != null && mRefreshHeader instanceof RefreshHeader) {
            //鍚?mRefreshHeader.onReleased 涓€鑷?            mOnMultiPurposeListener.onHeaderReleased((RefreshHeader)mRefreshHeader, mHeaderHeight,  (int) (mHeaderMaxDragRate * mHeaderHeight));
        }
        if (animator == null) {
            //onAnimationEnd 浼氭敼鍙樼姸鎬佷负 Refreshing 蹇呴』鍦?onReleased 涔嬪悗璋冪敤
            listener.onAnimationEnd(null);
        }
    }

    /**
     * 閲嶇疆鐘舵€?     */
    protected void resetStatus() {
        if (mState != RefreshState.None) {
            if (mSpinner == 0) {
                notifyStateChanged(RefreshState.None);
            }
        }
        if (mSpinner != 0) {
            mKernel.animSpinner(0);
        }
    }

    /**
     * 璁剧疆 鍓姸鎬?     * @param state 鐘舵€?     */
    protected void setViceState(RefreshState state) {
        if (mState.isDragging && mState.isHeader != state.isHeader) {
            notifyStateChanged(RefreshState.None);
        }
        if (mViceState != state) {
            mViceState = state;
        }
    }

    /**
     * 鍒ゆ柇鏄惁 涓嬫媺鐨勬椂鍊?闇€瑕?绉诲姩鍐呭
     * @param enable mEnableHeaderTranslationContent or mEnableFooterTranslationContent
     * @param internal mRefreshHeader or mRefreshFooter
     * @return enable
     */
    protected boolean isEnableTranslationContent(boolean enable, RefreshInternal internal) {
        return enable || mEnablePureScrollMode || internal == null || internal.getSpinnerStyle() == SpinnerStyle.FixedBehind;
    }

    /**
     * 鍒ゆ柇鏃跺€欏彲浠?鍒锋柊 鎴栬€?鍔犺浇
     * @param enable mEnableRefresh or mEnableLoadMore
     * @return enable
     */
    protected boolean isEnableRefreshOrLoadMore(boolean enable) {
        return enable && !mEnablePureScrollMode;
    }

    //</editor-fold>

    //<editor-fold desc="瑙嗗浘浣嶇Щ displacement">

    //<editor-fold desc="鍔ㄧ敾鐩戝惉 Animator Listener">
    protected Runnable animationRunnable;
    protected ValueAnimator reboundAnimator;
    protected class FlingRunnable implements Runnable {
        int mOffset;
        int mFrame = 0;
        int mFrameDelay = 10;
        float mVelocity;
        float mDamping = 0.98f;//姣忓抚閫熷害琛板噺鍊?        long mStartTime = 0;
        long mLastTime = AnimationUtils.currentAnimationTimeMillis();

        FlingRunnable(float velocity) {
            mVelocity = velocity;
            mOffset = mSpinner;
        }

        public Runnable start() {
            if (mState.isFinishing) {
                return null;
            }
            if (mSpinner != 0 && (!(mState.isOpening || (mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore)))
                    || ((mState == RefreshState.Loading || (mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore))) && mSpinner < -mFooterHeight)
                    || (mState == RefreshState.Refreshing && mSpinner > mHeaderHeight))) {
                int frame = 0;
                int offset = mSpinner;
                int spinner = mSpinner;
                float velocity = mVelocity;
                while (spinner * offset > 0) {
                    velocity *= Math.pow(mDamping, (++frame) * mFrameDelay / 10);
                    float velocityFrame = (velocity * (1f * mFrameDelay / 1000));
                    if (Math.abs(velocityFrame) < 1) {
                        if (!mState.isOpening
                                || (mState == RefreshState.Refreshing && offset > mHeaderHeight)
                                || (mState != RefreshState.Refreshing && offset < -mFooterHeight)) {
                            return null;
                        }
                        break;
                    }
                    offset += velocityFrame;
                }
            }
            mStartTime = AnimationUtils.currentAnimationTimeMillis();
            postDelayed(this, mFrameDelay);
            return this;
        }

        @Override
        public void run() {
            if (animationRunnable == this && !mState.isFinishing) {
//                mVelocity *= Math.pow(mDamping, ++mFrame);
                long now = AnimationUtils.currentAnimationTimeMillis();
                long span = now - mLastTime;
                mVelocity *= Math.pow(mDamping, (now - mStartTime) / (1000 / mFrameDelay));
                float velocity = (mVelocity * (1f * span / 1000));
                if (Math.abs(velocity) > 1) {
                    mLastTime = now;
                    mOffset += velocity;
                    if (mSpinner * mOffset > 0) {
                        mKernel.moveSpinner(mOffset, true);
                        postDelayed(this, mFrameDelay);
                    } else {
                        animationRunnable = null;
                        mKernel.moveSpinner(0, true);
                        SmartUtil.fling(mRefreshContent.getScrollableView(), (int) -mVelocity);
                        if (mFooterLocked && velocity > 0) {
                            mFooterLocked = false;
                        }
                    }
                } else {
                    animationRunnable = null;
                }
            }
        }
    }
    protected class BounceRunnable implements Runnable {
        int mFrame = 0;
        int mFrameDelay = 10;
        int mSmoothDistance;
        long mLastTime;
        float mOffset = 0;
        float mVelocity;
        BounceRunnable(float velocity, int smoothDistance){
            mVelocity = velocity;
            mSmoothDistance = smoothDistance;
            mLastTime = AnimationUtils.currentAnimationTimeMillis();
            postDelayed(this, mFrameDelay);
        }
        @Override
        public void run() {
            if (animationRunnable == this && !mState.isFinishing) {
                if (Math.abs(mSpinner) >= Math.abs(mSmoothDistance)) {
                    if (mSmoothDistance != 0) {
                        mVelocity *= Math.pow(0.45f, ++mFrame * 2);//鍒锋柊銆佸姞杞芥椂鍥炲脊婊氬姩鏁板害琛板噺
                    } else {
                        mVelocity *= Math.pow(0.85f, ++mFrame * 2);//鍥炲脊婊氬姩鏁板害琛板噺
                    }
                } else {
                    mVelocity *= Math.pow(0.95f, ++mFrame * 2);//骞虫粦婊氬姩鏁板害琛板噺
                }
                long now = AnimationUtils.currentAnimationTimeMillis();
                float t = 1f * (now - mLastTime) / 1000;
                float velocity = mVelocity * t;
                if (Math.abs(velocity) >= 1) {
                    mLastTime = now;
                    mOffset += velocity;
                    moveSpinnerInfinitely(mOffset);
                    postDelayed(this, mFrameDelay);
                } else {
                    animationRunnable = null;
                    if (Math.abs(mSpinner) >= Math.abs(mSmoothDistance)) {
                        int duration = 10 * Math.min(Math.max((int) DensityUtil.px2dp(Math.abs(mSpinner-mSmoothDistance)), 30), 100);
                        animSpinner(mSmoothDistance, 0, mReboundInterpolator, duration);
                    }
                }
            }
        }
    }
    //</editor-fold>

    /**
     * 鎵ц鍥炲脊鍔ㄧ敾
     * @param endSpinner 鐩爣鍊?     * @param startDelay 寤舵椂鍙傛暟
     * @param interpolator 鍔犻€熷櫒
     * @param duration 鏃堕暱
     * @return ValueAnimator or null
     */
    protected ValueAnimator animSpinner(int endSpinner, int startDelay, Interpolator interpolator, int duration) {
        if (mSpinner != endSpinner) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            animationRunnable = null;
            reboundAnimator = ValueAnimator.ofInt(mSpinner, endSpinner);
            reboundAnimator.setDuration(duration);
            reboundAnimator.setInterpolator(interpolator);
            reboundAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationEnd(animation);
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    reboundAnimator = null;
                    if (mSpinner == 0) {
                        if (mState != RefreshState.None && !mState.isOpening) {
                            notifyStateChanged(RefreshState.None);
                        }
                    } else if (mState != mViceState) {
                        setViceState(mState);
                    }
                }
            });
            reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mKernel.moveSpinner((int) animation.getAnimatedValue(), false);
                }
            });
            reboundAnimator.setStartDelay(startDelay);
//            reboundAnimator.setDuration(20000);
            reboundAnimator.start();
            return reboundAnimator;
        }
        return null;
    }

    /**
     * 瓒婄晫鍥炲脊鍔ㄧ敾
     * @param velocity 閫熷害
     */
    protected void animSpinnerBounce(final float velocity) {
        if (reboundAnimator == null) {
            if (velocity > 0 && (mState == RefreshState.Refreshing || mState == RefreshState.TwoLevel)) {
                animationRunnable = new BounceRunnable(velocity, mHeaderHeight);
            } else if (velocity < 0 && (mState == RefreshState.Loading
                    || (mEnableFooterFollowWhenNoMoreData && mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore))
                    || (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore) && mState != RefreshState.Refreshing))) {
                animationRunnable = new BounceRunnable(velocity, -mFooterHeight);
            } else if (mSpinner == 0 && mEnableOverScrollBounce) {
                animationRunnable = new BounceRunnable(velocity, 0);
            }
        }
    }

    /**
     * 鎵嬪娍鎷栧姩缁撴潫
     * 寮€濮嬫墽琛屽洖寮瑰姩鐢?     */
    protected void overSpinner() {
        if (mState == RefreshState.TwoLevel) {
            final View thisView = this;
            if (mCurrentVelocity > -1000 && mSpinner > thisView.getMeasuredHeight() / 2) {
                ValueAnimator animator = mKernel.animSpinner(thisView.getMeasuredHeight());
                if (animator != null) {
                    animator.setDuration(mFloorDuration);
                }
            } else if (mIsBeingDragged) {
                mKernel.finishTwoLevel();
            }
        } else if (mState == RefreshState.Loading
                || (mEnableFooterFollowWhenNoMoreData && mFooterNoMoreData && mSpinner < 0 && isEnableRefreshOrLoadMore(mEnableLoadMore))) {
            if (mSpinner < -mFooterHeight) {
                mKernel.animSpinner(-mFooterHeight);
            } else if (mSpinner > 0) {
                mKernel.animSpinner(0);
            }
        } else if (mState == RefreshState.Refreshing) {
            if (mSpinner > mHeaderHeight) {
                mKernel.animSpinner(mHeaderHeight);
            } else if (mSpinner < 0) {
                mKernel.animSpinner(0);
            }
        } else if (mState == RefreshState.PullDownToRefresh) {
            mKernel.setState(RefreshState.PullDownCanceled);
        } else if (mState == RefreshState.PullUpToLoad) {
            mKernel.setState(RefreshState.PullUpCanceled);
        } else if (mState == RefreshState.ReleaseToRefresh) {
            mKernel.setState(RefreshState.Refreshing);
        } else if (mState == RefreshState.ReleaseToLoad) {
            mKernel.setState(RefreshState.Loading);
        } else if (mState == RefreshState.ReleaseToTwoLevel) {
            mKernel.setState(RefreshState.TwoLevelReleased);
        } else if (mState == RefreshState.RefreshReleased) {
            if (reboundAnimator == null) {
                mKernel.animSpinner(mHeaderHeight);
            }
        } else if (mState == RefreshState.LoadReleased) {
            if (reboundAnimator == null) {
                mKernel.animSpinner(-mFooterHeight);
            }
        } else if (mSpinner != 0) {
            mKernel.animSpinner(0);
        }
    }

    /**
     * 榛忔€хЩ鍔?spinner
     * @param spinner 鍋忕Щ閲?     */
    protected void moveSpinnerInfinitely(float spinner) {
        final View thisView = this;
        if (mState == RefreshState.TwoLevel && spinner > 0) {
            mKernel.moveSpinner(Math.min((int) spinner, thisView.getMeasuredHeight()), true);
        } else if (mState == RefreshState.Refreshing && spinner >= 0) {
            if (spinner < mHeaderHeight) {
                mKernel.moveSpinner((int) spinner, true);
            } else {
                final double M = (mHeaderMaxDragRate - 1) * mHeaderHeight;
                final double H = Math.max(mScreenHeightPixels * 4 / 3, thisView.getHeight()) - mHeaderHeight;
                final double x = Math.max(0, (spinner - mHeaderHeight) * mDragRate);
                final double y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 鍏紡 y = M(1-100^(-x/H))
                mKernel.moveSpinner((int) y + mHeaderHeight, true);
            }
        } else if (spinner < 0 && (mState == RefreshState.Loading
                || (mEnableFooterFollowWhenNoMoreData && mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore))
                || (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore)))) {
            if (spinner > -mFooterHeight) {
                mKernel.moveSpinner((int) spinner, true);
            } else {
                final double M = (mFooterMaxDragRate - 1) * mFooterHeight;
                final double H = Math.max(mScreenHeightPixels * 4 / 3, thisView.getHeight()) - mFooterHeight;
                final double x = -Math.min(0, (spinner + mFooterHeight) * mDragRate);
                final double y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 鍏紡 y = M(1-100^(-x/H))
                mKernel.moveSpinner((int) y - mFooterHeight, true);
            }
        } else if (spinner >= 0) {
            final double M = mHeaderMaxDragRate * mHeaderHeight;
            final double H = Math.max(mScreenHeightPixels / 2, thisView.getHeight());
            final double x = Math.max(0, spinner * mDragRate);
            final double y = Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 鍏紡 y = M(1-100^(-x/H))
            mKernel.moveSpinner((int) y, true);
        } else {
            final double M = mFooterMaxDragRate * mFooterHeight;
            final double H = Math.max(mScreenHeightPixels / 2, thisView.getHeight());
            final double x = -Math.min(0, spinner * mDragRate);
            final double y = -Math.min(M * (1 - Math.pow(100, -x / (H == 0 ? 1 : H))), x);// 鍏紡 y = M(1-100^(-x/H))
            mKernel.moveSpinner((int) y, true);
        }
        if (mEnableAutoLoadMore && !mFooterNoMoreData && isEnableRefreshOrLoadMore(mEnableLoadMore) && spinner < 0
                && mState != RefreshState.Refreshing
                && mState != RefreshState.Loading
                && mState != RefreshState.LoadFinish) {
            if (mDisableContentWhenLoading) {
                animationRunnable = null;
                mKernel.animSpinner(-mFooterHeight);
            }
            setStateDirectLoading(false);
            /*
             * 鑷姩鍔犺浇妯″紡鏃讹紝寤惰繜瑙﹀彂 onLoadMore 锛宮ReboundDuration 淇濊瘉鍔ㄧ敾鑳介『鍒╂墽琛?             */
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mLoadMoreListener != null) {
                        mLoadMoreListener.onLoadMore(SmartRefreshLayout.this);
                    } else if (mOnMultiPurposeListener == null) {
                        finishLoadMore(2000);//濡傛灉娌℃湁浠讳綍鍔犺浇鐩戝惉鍣紝涓ょ涔嬪悗鑷姩鍏抽棴
                    }
                    final OnLoadMoreListener listener = mOnMultiPurposeListener;
                    if (listener != null) {
                        listener.onLoadMore(SmartRefreshLayout.this);
                    }
                }
            }, mReboundDuration);
        }
    }

    //</editor-fold>

    //<editor-fold desc="甯冨眬鍙傛暟 LayoutParams">
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(MATCH_PARENT, MATCH_PARENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        final View thisView = this;
        return new LayoutParams(thisView.getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout_Layout);
            backgroundColor = ta.getColor(R.styleable.SmartRefreshLayout_Layout_layout_srlBackgroundColor, backgroundColor);
            if (ta.hasValue(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle)) {
                spinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.SmartRefreshLayout_Layout_layout_srlSpinnerStyle, SpinnerStyle.Translate.ordinal())];
            }
            ta.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public int backgroundColor = 0;
        public SpinnerStyle spinnerStyle = null;
    }
    //</editor-fold>

    //<editor-fold desc="宓屽婊氬姩 NestedScrolling">

    //<editor-fold desc="NestedScrollingParent">

    @Override
    public int getNestedScrollAxes() {
        return mNestedParent.getNestedScrollAxes();
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        final View thisView = this;
        boolean accepted = thisView.isEnabled() && isNestedScrollingEnabled() && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
        accepted = accepted && (mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableRefresh) || isEnableRefreshOrLoadMore(mEnableLoadMore));
        return accepted;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedParent.onNestedScrollAccepted(child, target, axes);
        // Dispatch up to the nested parent
        mNestedChild.startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);

        mTotalUnconsumed = mSpinner;//0;
        mNestedInProgress = true;

        interceptAnimatorByAction(MotionEvent.ACTION_DOWN);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        int consumedY = 0;

        if (dy * mTotalUnconsumed > 0) {
            if (Math.abs(dy) > Math.abs(mTotalUnconsumed)) {
                consumedY = mTotalUnconsumed;
                mTotalUnconsumed = 0;
            } else {
                consumedY = dy;
                mTotalUnconsumed -= dy;
            }
            moveSpinnerInfinitely(mTotalUnconsumed);
            if (mViceState.isOpening || mViceState == RefreshState.None) {
                if (mSpinner > 0) {
                    mKernel.setState(RefreshState.PullDownToRefresh);
                } else {
                    mKernel.setState(RefreshState.PullUpToLoad);
                }
            }
        } else if (dy > 0 && mFooterLocked) {
            consumedY = dy;
            mTotalUnconsumed -= dy;
            moveSpinnerInfinitely(mTotalUnconsumed);
        }

        // Now let our nested parent consume the leftovers
        mNestedChild.dispatchNestedPreScroll(dx, dy - consumedY, consumed, null);
        consumed[1] += consumedY;

    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        // Dispatch up to the nested parent first
        mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow);

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        final int dy = dyUnconsumed + mParentOffsetInWindow[1];
        if (dy != 0 && (mEnableOverScrollDrag || (dy < 0 && isEnableRefreshOrLoadMore(mEnableRefresh)) || (dy > 0 && isEnableRefreshOrLoadMore(mEnableLoadMore)))) {
            if (mViceState == RefreshState.None) {
                mKernel.setState(dy > 0 ? RefreshState.PullUpToLoad : RefreshState.PullDownToRefresh);
            }
            moveSpinnerInfinitely(mTotalUnconsumed -= dy);
        }

        if (mFooterLocked && dyConsumed < 0) {
            mFooterLocked = false;//鍐呭鍚戜笅婊氬姩鏃?瑙ｉ攣Footer 鐨勯攣瀹?        }

    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return mFooterLocked && velocityY > 0 || startFlingIfNeed(-velocityY) || mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target) {
        mNestedParent.onStopNestedScroll(target);
        mNestedInProgress = false;
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        mTotalUnconsumed = 0;
        overSpinner();
        // Dispatch up our nested parent
        mNestedChild.stopNestedScroll();
    }
    //</editor-fold>

    //<editor-fold desc="NestedScrollingChild">
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
//        mManualNestedScrolling = true;
        mNestedChild.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedChild.isNestedScrollingEnabled();
    }

//    @Override
//    public boolean canScrollVertically(int direction) {
//        View target = mRefreshContent.getScrollableView();
//        if (direction < 0) {
//            return mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableRefresh) || ScrollBoundaryUtil.canScrollUp(target);
//        } else if (direction > 0) {
//            return mEnableOverScrollDrag || isEnableRefreshOrLoadMore(mEnableLoadMore) || ScrollBoundaryUtil.canScrollDown(target);
//        }
//        return true;
//    }

    //    @Override
//    @Deprecated
//    public boolean startNestedScroll(int axes) {
//        return mNestedChild.startNestedScroll(axes);
//    }
//
//    @Override
//    @Deprecated
//    public void stopNestedScroll() {
//        mNestedChild.stopNestedScroll();
//    }
//
//    @Override
//    @Deprecated
//    public boolean hasNestedScrollingParent() {
//        return mNestedChild.hasNestedScrollingParent();
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
//                                        int dyUnconsumed, int[] offsetInWindow) {
//        return mNestedChild.dispatchNestedScroll(dxConsumed, dyConsumed,
//                dxUnconsumed, dyUnconsumed, offsetInWindow);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
//        return mNestedChild.dispatchNestedPreScroll(
//                dx, dy, consumed, offsetInWindow);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        return mNestedChild.dispatchNestedFling(velocityX, velocityY, consumed);
//    }
//
//    @Override
//    @Deprecated
//    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
//        return mNestedChild.dispatchNestedPreFling(velocityX, velocityY);
//    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="寮€鏀炬帴鍙?open interface">

    /**
     * Set the Footer's height.
     * 璁剧疆 Footer 鐨勯珮搴?     * @param heightDp Density-independent Pixels 铏氭嫙鍍忕礌锛坧x闇€瑕佽皟鐢╬x2dp杞崲锛?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setFooterHeight(float heightDp) {
        if (mFooterHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mFooterHeight = dp2px(heightDp);
            mFooterHeightStatus = DimensionStatus.CodeExactUnNotify;
            if (mRefreshFooter != null) {
                mRefreshFooter.getView().requestLayout();
            }
        }
        return this;
    }

    /**
     * Set the Header's height.
     * 璁剧疆 Header 楂樺害
     * @param heightDp Density-independent Pixels 铏氭嫙鍍忕礌锛坧x闇€瑕佽皟鐢╬x2dp杞崲锛?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setHeaderHeight(float heightDp) {
        if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mHeaderHeight = dp2px(heightDp);
            mHeaderHeightStatus = DimensionStatus.CodeExactUnNotify;
            if (mRefreshHeader != null) {
                mRefreshHeader.getView().requestLayout();
            }
        }
        return this;
    }

    /**
     * Set the Header's start offset锛坰ee srlHeaderInsetStart in the RepastPracticeActivity XML in demo-app for the practical application锛?
     * 璁剧疆 Header 鐨勮捣濮嬪亸绉婚噺锛堜娇鐢ㄦ柟娉曞弬鑰?demo-app 涓殑 RepastPracticeActivity xml 涓殑 srlHeaderInsetStart锛?     * @param insetDp Density-independent Pixels 铏氭嫙鍍忕礌锛坧x闇€瑕佽皟鐢╬x2dp杞崲锛?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setHeaderInsetStart(float insetDp) {
        mHeaderInsetStart = dp2px(insetDp);
        return this;
    }

    /**
     * Set the Header's start offset.
     * 璁剧疆 Footer 璧峰鍋忕Щ閲忥紙鐢ㄦ埛鍜?setHeaderInsetStart 涓€鏍凤級
     * @see RefreshLayout#setHeaderInsetStart(float)
     * @param insetDp Density-independent Pixels 铏氭嫙鍍忕礌锛坧x闇€瑕佽皟鐢╬x2dp杞崲锛?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setFooterInsetStart(float insetDp) {
        mFooterInsetStart = dp2px(insetDp);
        return this;
    }

    /**
     * Set the damping effect.
     * 鏄剧ず鎷栧姩楂樺害/鐪熷疄鎷栧姩楂樺害 姣旂巼锛堥粯璁?.5锛岄樆灏兼晥鏋滐級
     * @param rate ratio = (The drag height of the view)/(The actual drag height of the finger)
     *             姣旂巼 = 瑙嗗浘鎷栧姩楂樺害 / 鎵嬫寚鎷栧姩楂樺害
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setDragRate(float rate) {
        this.mDragRate = rate;
        return this;
    }

    /**
     * Set the ratio of the maximum height to drag header.
     * 璁剧疆涓嬫媺鏈€澶ч珮搴﹀拰Header楂樺害鐨勬瘮鐜囷紙灏嗕細褰卞搷鍙互涓嬫媺鐨勬渶澶ч珮搴︼級
     * @param rate ratio = (the maximum height to drag header)/(the height of header)
     *             姣旂巼 = 涓嬫媺鏈€澶ч珮搴?/ Header鐨勯珮搴?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setHeaderMaxDragRate(float rate) {
        this.mHeaderMaxDragRate = rate;
        if (mRefreshHeader != null && mHandler != null) {
            mRefreshHeader.onInitialized(mKernel, mHeaderHeight,  (int) (mHeaderMaxDragRate * mHeaderHeight));
        } else {
            mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
        }
        return this;
    }

    /**
     * Set the ratio of the maximum height to drag footer.
     * 璁剧疆涓婃媺鏈€澶ч珮搴﹀拰Footer楂樺害鐨勬瘮鐜囷紙灏嗕細褰卞搷鍙互涓婃媺鐨勬渶澶ч珮搴︼級
     * @param rate ratio = (the maximum height to drag footer)/(the height of footer)
     *             姣旂巼 = 涓嬫媺鏈€澶ч珮搴?/ Footer鐨勯珮搴?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setFooterMaxDragRate(float rate) {
        this.mFooterMaxDragRate = rate;
        if (mRefreshFooter != null && mHandler != null) {
            mRefreshFooter.onInitialized(mKernel, mFooterHeight, (int)(mFooterHeight * mFooterMaxDragRate));
        } else {
            mFooterHeightStatus = mFooterHeightStatus.unNotify();
        }
        return this;
    }

    /**
     * Set the ratio at which the refresh is triggered.
     * 璁剧疆 瑙﹀彂鍒锋柊璺濈 涓?HeaderHeight 鐨勬瘮鐜?     * @param rate 瑙﹀彂鍒锋柊璺濈 涓?HeaderHeight 鐨勬瘮鐜?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setHeaderTriggerRate(float rate) {
        this.mHeaderTriggerRate = rate;
        return this;
    }

    /**
     * Set the ratio at which the load more is triggered.
     * 璁剧疆 瑙﹀彂鍔犺浇璺濈 涓?FooterHeight 鐨勬瘮鐜?     * @param rate 瑙﹀彂鍔犺浇璺濈 涓?FooterHeight 鐨勬瘮鐜?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setFooterTriggerRate(float rate) {
        this.mFooterTriggerRate = rate;
        return this;
    }

    /**
     * Set the rebound interpolator.
     * 璁剧疆鍥炲脊鏄剧ず鎻掑€煎櫒 [鏀炬墜鏃跺洖寮瑰姩鐢?缁撴潫鏃舵敹缂╁姩鐢籡
     * @param interpolator 鍔ㄧ敾鎻掑€煎櫒
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setReboundInterpolator(@NonNull Interpolator interpolator) {
        this.mReboundInterpolator = interpolator;
        return this;
    }

    /**
     * Set the duration of the rebound animation.
     * 璁剧疆鍥炲脊鍔ㄧ敾鏃堕暱 [鏀炬墜鏃跺洖寮瑰姩鐢?缁撴潫鏃舵敹缂╁姩鐢籡
     * @param duration 鏃堕暱
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setReboundDuration(int duration) {
        this.mReboundDuration = duration;
        return this;
    }

    /**
     * Set whether to enable pull-up loading more (enabled by default).
     * 璁剧疆鏄惁鍚敤涓婃媺鍔犺浇鏇村锛堥粯璁ゅ惎鐢級
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableLoadMore(boolean enabled) {
        this.mManualLoadMore = true;
        this.mEnableLoadMore = enabled;
        return this;
    }

    /**
     * 鏄惁鍚敤涓嬫媺鍒锋柊锛堥粯璁ゅ惎鐢級
     * @param enabled 鏄惁鍚敤
     * @return SmartRefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableRefresh(boolean enabled) {
        this.mEnableRefresh = enabled;
        return this;
    }

    /**
     * Whether to enable pull-down refresh (enabled by default).
     * 鏄惁鍚敤涓嬫媺鍒锋柊锛堥粯璁ゅ惎鐢級
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableHeaderTranslationContent(boolean enabled) {
        this.mEnableHeaderTranslationContent = enabled;
        this.mManualHeaderTranslationContent = true;
        return this;
    }

    /**
     * Set whether to pull up the content while pulling up the header.
     * 璁剧疆鏄惁鍚湪涓婃媺 Footer 鐨勫悓鏃朵笂鎷夊唴瀹?     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableFooterTranslationContent(boolean enabled) {
        this.mEnableFooterTranslationContent = enabled;
        this.mManualFooterTranslationContent = true;
        return this;
    }

    /**
     * Sets whether to listen for the list to trigger a load event when scrolling to the bottom (default true).
     * 璁剧疆鏄惁鐩戝惉鍒楄〃鍦ㄦ粴鍔ㄥ埌搴曢儴鏃惰Е鍙戝姞杞戒簨浠讹紙榛樿true锛?     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableAutoLoadMore(boolean enabled) {
        this.mEnableAutoLoadMore = enabled;
        return this;
    }

    /**
     * Set whether to enable cross-border rebound function.
     * 璁剧疆鏄惁鍚敤瓒婄晫鍥炲脊
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableOverScrollBounce(boolean enabled) {
        this.mEnableOverScrollBounce = enabled;
        return this;
    }

    /**
     * Set whether to enable the pure scroll mode.
     * 璁剧疆鏄惁寮€鍚函婊氬姩妯″紡
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnablePureScrollMode(boolean enabled) {
        this.mEnablePureScrollMode = enabled;
        return this;
    }

    /**
     * Set whether to scroll the content to display new data after loading more complete.
     * 璁剧疆鏄惁鍦ㄥ姞杞芥洿澶氬畬鎴愪箣鍚庢粴鍔ㄥ唴瀹规樉绀烘柊鏁版嵁
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableScrollContentWhenLoaded(boolean enabled) {
        this.mEnableScrollContentWhenLoaded = enabled;
        return this;
    }

    /**
     * Set whether to scroll the content to display new data after the refresh is complete.
     * 鏄惁鍦ㄥ埛鏂板畬鎴愪箣鍚庢粴鍔ㄥ唴瀹规樉绀烘柊鏁版嵁
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableScrollContentWhenRefreshed(boolean enabled) {
        this.mEnableScrollContentWhenRefreshed = enabled;
        return this;
    }

    /**
     * Set whether to pull up and load more when the content is not full of one page.
     * 璁剧疆鍦ㄥ唴瀹逛笉婊′竴椤电殑鏃跺€欙紝鏄惁鍙互涓婃媺鍔犺浇鏇村
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableLoadMoreWhenContentNotFull(boolean enabled) {
        this.mEnableLoadMoreWhenContentNotFull = enabled;
        if (mRefreshContent != null) {
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(enabled);
        }
        return this;
    }

    /**
     * Set whether to enable cross-border drag (imitation iphone effect).
     * 璁剧疆鏄惁鍚敤瓒婄晫鎷栧姩锛堜豢鑻规灉鏁堟灉锛?     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableOverScrollDrag(boolean enabled) {
        this.mEnableOverScrollDrag = enabled;
        return this;
    }

    /**
     * Set whether or not Footer follows the content after there is no more data.
     * 璁剧疆鏄惁鍦ㄦ病鏈夋洿澶氭暟鎹箣鍚?Footer 璺熼殢鍐呭
     * @deprecated use {@link RefreshLayout#setEnableFooterFollowWhenNoMoreData(boolean)}
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    @Deprecated
    public SmartRefreshLayout setEnableFooterFollowWhenLoadFinished(boolean enabled) {
        this.mEnableFooterFollowWhenNoMoreData = enabled;
        return this;
    }

    /**
     * Set whether or not Footer follows the content after there is no more data.
     * 璁剧疆鏄惁鍦ㄦ病鏈夋洿澶氭暟鎹箣鍚?Footer 璺熼殢鍐呭
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableFooterFollowWhenNoMoreData(boolean enabled) {
        this.mEnableFooterFollowWhenNoMoreData = enabled;
        return this;
    }

    /**
     * Set whether to clip header when the Header is in the FixedBehind state.
     * 璁剧疆鏄惁鍦ㄥ綋 Header 澶勪簬 FixedBehind 鐘舵€佺殑鏃跺€欏壀瑁侀伄鎸?Header
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableClipHeaderWhenFixedBehind(boolean enabled) {
        this.mEnableClipHeaderWhenFixedBehind = enabled;
        return this;
    }

    /**
     * Set whether to clip footer when the Footer is in the FixedBehind state.
     * 璁剧疆鏄惁鍦ㄥ綋 Footer 澶勪簬 FixedBehind 鐘舵€佺殑鏃跺€欏壀瑁侀伄鎸?Footer
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setEnableClipFooterWhenFixedBehind(boolean enabled) {
        this.mEnableClipFooterWhenFixedBehind = enabled;
        return this;
    }

    /**
     * Setting whether nesting scrolling is enabled (default off + smart on).
     * 璁剧疆鏄細鍚﹀惎鐢ㄥ祵濂楁粴鍔ㄥ姛鑳斤紙榛樿鍏抽棴+鏅鸿兘寮€鍚級
     * @param enabled 鏄惁鍚敤
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout setEnableNestedScroll(boolean enabled) {
        setNestedScrollingEnabled(enabled);
        return this;
    }

//    /**
//     * Sets whether to enable pure nested scrolling mode
//     * Smart scrolling supports both [nested scrolling] and [traditional scrolling] modes
//     * With nested scrolling enabled, traditional mode also works when necessary
//     * However, sometimes interference and conflict can occur. If you find this conflict, you can try to turn on [pure nested scrolling] mode and [traditional mode] off
//     * 璁剧疆鏄惁寮€鍚€愮函宓屽婊氬姩銆戞ā寮?//     * Smart 鐨勬粴鍔ㄦ敮鎸?銆愬祵濂楁粴鍔ㄣ€?+ 銆愪紶缁熸粴鍔ㄣ€?涓ょ妯″紡
//     * 鍦ㄥ紑鍚?銆愬祵濂楁粴鍔ㄣ€?鐨勬儏鍐典笅锛屻€愪紶缁熸ā寮忋€戜篃浼氬湪蹇呰鐨勬椂鍊欏彂鎸ヤ綔鐢?//     * 浣嗘槸鏈夋椂鍊欎篃浼氬彂鐢熷共鎵板拰鍐茬獊锛屽鏋滄偍鍙戠幇浜嗚繖涓啿绐侊紝鍙互灏濊瘯寮€鍚?銆愮函宓屽婊氬姩銆戞ā寮忥紝銆愪紶缁熸ā寮忋€戝叧闂?//     * @param enabled 鏄惁鍚敤
//     * @return RefreshLayout
//     */
//    @Override
//    public RefreshLayout setEnableNestedScrollOnly(boolean enabled) {
//        if (enabled && !mNestedChild.isNestedScrollingEnabled()) {
//            mNestedChild.setNestedScrollingEnabled(true);
//        }
//        mEnableNestedScrollingOnly = enabled;
//        return this;
//    }

    /**
     * Set whether to enable the action content view when refreshing.
     * 璁剧疆鏄惁寮€鍚湪鍒锋柊鏃跺€欑姝㈡搷浣滃唴瀹硅鍥?     * @param disable 鏄惁绂佹
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setDisableContentWhenRefresh(boolean disable) {
        this.mDisableContentWhenRefresh = disable;
        return this;
    }

    /**
     * Set whether to enable the action content view when loading.
     * 璁剧疆鏄惁寮€鍚湪鍔犺浇鏃跺€欑姝㈡搷浣滃唴瀹硅鍥?     * @param disable 鏄惁绂佹
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setDisableContentWhenLoading(boolean disable) {
        this.mDisableContentWhenLoading = disable;
        return this;
    }

    /**
     * Set the header of RefreshLayout.
     * 璁剧疆鎸囧畾鐨?Header
     * @param header RefreshHeader 鍒锋柊澶?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshHeader(@NonNull RefreshHeader header) {
        return setRefreshHeader(header, MATCH_PARENT, WRAP_CONTENT);
    }

    /**
     * Set the header of RefreshLayout.
     * 璁剧疆鎸囧畾鐨?Header
     * @param header RefreshHeader 鍒锋柊澶?     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              瀹藉害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               楂樺害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshHeader(@NonNull RefreshHeader header, int width, int height) {
        if (mRefreshHeader != null) {
            super.removeView(mRefreshHeader.getView());
        }
        this.mRefreshHeader = header;
        this.mHeaderBackgroundColor = 0;
        this.mHeaderNeedTouchEventWhenRefreshing = false;
        this.mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
        if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
            super.addView(mRefreshHeader.getView(), 0, new LayoutParams(width, height));
        } else {
            super.addView(mRefreshHeader.getView(), width, height);
        }
        return this;
    }

    /**
     * Set the footer of RefreshLayout.
     * 璁剧疆鎸囧畾鐨?Footer
     * @param footer RefreshFooter 鍒锋柊灏惧反
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshFooter(@NonNull RefreshFooter footer) {
        return setRefreshFooter(footer, MATCH_PARENT, WRAP_CONTENT);
    }

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
    @Override
    public SmartRefreshLayout setRefreshFooter(@NonNull RefreshFooter footer, int width, int height) {
        if (mRefreshFooter != null) {
            super.removeView(mRefreshFooter.getView());
        }
        this.mRefreshFooter = footer;
        this.mFooterBackgroundColor = 0;
        this.mFooterNeedTouchEventWhenLoading = false;
        this.mFooterHeightStatus = mFooterHeightStatus.unNotify();
        this.mEnableLoadMore = !mManualLoadMore || mEnableLoadMore;
        if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
            super.addView(mRefreshFooter.getView(), 0, new LayoutParams(width, height));
        } else {
            super.addView(mRefreshFooter.getView(), width, height);
        }
        return this;
    }

    /**
     * Set the content of RefreshLayout锛圫uitable for non-XML pages, not suitable for replacing empty layouts锛夈€?     * 璁剧疆鎸囧畾鐨?Content锛堥€傜敤浜庨潪XML椤甸潰锛屼笉閫傚悎鐢ㄦ浛鎹㈢┖甯冨眬锛?     * @param content View 鍐呭瑙嗗浘
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshContent(@NonNull View content) {
        return setRefreshContent(content, MATCH_PARENT, MATCH_PARENT);
    }

    /**
     * Set the content of RefreshLayout锛圫uitable for non-XML pages, not suitable for replacing empty layouts锛?
     * 璁剧疆鎸囧畾鐨?Content锛堥€傜敤浜庨潪XML椤甸潰锛屼笉閫傚悎鐢ㄦ浛鎹㈢┖甯冨眬锛?     * @param content View 鍐呭瑙嗗浘
     * @param width the width in px, can use MATCH_PARENT and WRAP_CONTENT.
     *              瀹藉害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @param height the height in px, can use MATCH_PARENT and WRAP_CONTENT.
     *               楂樺害 鍙互浣跨敤 MATCH_PARENT, WRAP_CONTENT
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setRefreshContent(@NonNull View content, int width, int height) {
        final View thisView = this;
        if (mRefreshContent != null) {
            super.removeView(mRefreshContent.getView());
        }
        super.addView(content, 0, new LayoutParams(width, height));
        if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
            super.bringChildToFront(content);
            if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() != SpinnerStyle.FixedBehind) {
                super.bringChildToFront(mRefreshFooter.getView());
            }
        } else if (mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
            super.bringChildToFront(content);
            if (mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind) {
                super.bringChildToFront(mRefreshHeader.getView());
            }
        }
        mRefreshContent = new RefreshContentWrapper(content);
        if (mHandler != null) {
            View fixedHeaderView = mFixedHeaderViewId > 0 ? thisView.findViewById(mFixedHeaderViewId) : null;
            View fixedFooterView = mFixedFooterViewId > 0 ? thisView.findViewById(mFixedFooterViewId) : null;

            mRefreshContent.setScrollBoundaryDecider(mScrollBoundaryDecider);
            mRefreshContent.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull);
            mRefreshContent.setUpComponent(mKernel, fixedHeaderView, fixedFooterView);
        }
        return this;
    }

    /**
     * Get footer of RefreshLayout
     * 鑾峰彇褰撳墠 Footer
     * @return RefreshLayout
     */
    @Nullable
    @Override
    public RefreshFooter getRefreshFooter() {
        return mRefreshFooter instanceof RefreshFooter ? (RefreshFooter) mRefreshFooter : null;
    }

    /**
     * Get header of RefreshLayout
     * 鑾峰彇褰撳墠 Header
     * @return RefreshLayout
     */
    @Nullable
    @Override
    public RefreshHeader getRefreshHeader() {
        return mRefreshHeader instanceof RefreshHeader ? (RefreshHeader) mRefreshHeader : null;
    }

    /**
     * Get the current state of RefreshLayout
     * 鑾峰彇褰撳墠鐘舵€?     * @return RefreshLayout
     */
    @Override
    public RefreshState getState() {
        return mState;
    }

    /**
     * Get the ViewGroup of RefreshLayout
     * 鑾峰彇瀹炰綋甯冨眬瑙嗗浘
     * @return ViewGroup
     */
    @Override
    public SmartRefreshLayout getLayout() {
        return this;
    }

    /**
     * Set refresh listener separately.
     * 鍗曠嫭璁剧疆鍒锋柊鐩戝惉鍣?     * @param listener OnRefreshListener 鍒锋柊鐩戝惉鍣?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
        return this;
    }

    /**
     * Set load more listener separately.
     * 鍗曠嫭璁剧疆鍔犺浇鐩戝惉鍣?     * @param listener OnLoadMoreListener 鍔犺浇鐩戝惉鍣?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mLoadMoreListener = listener;
        this.mEnableLoadMore = mEnableLoadMore || (!mManualLoadMore && listener != null);
        return this;
    }

    /**
     * Set refresh and load listeners at the same time.
     * 鍚屾椂璁剧疆鍒锋柊鍜屽姞杞界洃鍚櫒
     * @param listener OnRefreshLoadMoreListener 鍒锋柊鍔犺浇鐩戝惉鍣?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener) {
        this.mRefreshListener = listener;
        this.mLoadMoreListener = listener;
        this.mEnableLoadMore = mEnableLoadMore || (!mManualLoadMore && listener != null);
        return this;
    }

    /**
     * Set up a multi-function listener.
     * Recommended {@link com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener}
     * 璁剧疆澶氬姛鑳界洃鍚櫒
     * 寤鸿浣跨敤 {@link com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener}
     * @param listener OnMultiPurposeListener 澶氬姛鑳界洃鍚櫒
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener) {
        this.mOnMultiPurposeListener = listener;
        return this;
    }

    /**
     * Set theme color int (primaryColor and accentColor).
     * 璁剧疆涓婚棰滆壊
     * @param primaryColors ColorInt 涓婚棰滆壊
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setPrimaryColors(@ColorInt int... primaryColors) {
        if (mRefreshHeader != null) {
            mRefreshHeader.setPrimaryColors(primaryColors);
        }
        if (mRefreshFooter != null) {
            mRefreshFooter.setPrimaryColors(primaryColors);
        }
        mPrimaryColors = primaryColors;
        return this;
    }

    /**
     * Set theme color id (primaryColor and accentColor).
     * 璁剧疆涓婚棰滆壊
     * @param primaryColorId ColorRes 涓婚棰滆壊ID
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setPrimaryColorsId(@ColorRes int... primaryColorId) {
        final View thisView = this;
        final int[] colors = new int[primaryColorId.length];
        for (int i = 0; i < primaryColorId.length; i++) {
            colors[i] = getColor(thisView.getContext(), primaryColorId[i]);
        }
        setPrimaryColors(colors);
        return this;
    }

    /**
     * Set the scroll boundary Decider, Can customize when you can refresh.
     * Recommended {@link com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter}
     * 璁剧疆婊氬姩杈圭晫鍒ゆ柇鍣?     * 寤鸿浣跨敤 {@link com.scwang.smartrefresh.layout.impl.ScrollBoundaryDeciderAdapter}
     * @param boundary ScrollBoundaryDecider 鍒ゆ柇鍣?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider boundary) {
        mScrollBoundaryDecider = boundary;
        if (mRefreshContent != null) {
            mRefreshContent.setScrollBoundaryDecider(boundary);
        }
        return this;
    }

    /**
     * Restore the original state after finishLoadMoreWithNoMoreData.
     * 鎭㈠娌℃湁鏇村鏁版嵁鐨勫師濮嬬姸鎬?     * @deprecated use {@link RefreshLayout#resetNoMoreData()}
     * @param noMoreData 鏄惁鏈夋洿澶氭暟鎹?     * @return RefreshLayout
     */
    @Override
    @Deprecated
    public SmartRefreshLayout setNoMoreData(boolean noMoreData) {
        if (mState == RefreshState.Loading && noMoreData) {
            finishLoadMore();
        }
        mFooterNoMoreData = noMoreData;
        if (mRefreshFooter instanceof RefreshFooter && !((RefreshFooter)mRefreshFooter).setNoMoreData(noMoreData)) {
            System.out.println("Footer:" + mRefreshFooter + " NoMoreData is not supported.(涓嶆敮鎸丯oMoreData锛岃浣跨敤ClassicsFooter鎴栬€呰嚜瀹氫箟)");
        }
        return this;
    }

    /**
     * Restore the original state after finishLoadMoreWithNoMoreData.
     * 鎭㈠娌℃湁鏇村鏁版嵁鐨勫師濮嬬姸鎬?     * @return RefreshLayout
     */
    @Override
    public RefreshLayout resetNoMoreData() {
        mFooterNoMoreData = false;
        if (mRefreshFooter instanceof RefreshFooter && !((RefreshFooter)mRefreshFooter).setNoMoreData(false)) {
            System.out.println("Footer:" + mRefreshFooter + " NoMoreData is not supported.(涓嶆敮鎸丯oMoreData锛岃浣跨敤ClassicsFooter鎴栬€呰嚜瀹氫箟)");
        }
        return this;
    }

    /**
     * finish refresh.
     * 瀹屾垚鍒锋柊
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishRefresh(Math.min(Math.max(0, 300 - (int) passTime), 300));//淇濊瘉鍒锋柊鍔ㄧ敾鏈?00姣鐨勬椂闂?    }

    /**
     * finish load more.
     * 瀹屾垚鍔犺浇
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore(Math.min(Math.max(0, 300 - (int) passTime), 300));//淇濊瘉鍔犺浇鍔ㄧ敾鏈?00姣鐨勬椂闂?    }

    /**
     * finish refresh.
     * 瀹屾垚鍒锋柊
     * @param delayed 寮€濮嬪欢鏃?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh(int delayed) {
        return finishRefresh(delayed, true);
    }

    /**
     * finish refresh.
     * 瀹屾垚鍔犺浇
     * @param success 鏁版嵁鏄惁鎴愬姛鍒锋柊 锛堜細褰卞搷鍒颁笂娆℃洿鏂版椂闂寸殑鏀瑰彉锛?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh(boolean success) {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishRefresh(success ? Math.min(Math.max(0, 300 - (int) passTime), 300) : 0, success);//淇濊瘉鍔犺浇鍔ㄧ敾鏈?00姣鐨勬椂闂?    }

    /**
     * finish refresh.
     * 瀹屾垚鍒锋柊
     * @param delayed 寮€濮嬪欢鏃?     * @param success 鏁版嵁鏄惁鎴愬姛鍒锋柊 锛堜細褰卞搷鍒颁笂娆℃洿鏂版椂闂寸殑鏀瑰彉锛?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishRefresh(int delayed, final boolean success) {
        if (mState == RefreshState.Refreshing && success) {
            resetNoMoreData();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mState == RefreshState.Refreshing && mRefreshHeader != null && mRefreshContent != null) {
                    notifyStateChanged(RefreshState.RefreshFinish);
                    int startDelay = mRefreshHeader.onFinish(SmartRefreshLayout.this, success);
                    if (mOnMultiPurposeListener != null && mRefreshHeader instanceof RefreshHeader) {
                        mOnMultiPurposeListener.onHeaderFinish((RefreshHeader) mRefreshHeader, success);
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        if (mIsBeingDragged || mNestedInProgress) {
                            if (mIsBeingDragged) {
                                mTouchY = mLastTouchY;
                                mTouchSpinner = 0;
                                mIsBeingDragged = false;
                            }
                            long time = System.currentTimeMillis();
                            SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, mLastTouchX, mLastTouchY + mSpinner - mTouchSlop*2, 0));
                            SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_MOVE, mLastTouchX, mLastTouchY + mSpinner, 0));
                            if (mNestedInProgress) {
                                mTotalUnconsumed = 0;
                            }
                        }
                        if (mSpinner > 0) {
                            AnimatorUpdateListener updateListener = null;
                            ValueAnimator valueAnimator = animSpinner(0, startDelay, mReboundInterpolator, mReboundDuration);
                            if (mEnableScrollContentWhenRefreshed) {
                                updateListener = mRefreshContent.scrollContentWhenFinished(mSpinner);
                            }
                            if (valueAnimator != null && updateListener != null) {
                                valueAnimator.addUpdateListener(updateListener);
                            }
                        } else if (mSpinner < 0) {
                            animSpinner(0, startDelay, mReboundInterpolator, mReboundDuration);
                        } else {
                            mKernel.moveSpinner(0, false);
                            resetStatus();
                        }
                    }
                }
            }
        }, delayed <= 0 ? 1 : delayed);
        return this;
    }

    /**
     * finish load more.
     * 瀹屾垚鍔犺浇
     * @param delayed 寮€濮嬪欢鏃?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore(int delayed) {
        return finishLoadMore(delayed, true, false);
    }

    /**
     * finish load more.
     * 瀹屾垚鍔犺浇
     * @param success 鏁版嵁鏄惁鎴愬姛
     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore(boolean success) {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore(success ? Math.min(Math.max(0, 300 - (int) passTime), 300) : 0, success, false);
    }

    /**
     * finish load more.
     * 瀹屾垚鍔犺浇
     * @param delayed 寮€濮嬪欢鏃?     * @param success 鏁版嵁鏄惁鎴愬姛
     * @param noMoreData 鏄惁鏈夋洿澶氭暟鎹?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMore(int delayed, final boolean success, final boolean noMoreData) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mState == RefreshState.Loading && mRefreshFooter != null && mRefreshContent != null) {
                    notifyStateChanged(RefreshState.LoadFinish);
                    final int startDelay = mRefreshFooter.onFinish(SmartRefreshLayout.this, success);
                    if (mOnMultiPurposeListener != null && mRefreshFooter instanceof RefreshFooter) {
                        mOnMultiPurposeListener.onFooterFinish((RefreshFooter) mRefreshFooter, success);
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        //璁＄畻甯冨眬灏嗚绉诲姩鐨勫亸绉婚噺
                        final boolean needHoldFooter = noMoreData && mEnableFooterFollowWhenNoMoreData && mSpinner < 0 && mRefreshContent.canLoadMore();
                        final int offset = mSpinner - (needHoldFooter ? Math.max(mSpinner,-mFooterHeight) : 0);
                        //濡傛灉姝ｅ湪鎷栧姩鐨勮瘽锛屽亸绉诲垵濮嬬偣鍑讳簨浠?                        if (mIsBeingDragged || mNestedInProgress) {
                            if (mIsBeingDragged) {
                                mTouchY = mLastTouchY;
                                mIsBeingDragged = false;
                                mTouchSpinner = mSpinner - offset;
                            }
                            final long time = System.currentTimeMillis();
                            SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, mLastTouchX, mLastTouchY + offset + mTouchSlop * 2, 0));
                            SmartRefreshLayout.super.dispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_MOVE, mLastTouchX, mLastTouchY + offset, 0));
                            if (mNestedInProgress) {
                                mTotalUnconsumed = 0;
                            }
                        }
                        //鍑嗗锛氬亸绉诲苟缁撴潫鐘舵€?                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AnimatorUpdateListener updateListener = null;
                                if (mEnableScrollContentWhenLoaded && offset < 0) {
                                    updateListener = mRefreshContent.scrollContentWhenFinished(mSpinner);
                                }
                                if (updateListener != null) {
                                    updateListener.onAnimationUpdate(ValueAnimator.ofInt(0, 0));
                                }
                                ValueAnimator animator = null;
                                AnimatorListenerAdapter listenerAdapter = new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        super.onAnimationEnd(animation);
                                    }
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mFooterLocked = false;
                                        if (noMoreData) {
                                            setNoMoreData(true);
                                        }
                                        if (mState == RefreshState.LoadFinish) {
                                            notifyStateChanged(RefreshState.None);
                                        }
                                    }
                                };
                                if (mSpinner > 0) {
                                    animator = mKernel.animSpinner(0);
                                } else if (updateListener != null || mSpinner == 0) {
                                    if (reboundAnimator != null) {
                                        reboundAnimator.cancel();
                                        reboundAnimator = null;
                                    }
                                    mKernel.moveSpinner(0, false);
                                    resetStatus();
                                } else {
                                    if (noMoreData && mEnableFooterFollowWhenNoMoreData) {
                                        if (mSpinner >= -mFooterHeight) {
                                            notifyStateChanged(RefreshState.None);
                                        } else {
                                            animator = mKernel.animSpinner(-mFooterHeight);
                                        }
                                    } else {
                                        animator = mKernel.animSpinner(0);
                                    }
                                }
                                if (animator != null) {
                                    animator.addListener(listenerAdapter);
                                } else {
                                    listenerAdapter.onAnimationEnd(null);
                                }
                            }
                        }, mSpinner < 0 ? startDelay : 0);
                    }
                } else {
                    if (noMoreData) {
                        setNoMoreData(true);
                    }
                }
            }
        }, delayed <= 0 ? 1 : delayed);
        return this;
    }

    /**
     * finish load more with no more data.
     * 瀹屾垚鍔犺浇骞舵爣璁版病鏈夋洿澶氭暟鎹?     * @return RefreshLayout
     */
    @Override
    public SmartRefreshLayout finishLoadMoreWithNoMoreData() {
        long passTime = System.currentTimeMillis() - mLastOpenTime;
        return finishLoadMore(Math.min(Math.max(0, 300 - (int) passTime), 300), true, true);
    }

    /**
     * Close the Header or Footer, can't replace finishRefresh and finishLoadMore.
     * 鍏抽棴 Header 鎴栬€?Footer
     * @return RefreshLayout
     */
    @Override
    public RefreshLayout closeHeaderOrFooter() {
        if (mState == RefreshState.Refreshing) {
            finishRefresh();
        } else if (mState == RefreshState.Loading) {
            finishLoadMore();
        } else if (mSpinner != 0) {
            animSpinner(0, 0, mReboundInterpolator, mReboundDuration);
        }
        return this;
    }

    /**
     * Display refresh animation and trigger refresh event.
     * 鏄剧ず鍒锋柊鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Override
    public boolean autoRefresh() {
        return autoRefresh(mHandler == null ? 400 : 0, mReboundDuration, 1f * ((mHeaderMaxDragRate/2 + 0.5f) * mHeaderHeight) / (mHeaderHeight == 0 ? 1 : mHeaderHeight), false);
    }

    /**
     * Display refresh animation and trigger refresh event, Delayed start.
     * 鏄剧ず鍒锋柊鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢锛屽欢鏃跺惎鍔?     * @param delayed 寮€濮嬪欢鏃?     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Override
    @Deprecated
    public boolean autoRefresh(int delayed) {
        return autoRefresh(delayed, mReboundDuration, 1f * ((mHeaderMaxDragRate/2 + 0.5f) * mHeaderHeight) / (mHeaderHeight == 0 ? 1 : mHeaderHeight), false);
    }


    /**
     * Display refresh animation without triggering events.
     * 鏄剧ず鍒锋柊鍔ㄧ敾锛屼笉瑙﹀彂浜嬩欢
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Override
    public boolean autoRefreshAnimationOnly() {
        return autoRefresh(mHandler == null ? 400 : 0, mReboundDuration, 1f * ((mHeaderMaxDragRate/2 + 0.5f) * mHeaderHeight) / (mHeaderHeight == 0 ? 1 : mHeaderHeight), true);
    }

    /**
     * Display refresh animation, Multifunction.
     * 鏄剧ず鍒锋柊鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢
     * @param delayed 寮€濮嬪欢鏃?     * @param duration 鎷栨嫿鍔ㄧ敾鎸佺画鏃堕棿
     * @param dragRate 鎷夋嫿鐨勯珮搴︽瘮鐜囷紙瑕佹眰 鈮?1 锛?     * @param animationOnly animation only 鍙湁鍔ㄧ敾
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Override
    public boolean autoRefresh(int delayed, final int duration, final float dragRate,final boolean animationOnly) {
        if (mState == RefreshState.None && isEnableRefreshOrLoadMore(mEnableRefresh)) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    reboundAnimator = ValueAnimator.ofInt(mSpinner, (int) (mHeaderHeight * dragRate));
                    reboundAnimator.setDuration(duration);
                    reboundAnimator.setInterpolator(new DecelerateInterpolator());
                    reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mKernel.moveSpinner((int) animation.getAnimatedValue(), true);
                        }
                    });
                    reboundAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            final View thisView = SmartRefreshLayout.this;
                            mLastTouchX = thisView.getMeasuredWidth() / 2;
                            mKernel.setState(RefreshState.PullDownToRefresh);
                        }
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            reboundAnimator = null;
                            if (animationOnly) {
                                if (mState == RefreshState.ReleaseToRefresh) {
                                    mKernel.setState(RefreshState.PullDownToRefresh);
                                }
                            } else {
                                if (mState != RefreshState.ReleaseToRefresh) {
                                    mKernel.setState(RefreshState.ReleaseToRefresh);
                                }
                            }
                            overSpinner();
                        }
                    });
                    reboundAnimator.start();
                }
            };
            if (delayed > 0) {
                reboundAnimator = new ValueAnimator();
                postDelayed(runnable, delayed);
            } else {
                runnable.run();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Display load more animation and trigger load more event.
     * 鏄剧ず鍔犺浇鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Override
    public boolean autoLoadMore() {
        return autoLoadMore(0, mReboundDuration, 1f * (mFooterHeight * (mFooterMaxDragRate / 2 + 0.5f)) / (mFooterHeight == 0 ? 1 : mFooterHeight), false);
    }

    /**
     * Display load more animation and trigger load more event, Delayed start.
     * 鏄剧ず鍔犺浇鍔ㄧ敾骞朵笖瑙﹀彂鍒锋柊浜嬩欢, 寤舵椂鍚姩
     * @param delayed 寮€濮嬪欢鏃?     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Override
    @Deprecated
    public boolean autoLoadMore(int delayed) {
        return autoLoadMore(delayed, mReboundDuration, 1f * (mFooterHeight * (mFooterMaxDragRate / 2 + 0.5f)) / (mFooterHeight == 0 ? 1 : mFooterHeight), false);
    }

    /**
     * Display load more animation without triggering events.
     * 鏄剧ず鍔犺浇鍔ㄧ敾锛屼笉瑙﹀彂浜嬩欢
     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Override
    public boolean autoLoadMoreAnimationOnly() {
        return autoLoadMore(0, mReboundDuration, 1f * (mFooterHeight * (mFooterMaxDragRate / 2 + 0.5f)) / (mFooterHeight == 0 ? 1 : mFooterHeight), true);
    }

    /**
     * Display load more animation and trigger load more event, Delayed start.
     * 鏄剧ず鍔犺浇鍔ㄧ敾, 澶氬姛鑳介€夐」
     * @param delayed 寮€濮嬪欢鏃?     * @param duration 鎷栨嫿鍔ㄧ敾鎸佺画鏃堕棿
     * @param dragRate 鎷夋嫿鐨勯珮搴︽瘮鐜囷紙瑕佹眰 鈮?1 锛?     * @return true or false, Status non-compliance will fail.
     *         鏄惁鎴愬姛锛堢姸鎬佷笉绗﹀悎浼氬け璐ワ級
     */
    @Override
    public boolean autoLoadMore(int delayed, final int duration, final float dragRate, final boolean animationOnly) {
        if (mState == RefreshState.None && (isEnableRefreshOrLoadMore(mEnableLoadMore) && !mFooterNoMoreData)) {
            if (reboundAnimator != null) {
                reboundAnimator.cancel();
            }
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    reboundAnimator = ValueAnimator.ofInt(mSpinner, -(int) (mFooterHeight * dragRate));
                    reboundAnimator.setDuration(duration);
                    reboundAnimator.setInterpolator(new DecelerateInterpolator());
                    reboundAnimator.addUpdateListener(new AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mKernel.moveSpinner((int) animation.getAnimatedValue(), true);
                        }
                    });
                    reboundAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            final View thisView = SmartRefreshLayout.this;
                            mLastTouchX = thisView.getMeasuredWidth() / 2;
                            mKernel.setState(RefreshState.PullUpToLoad);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            reboundAnimator = null;
                            if (animationOnly) {
                                if (mState == RefreshState.ReleaseToLoad) {
                                    mKernel.setState(RefreshState.PullUpToLoad);
                                }
                            } else {
                                if (mState != RefreshState.ReleaseToLoad) {
                                    mKernel.setState(RefreshState.ReleaseToLoad);
                                }
                            }
                            if (mEnableAutoLoadMore) {
                                mEnableAutoLoadMore = false;
                                overSpinner();
                                mEnableAutoLoadMore = true;
                            } else {
                                overSpinner();
                            }
                        }
                    });
                    reboundAnimator.start();
                }
            };
            if (delayed > 0) {
                reboundAnimator = new ValueAnimator();
                postDelayed(runnable, delayed);
            } else {
                runnable.run();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 璁剧疆榛樿 Header 鏋勫缓鍣?     * @param creator Header鏋勫缓鍣?     */
    public static void setDefaultRefreshHeaderCreator(@NonNull DefaultRefreshHeaderCreator creator) {
        sHeaderCreator = creator;
    }

    /**
     * 璁剧疆榛樿 Footer 鏋勫缓鍣?     * @param creator Footer鏋勫缓鍣?     */
    public static void setDefaultRefreshFooterCreator(@NonNull DefaultRefreshFooterCreator creator) {
        sFooterCreator = creator;
    }

    /**
     * 璁剧疆榛樿 Refresh 鍒濆鍖栧櫒
     * @param initializer 鍏ㄥ眬鍒濆鍖栧櫒
     */
    public static void setDefaultRefreshInitializer(@NonNull DefaultRefreshInitializer initializer) {
        sRefreshInitializer = initializer;
    }

    //<editor-fold desc="涓㈠純鐨凙PI">

//    /**
//     * 鏄惁姝ｅ湪鍒锋柊
//     * @return 鏄惁姝ｅ湪鍒锋柊
//     */
//    @Override
//    public boolean isRefreshing() {
//        return mState == RefreshState.Refreshing;
//    }
//
//    /**
//     * 鏄惁姝ｅ湪鍔犺浇
//     * @return 鏄惁姝ｅ湪鍔犺浇
//     */
//    @Override
//    public boolean isLoading() {
//        return mState == RefreshState.Loading;
//    }
//    /**
//     * 鎭㈠娌℃湁鏇村鏁版嵁鐨勫師濮嬬姸鎬?//     * @deprecated 浣跨敤 {@link RefreshLayout#setNoMoreData(boolean)} 浠ｆ浛
//     * @return SmartRefreshLayout
//     */
//    @Override
//    @Deprecated
//    public SmartRefreshLayout resetNoMoreData() {
//        return setNoMoreData(false);
//    }

    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="鏍稿績鎺ュ彛 RefreshKernel">

    /**
     * 鍒锋柊甯冨眬鏍稿績鍔熻兘鎺ュ彛
     * 涓哄姛鑳藉鏉傜殑 Header 鎴栬€?Footer 寮€鏀剧殑鎺ュ彛
     */
    public class RefreshKernelImpl implements RefreshKernel {

        @NonNull
        @Override
        public RefreshLayout getRefreshLayout() {
            return SmartRefreshLayout.this;
        }

        @NonNull
        @Override
        public RefreshContent getRefreshContent() {
            return mRefreshContent;
        }

        @Override
        public RefreshKernel setState(@NonNull RefreshState state) {
            switch (state) {
                case None:
                    resetStatus();
                    break;
                case PullDownToRefresh:
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.PullDownToRefresh);
                    } else {
                        setViceState(RefreshState.PullDownToRefresh);
                    }
                    break;
                case PullUpToLoad:
                    if (isEnableRefreshOrLoadMore(mEnableLoadMore) && !mState.isOpening && !mState.isFinishing && !(mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData)) {
                        notifyStateChanged(RefreshState.PullUpToLoad);
                    } else {
                        setViceState(RefreshState.PullUpToLoad);
                    }
                    break;
                case PullDownCanceled:
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.PullDownCanceled);
                        resetStatus();
                    } else {
                        setViceState(RefreshState.PullDownCanceled);
                    }
                    break;
                case PullUpCanceled:
                    if (isEnableRefreshOrLoadMore(mEnableLoadMore) && !mState.isOpening && !(mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData)) {
                        notifyStateChanged(RefreshState.PullUpCanceled);
                        resetStatus();
                    } else {
                        setViceState(RefreshState.PullUpCanceled);
                    }
                    break;
                case ReleaseToRefresh:
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.ReleaseToRefresh);
                    } else {
                        setViceState(RefreshState.ReleaseToRefresh);
                    }
                    break;
                case ReleaseToLoad:
                    if (isEnableRefreshOrLoadMore(mEnableLoadMore) && !mState.isOpening && !mState.isFinishing && !(mFooterNoMoreData && mEnableFooterFollowWhenNoMoreData)) {
                        notifyStateChanged(RefreshState.ReleaseToLoad);
                    } else {
                        setViceState(RefreshState.ReleaseToLoad);
                    }
                    break;
                case ReleaseToTwoLevel: {
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.ReleaseToTwoLevel);
                    } else {
                        setViceState(RefreshState.ReleaseToTwoLevel);
                    }
                    break;
                }
                case RefreshReleased: {
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableRefresh)) {
                        notifyStateChanged(RefreshState.RefreshReleased);
                    } else {
                        setViceState(RefreshState.RefreshReleased);
                    }
                    break;
                }
                case LoadReleased: {
                    if (!mState.isOpening && isEnableRefreshOrLoadMore(mEnableLoadMore)) {
                        notifyStateChanged(RefreshState.LoadReleased);
                    } else {
                        setViceState(RefreshState.LoadReleased);
                    }
                    break;
                }
                case Refreshing:
                    setStateRefreshing();
                    break;
                case Loading:
                    setStateLoading();
                    break;
                case RefreshFinish: {
                    if (mState == RefreshState.Refreshing) {
                        notifyStateChanged(RefreshState.RefreshFinish);
                    }
                    break;
                }
                case LoadFinish:{
                    if (mState == RefreshState.Loading) {
                        notifyStateChanged(RefreshState.LoadFinish);
                    }
                    break;
                }
                case TwoLevelReleased:
                    notifyStateChanged(RefreshState.TwoLevelReleased);
                    break;
                case TwoLevelFinish:
                    notifyStateChanged(RefreshState.TwoLevelFinish);
                    break;
                case TwoLevel:
                    notifyStateChanged(RefreshState.TwoLevel);
                    break;
            }
            return null;
        }

        @Override
        public RefreshKernel startTwoLevel(boolean open) {
            if (open) {
                AnimatorListenerAdapter listener = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mKernel.setState(RefreshState.TwoLevel);
                    }
                };
                final View thisView = SmartRefreshLayout.this;
                ValueAnimator animator = animSpinner(thisView.getMeasuredHeight());
                if (animator != null && animator == reboundAnimator) {
                    animator.setDuration(mFloorDuration);
                    animator.addListener(listener);
                } else {
                    listener.onAnimationEnd(null);
                }
            } else {
                if (animSpinner(0) == null) {
                    notifyStateChanged(RefreshState.None);
                }
            }
            return this;
        }

        @Override
        public RefreshKernel finishTwoLevel() {
            if (mState == RefreshState.TwoLevel) {
                mKernel.setState(RefreshState.TwoLevelFinish);
                if (mSpinner == 0) {
                    moveSpinner(0, false);
                    notifyStateChanged(RefreshState.None);
                } else {
                    animSpinner(0).setDuration(mFloorDuration);
                }
            }
            return this;
        }
        //<editor-fold desc="鐘舵€佹洿鏀?state changes">

        //</editor-fold>

        //<editor-fold desc="瑙嗗浘浣嶇Щ Spinner">

        /*
         * 绉诲姩婊氬姩 Scroll
         * moveSpinner 鐨勫彇鍚嶆潵鑷?璋锋瓕瀹樻柟鐨?{@link androidx.swiperefreshlayout.widget.SwipeRefreshLayout#moveSpinner(float)}
         */
        @SuppressWarnings("ConstantConditions")
        public RefreshKernel moveSpinner(int spinner, boolean isDragging) {
            if (mSpinner == spinner
                    && (mRefreshHeader == null || !mRefreshHeader.isSupportHorizontalDrag())
                    && (mRefreshFooter == null || !mRefreshFooter.isSupportHorizontalDrag())) {
                return this;
            }
            final View thisView = SmartRefreshLayout.this;
            final int oldSpinner = mSpinner;
            mSpinner = spinner;
            if (isDragging && mViceState.isDragging) {
                if (mSpinner > mHeaderHeight * mHeaderTriggerRate) {
                    if (mState != RefreshState.ReleaseToTwoLevel) {
                        mKernel.setState(RefreshState.ReleaseToRefresh);
                    }
                } else if (-mSpinner > mFooterHeight * mFooterTriggerRate && !mFooterNoMoreData) {
                    mKernel.setState(RefreshState.ReleaseToLoad);
                } else if (mSpinner < 0 && !mFooterNoMoreData) {
                    mKernel.setState(RefreshState.PullUpToLoad);
                } else if (mSpinner > 0) {
                    mKernel.setState(RefreshState.PullDownToRefresh);
                }
            }
            if (mRefreshContent != null) {
                Integer tSpinner = null;
                if (spinner >= 0 && mRefreshHeader != null) {
                    if (isEnableTranslationContent(mEnableHeaderTranslationContent, mRefreshHeader)) {
                        tSpinner = spinner;
                    } else if (oldSpinner < 0) {
                        tSpinner = 0;
                    }
                }
                if (spinner <= 0 && mRefreshFooter != null) {
                    if (isEnableTranslationContent(mEnableFooterTranslationContent, mRefreshFooter)) {
                        tSpinner = spinner;
                    } else if (oldSpinner > 0) {
                        tSpinner = 0;
                    }
                }
                if (tSpinner != null) {
                    mRefreshContent.moveSpinner(tSpinner, mHeaderTranslationViewId, mFooterTranslationViewId);
                    boolean header = mEnableClipHeaderWhenFixedBehind && mRefreshHeader != null && mRefreshHeader.getSpinnerStyle() == SpinnerStyle.FixedBehind;
                    header = header || mHeaderBackgroundColor != 0;
                    boolean footer = mEnableClipFooterWhenFixedBehind && mRefreshFooter != null && mRefreshFooter.getSpinnerStyle() == SpinnerStyle.FixedBehind;
                    footer = footer || mFooterBackgroundColor != 0;
                    if ((header && (tSpinner >= 0 || oldSpinner > 0)) || (footer && (tSpinner <= 0 || oldSpinner < 0))) {
                        thisView.invalidate();
                    }
                }
            }
            if ((spinner >= 0 || oldSpinner > 0) && mRefreshHeader != null) {

                final int offset = Math.max(spinner, 0);
                final int headerHeight = mHeaderHeight;
                final int maxDragHeight = (int) (mHeaderHeight * mHeaderMaxDragRate);
                final float percent = 1f * offset / (mHeaderHeight == 0 ? 1 : mHeaderHeight);

                if (isEnableRefreshOrLoadMore(mEnableRefresh) || (mState == RefreshState.RefreshFinish && !isDragging)) {
                    if (oldSpinner != mSpinner) {
                        if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Translate) {
                            mRefreshHeader.getView().setTranslationY(mSpinner);
                            if (mHeaderBackgroundColor != 0 && mPaint != null && !isEnableTranslationContent(mEnableHeaderTranslationContent,mRefreshHeader)) {
                                thisView.invalidate();
                            }
                        } else if (mRefreshHeader.getSpinnerStyle() == SpinnerStyle.Scale){
                            mRefreshHeader.getView().requestLayout();
                        }
                        mRefreshHeader.onMoving(isDragging, percent, offset, headerHeight, maxDragHeight);
                    }
                    if (isDragging && mRefreshHeader.isSupportHorizontalDrag()) {
                        final int offsetX = (int) mLastTouchX;
                        final int offsetMax = thisView.getWidth();
                        final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                        mRefreshHeader.onHorizontalDrag(percentX, offsetX, offsetMax);
                    }
                }

                if (oldSpinner != mSpinner && mOnMultiPurposeListener != null && mRefreshHeader instanceof RefreshHeader) {
                    mOnMultiPurposeListener.onHeaderMoving((RefreshHeader) mRefreshHeader, isDragging, percent, offset, headerHeight, maxDragHeight);
                }

            }
            if ((spinner <= 0 || oldSpinner < 0) && mRefreshFooter != null) {

                final int offset = -Math.min(spinner, 0);
                final int footerHeight = mFooterHeight;
                final int maxDragHeight = (int) (mFooterHeight * mFooterMaxDragRate);
                final float percent = offset * 1f / (mFooterHeight == 0 ? 1 : mFooterHeight);

                if (isEnableRefreshOrLoadMore(mEnableLoadMore) || (mState == RefreshState.LoadFinish && !isDragging)) {
                    if (oldSpinner != mSpinner) {
                        if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Translate) {
                            mRefreshFooter.getView().setTranslationY(mSpinner);
                            if (mFooterBackgroundColor != 0 && mPaint != null && !isEnableTranslationContent(mEnableFooterTranslationContent, mRefreshFooter)) {
                                thisView.invalidate();
                            }
                        } else if (mRefreshFooter.getSpinnerStyle() == SpinnerStyle.Scale){
                            mRefreshFooter.getView().requestLayout();
                        }
                        mRefreshFooter.onMoving(isDragging, percent, offset, footerHeight, maxDragHeight);
                    }
                    if (isDragging && mRefreshFooter.isSupportHorizontalDrag()) {
                        final int offsetX = (int) mLastTouchX;
                        final int offsetMax = thisView.getWidth();
                        final float percentX = mLastTouchX / (offsetMax == 0 ? 1 : offsetMax);
                        mRefreshFooter.onHorizontalDrag(percentX, offsetX, offsetMax);
                    }
                }

                if (oldSpinner != mSpinner && mOnMultiPurposeListener != null && mRefreshFooter instanceof RefreshFooter) {
                    mOnMultiPurposeListener.onFooterMoving((RefreshFooter)mRefreshFooter, isDragging, percent, offset, footerHeight, maxDragHeight);
                }
            }
            return this;
        }

        public ValueAnimator animSpinner(int endSpinner) {
            return SmartRefreshLayout.this.animSpinner(endSpinner, 0, mReboundInterpolator, mReboundDuration);
        }

        //</editor-fold>

        //<editor-fold desc="璇锋眰浜嬩欢">

        @Override
        public RefreshKernel requestDrawBackgroundFor(@NonNull RefreshInternal internal, int backgroundColor) {
            if (mPaint == null && backgroundColor != 0) {
                mPaint = new Paint();
            }
            if (internal.equals(mRefreshHeader)) {
                mHeaderBackgroundColor = backgroundColor;
            } else if (internal.equals(mRefreshFooter)) {
                mFooterBackgroundColor = backgroundColor;
            }
            return this;
        }

        @Override
        public RefreshKernel requestNeedTouchEventFor(@NonNull RefreshInternal internal, boolean request) {
            if (internal.equals(mRefreshHeader)) {
                mHeaderNeedTouchEventWhenRefreshing = request;
            } else if (internal.equals(mRefreshFooter)) {
                mFooterNeedTouchEventWhenLoading = request;
            }
            return this;
        }

        @Override
        public RefreshKernel requestDefaultTranslationContentFor(@NonNull RefreshInternal internal, boolean translation) {
            if (internal.equals(mRefreshHeader)) {
                if (!mManualHeaderTranslationContent) {
                    mManualHeaderTranslationContent = true;
                    mEnableHeaderTranslationContent = translation;
                }
            } else if (internal.equals(mRefreshFooter)) {
                if (!mManualFooterTranslationContent) {
                    mManualFooterTranslationContent = true;
                    mEnableFooterTranslationContent = translation;
                }
            }
            return this;
        }
        @Override
        public RefreshKernel requestRemeasureHeightFor(@NonNull RefreshInternal internal) {
            if (internal.equals(mRefreshHeader)) {
                if (mHeaderHeightStatus.notified) {
                    mHeaderHeightStatus = mHeaderHeightStatus.unNotify();
                }
            } else if (internal.equals(mRefreshFooter)) {
                if (mFooterHeightStatus.notified) {
                    mFooterHeightStatus = mFooterHeightStatus.unNotify();
                }
            }
            return this;
        }
        @Override
        public RefreshKernel requestFloorDuration(int duration) {
            mFloorDuration = duration;
            return this;
        }
        //</editor-fold>
    }
    //</editor-fold>

    //<editor-fold desc="鍐呭瓨娉勬紡 postDelayed浼樺寲">

    /**
     * 閲嶅啓 post 鍜?postDelayed
     * 鑷繁鐢?Handler 鍜?DelayedRunnable 瀹炵幇 闃叉鍐呭瓨娉勬紡
     */
    @Override
    public boolean post(@NonNull Runnable action) {
        if (mHandler == null) {
            mListDelayedRunnable = mListDelayedRunnable == null ? new ArrayList<DelayedRunnable>() : mListDelayedRunnable;
            mListDelayedRunnable.add(new DelayedRunnable(action,0));
            return false;
        }
        return mHandler.post(new DelayedRunnable(action,0));
    }

    /**
     * 閲嶅啓 post 鍜?postDelayed
     * 鑷繁鐢?Handler 鍜?DelayedRunnable 瀹炵幇 闃叉鍐呭瓨娉勬紡
     */
    @Override
    public boolean postDelayed(@NonNull Runnable action, long delayMillis) {
        if (delayMillis == 0) {
            new DelayedRunnable(action,0).run();
            return true;
        }
        if (mHandler == null) {
            mListDelayedRunnable = mListDelayedRunnable == null ? new ArrayList<DelayedRunnable>() : mListDelayedRunnable;
            mListDelayedRunnable.add(new DelayedRunnable(action, delayMillis));
            return false;
        }
        return mHandler.postDelayed(new DelayedRunnable(action, 0), delayMillis);
    }

    //</editor-fold>
}

