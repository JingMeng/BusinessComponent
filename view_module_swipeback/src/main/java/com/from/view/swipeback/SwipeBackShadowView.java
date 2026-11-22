package com.from.view.swipeback;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import androidx.annotation.DrawableRes;
import androidx.core.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.ref.WeakReference;

import static com.from.view.swipeback.SwipeBackUtil.getNavigationBarHeight;
import static com.from.view.swipeback.SwipeBackUtil.getRealScreenHeight;

/**
 * 宸︿晶闃村奖鎺т欢
 *
 * @author Vea
 * @version 0.0.9
 * @since 2019-1
 */
class SwipeBackShadowView extends FrameLayout {
    private static final String TAG = SwipeBackShadowView.class.getSimpleName();
    private static final float WE_CHAT_STYLE_MAX_OFFSET = 0.75f;
    private Activity mActivity;
    private WeakReference<Activity> mPreActivity;
    private ViewGroup mPreDecorView;
    private View mPreContentView;
    private ImageView mPreImageView;
    private View mShadowView;
    /**
     * 鏄惁鏄剧ず婊戝姩杩斿洖鐨勯槾褰辨晥鏋?     */
    private boolean mIsNeedShowShadow = true;
    /**
     * 闃村奖璧勬簮 id
     */
    private int mShadowResId = R.drawable.maven_swipeback_shadow;
    /**
     * 闃村奖鍖哄煙鐨勯€忔槑搴︽槸鍚︽牴鎹粦鍔ㄧ殑璺濈娓愬彉
     */
    private boolean mIsShadowAlphaGradient = true;
    /**
     * 鏄惁鏄井淇℃粦鍔ㄨ繑鍥炴牱寮?     */
    private boolean mIsWeChatStyle = true;

    SwipeBackShadowView(Activity activity) {
        super(activity);
        mActivity = activity;
        updateShadow();
    }

    /**
     * 璁剧疆鏄惁鏄剧ず婊戝姩杩斿洖鐨勯槾褰辨晥鏋溿€傞粯璁ゅ€间负 true
     */
    void setIsNeedShowShadow(boolean isNeedShowShadow) {
        mIsNeedShowShadow = isNeedShowShadow;
        updateShadow();
    }

    /**
     * 璁剧疆闃村奖璧勬簮 id銆傞粯璁ゅ€间负 R.drawable.maven_swipeback_shadow
     */
    void setShadowResId(@DrawableRes int shadowResId) {
        mShadowResId = shadowResId;
        updateShadow();
    }

    /**
     * 璁剧疆闃村奖鍖哄煙鐨勯€忔槑搴︽槸鍚︽牴鎹粦鍔ㄧ殑璺濈娓愬彉銆傞粯璁ゅ€间负 true
     */
    void setIsShadowAlphaGradient(boolean isShadowAlphaGradient) {
        mIsShadowAlphaGradient = isShadowAlphaGradient;
    }

    /**
     * 璁剧疆鏄惁鏄井淇℃粦鍔ㄨ繑鍥炴牱寮忋€傞粯璁ゅ€间负 true
     */
    void setIsWeChatStyle(boolean isWeChatStyle) {
        mIsWeChatStyle = isWeChatStyle;
    }

    private void updateShadow() {
        if (mIsNeedShowShadow) {
            if (mShadowView == null) {
                mShadowView = new View(getContext());
                addView(mShadowView, getChildCount(), new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }
            mShadowView.setBackgroundResource(mShadowResId);
        } else {
            if (mShadowView != null) {
                removeView(mShadowView);
            }
        }
    }

    void bindPreActivity() {
        if (mPreActivity == null) {
            Activity preActivity = SwipeBackManager.getInstance().getPenultimateActivity(mActivity);
            if (preActivity != null) {
                mPreActivity = new WeakReference<>(preActivity);
                mPreDecorView = (ViewGroup) preActivity.getWindow().getDecorView();
                for (int i = 0; i < mPreDecorView.getChildCount(); i++) {
                    if (mPreDecorView.getChildAt(i) instanceof SwipeBackLayout) {
                        mPreContentView = mPreDecorView.getChildAt(i);
                        mPreDecorView.removeView(mPreContentView);
                        addView(mPreContentView, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                    }
                }
            }
        }
    }

    void unBindPreActivity(boolean isNeedAddImageView) {
        if (mPreActivity == null) {
            return;
        }

        Activity activity = mPreActivity.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }

        if (mPreDecorView != null && mPreContentView != null) {
            if (isNeedAddImageView && mPreImageView == null && containsProblemView((ViewGroup) mPreContentView)) {
                mPreImageView = new ImageView(mActivity);
                mPreImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                mPreImageView.setImageBitmap(getBitmap(mPreContentView));
                addView(mPreImageView, 0, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }

            removeView(mPreContentView);

            ViewGroup.LayoutParams lp = null;
            if (!(mPreContentView instanceof SwipeBackLayout)) {
                int width = mPreDecorView.getWidth();
                int height = mPreDecorView.getHeight() - getNavigationBarHeight(activity);
                if (!SwipeBackUtil.isPortrait(activity)) {
                    width = mPreDecorView.getWidth() - getNavigationBarHeight(activity);
                    height = mPreDecorView.getHeight();
                }
                if (mPreDecorView instanceof FrameLayout) {
                    lp = new FrameLayout.LayoutParams(width, height);
                } else if (mPreDecorView instanceof LinearLayout) {
                    lp = new LinearLayout.LayoutParams(width, height);
                } else if (mPreDecorView instanceof RelativeLayout) {
                    lp = new RelativeLayout.LayoutParams(width, height);
                }
            }
            if (lp == null) {
                mPreDecorView.addView(mPreContentView, 0);
            } else {
                mPreDecorView.addView(mPreContentView, 0, lp);
            }

            mPreContentView = null;
            mPreActivity.clear();
            mPreActivity = null;
        }
    }

    @Override
    protected void dispatchDraw(final Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mPreImageView != null) {
            return;
        }
        // add 鍜?remove 鏂瑰紡鏃讹紝婊戝姩杩斿洖缁撴潫鍚庣殑鏈€鍚庝竴甯ч€氳繃 draw 鏉ュ疄鐜帮紝閬垮厤鎶栧姩
        if (mPreContentView == null && mPreDecorView != null) {
            mPreDecorView.draw(canvas);
        }
    }

    private Bitmap getBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0,
                SwipeBackUtil.getRealScreenWidth(mActivity),
                getRealScreenHeight(mActivity) - getNavigationBarHeight(mActivity));
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 璇?ViewGroup 涓槸鍚﹀寘鍚鑷村娆?draw 鍚庡簲鐢ㄥ穿婧冪殑 View
     */
    private boolean containsProblemView(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        View childView;
        for (int i = 0; i < childCount; i++) {
            childView = viewGroup.getChildAt(i);
            if (SwipeBackManager.getInstance().isProblemView(childView)) {
                return true;
            } else if (childView instanceof ViewGroup) {
                if (containsProblemView((ViewGroup) childView)) {
                    return true;
                }
            }
        }
        return false;
    }

    void setShadowAlpha(float alpha) {
        if (mIsNeedShowShadow && mIsShadowAlphaGradient) {
            ViewCompat.setAlpha(mShadowView, alpha);
        }
    }

    void onPanelSlide(float slideOffset) {
        if (mPreContentView == null) return;
        if (mIsWeChatStyle) { // 寰俊婊戝姩杩斿洖鏍峰紡鐨勬儏鍐?            ViewCompat.setTranslationX(mPreContentView, (mPreContentView.getMeasuredWidth() * WE_CHAT_STYLE_MAX_OFFSET) * (1 - slideOffset));
        } else { // 闈炲井淇℃粦鍔ㄨ繑鍥炴牱寮忕殑鎯呭喌
            ViewCompat.setTranslationX(mPreContentView, mPreContentView.getMeasuredWidth() * (1 - slideOffset));
        }
    }

    void onPanelClosed() {
        if (mPreContentView == null) return;
        if (mIsWeChatStyle) { // 寰俊婊戝姩杩斿洖鏍峰紡鐨勬儏鍐?            ViewCompat.setTranslationX(mPreContentView, 0);
        } else { // 闈炲井淇℃粦鍔ㄨ繑鍥炴牱寮忕殑鎯呭喌
            ViewCompat.setTranslationX(mPreContentView, 0);
        }
        unBindPreActivity(false);
    }

//    private void onPanelSlide(Activity currentActivity, float slideOffset) {
//        try {
//            Activity preActivity = SwipeBackManager.getInstance().getPenultimateActivity(currentActivity);
//            if (preActivity != null) {
//                View decorView = preActivity.getWindow().getDecorView();
//                ViewCompat.setTranslationX(decorView, -decorView.getMeasuredWidth() * (1 - WE_CHAT_STYLE_MAX_OFFSET) * (1 - slideOffset));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private void onPanelClosed(Activity currentActivity) {
//        try {
//            Activity preActivity = SwipeBackManager.getInstance().getPenultimateActivity(currentActivity);
//            if (preActivity != null) {
//                View decorView = preActivity.getWindow().getDecorView();
//                ViewCompat.setTranslationX(decorView, 0);
//            }
//        } catch (Exception e) {
//        }
//    }
}

