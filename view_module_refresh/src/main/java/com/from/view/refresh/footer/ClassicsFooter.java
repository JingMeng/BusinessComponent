package com.from.view.refresh.footer;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.from.view.refresh.R;
import com.from.view.refresh.api.RefreshFooter;
import com.from.view.refresh.api.RefreshLayout;
import com.from.view.refresh.constant.RefreshState;
import com.from.view.refresh.constant.SpinnerStyle;
import com.from.view.refresh.internal.ArrowDrawable;
import com.from.view.refresh.internal.InternalClassics;
import com.from.view.refresh.internal.ProgressDrawable;
import com.from.view.refresh.util.DensityUtil;

/**
 * 缁忓吀涓婃媺搴曢儴缁勪欢
 * Created by SCWANG on 2017/5/28.
 */

@SuppressWarnings({"unused", "UnusedReturnValue", "deprecation"})
public class ClassicsFooter extends InternalClassics<ClassicsFooter> implements RefreshFooter {

    public static String REFRESH_FOOTER_PULLING = null;//"涓婃媺鍔犺浇鏇村";
    public static String REFRESH_FOOTER_RELEASE = null;//"閲婃斁绔嬪嵆鍔犺浇";
    public static String REFRESH_FOOTER_LOADING = null;//"姝ｅ湪鍔犺浇...";
    public static String REFRESH_FOOTER_REFRESHING = null;//"姝ｅ湪鍒锋柊...";
    public static String REFRESH_FOOTER_FINISH = null;//"鍔犺浇瀹屾垚";
    public static String REFRESH_FOOTER_FAILED = null;//"鍔犺浇澶辫触";
    public static String REFRESH_FOOTER_NOTHING = null;//"娌℃湁鏇村鏁版嵁浜?;

    protected String mTextPulling = null;//"涓婃媺鍔犺浇鏇村";
    protected String mTextRelease = null;//"閲婃斁绔嬪嵆鍔犺浇";
    protected String mTextLoading = null;//"姝ｅ湪鍔犺浇...";
    protected String mTextRefreshing = null;//"姝ｅ湪鍒锋柊...";
    protected String mTextFinish = null;//"鍔犺浇瀹屾垚";
    protected String mTextFailed = null;//"鍔犺浇澶辫触";
    protected String mTextNothing = null;//"娌℃湁鏇村鏁版嵁浜?;

    protected boolean mNoMoreData = false;

    //<editor-fold desc="LinearLayout">
    public ClassicsFooter(Context context) {
        this(context, null);
    }

    public ClassicsFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicsFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (REFRESH_FOOTER_PULLING == null) {
            REFRESH_FOOTER_PULLING = context.getString(R.string.srl_footer_pulling);
        }

        if (REFRESH_FOOTER_RELEASE == null) {
            REFRESH_FOOTER_RELEASE = context.getString(R.string.srl_footer_release);
        }

        if (REFRESH_FOOTER_LOADING == null) {
            REFRESH_FOOTER_LOADING = context.getString(R.string.srl_footer_loading);
        }

        if (REFRESH_FOOTER_REFRESHING == null) {
            REFRESH_FOOTER_REFRESHING = context.getString(R.string.srl_footer_refreshing);
        }

        if (REFRESH_FOOTER_FINISH == null) {
            REFRESH_FOOTER_FINISH = context.getString(R.string.srl_footer_finish);
        }

        if (REFRESH_FOOTER_FAILED == null) {
            REFRESH_FOOTER_FAILED = context.getString(R.string.srl_footer_failed);
        }

        if (REFRESH_FOOTER_NOTHING == null) {
            REFRESH_FOOTER_NOTHING = context.getString(R.string.srl_footer_nothing);
        }

        final View thisView = this;
        final View arrowView = mArrowView;
        final View progressView = mProgressView;
        final DensityUtil density = new DensityUtil();

        mTitleText.setTextColor(0xff666666);
        mTitleText.setText(thisView.isInEditMode() ? REFRESH_FOOTER_LOADING : REFRESH_FOOTER_PULLING);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsFooter);

        LayoutParams lpArrow = (LayoutParams) arrowView.getLayoutParams();
        LayoutParams lpProgress = (LayoutParams) progressView.getLayoutParams();
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlDrawableMarginRight, density.dip2px(20));
        lpArrow.rightMargin = lpProgress.rightMargin;

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableProgressSize, lpProgress.height);

        lpArrow.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.ClassicsFooter_srlDrawableSize, lpProgress.height);

        mFinishDuration = ta.getInt(R.styleable.ClassicsFooter_srlFinishDuration, mFinishDuration);
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.ClassicsFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal())];

        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableArrow));
        } else {
            mArrowDrawable = new ArrowDrawable();
            mArrowDrawable.setColor(0xff666666);
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsFooter_srlDrawableProgress));
        } else {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlTextSizeTitle, DensityUtil.dp2px(16)));
        } else {
            mTitleText.setTextSize(16);
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
            super.setAccentColor(ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0));
        }

        mTextPulling = REFRESH_FOOTER_PULLING;
        mTextRelease = REFRESH_FOOTER_RELEASE;
        mTextLoading = REFRESH_FOOTER_LOADING;
        mTextRefreshing = REFRESH_FOOTER_REFRESHING;
        mTextFinish = REFRESH_FOOTER_FINISH;
        mTextFailed = REFRESH_FOOTER_FAILED;
        mTextNothing = REFRESH_FOOTER_NOTHING;

        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextPulling)){
            mTextPulling = ta.getString(R.styleable.ClassicsFooter_srlTextPulling);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextRelease)){
            mTextRelease = ta.getString(R.styleable.ClassicsFooter_srlTextRelease);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextLoading)){
            mTextLoading = ta.getString(R.styleable.ClassicsFooter_srlTextLoading);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextRefreshing)){
            mTextRefreshing = ta.getString(R.styleable.ClassicsFooter_srlTextRefreshing);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextFinish)){
            mTextFinish = ta.getString(R.styleable.ClassicsFooter_srlTextFinish);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextFailed)){
            mTextFailed = ta.getString(R.styleable.ClassicsFooter_srlTextFailed);
        }
        if(ta.hasValue(R.styleable.ClassicsFooter_srlTextNothing)){
            mTextNothing = ta.getString(R.styleable.ClassicsFooter_srlTextNothing);
        }

        ta.recycle();

    }

//    @Override
//    protected ClassicsFooter self() {
//        return this;
//    }

    //</editor-fold>

    //<editor-fold desc="RefreshFooter">

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (!mNoMoreData) {
            super.onStartAnimator(refreshLayout, height, maxDragHeight);
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (!mNoMoreData) {
            mTitleText.setText(success ? mTextFinish : mTextFailed);
            return super.onFinish(layout, success);
        }
        return 0;
    }

    /**
     * ClassicsFooter 鍦?SpinnerStyle.FixedBehind)鏃舵墠鏈変富棰樿壊
     */
    @Override@Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (mSpinnerStyle == SpinnerStyle.FixedBehind) {
            super.setPrimaryColors(colors);
        }
    }

    /**
     * 璁剧疆鏁版嵁鍏ㄩ儴鍔犺浇瀹屾垚锛屽皢涓嶈兘鍐嶆瑙﹀彂鍔犺浇鍔熻兘
     */
    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            final View arrowView = mArrowView;
            if (noMoreData) {
                mTitleText.setText(mTextNothing);
                arrowView.setVisibility(GONE);
            } else {
                mTitleText.setText(mTextPulling);
                arrowView.setVisibility(VISIBLE);
            }
        }
        return true;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        if (!mNoMoreData) {
            switch (newState) {
                case None:
                    arrowView.setVisibility(VISIBLE);
                case PullUpToLoad:
                    mTitleText.setText(mTextPulling);
                    arrowView.animate().rotation(180);
                    break;
                case Loading:
                case LoadReleased:
                    arrowView.setVisibility(GONE);
                    mTitleText.setText(mTextLoading);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(mTextRelease);
                    arrowView.animate().rotation(0);
                    break;
                case Refreshing:
                    mTitleText.setText(mTextRefreshing);
                    arrowView.setVisibility(GONE);
                    break;
            }
        }
    }
    //</editor-fold>

}

