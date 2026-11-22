package com.from.view.swipeback;

import android.app.Activity;
import android.app.Application;
import androidx.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.view.View;

import java.util.List;

import static com.from.view.swipeback.SwipeBackUtil.convertActivityFromTranslucent;

/**
 * 婊戝姩杩斿洖甯姪绫? *
 * @author Vea
 * @version 0.0.9
 * @since 2019-1
 */
public final class SwipeBackHelper {
    private Activity mActivity;
    private Delegate mDelegate;
    private SwipeBackLayout mSwipeBackLayout;

    private SwipeBackHelper() {
    }

    /**
     * 蹇呴』鍦?Application 鐨?onCreate 鏂规硶涓皟鐢?     *
     * @param application          搴旂敤绋嬪簭涓婁笅鏂?     * @param problemViewClassList 濡傛灉鍙戠幇婊戝姩杩斿洖鍚庣珛鍗宠Е鎽哥晫闈㈡椂搴旂敤宕╂簝锛?     *                             璇锋妸璇ョ晫闈㈤噷姣旇緝鐗规畩鐨?View 鐨?class 娣诲姞鍒拌闆嗗悎涓紝
     *                             鐩墠鍦ㄥ簱涓凡缁忔坊鍔犱簡 WebView 鍜?SurfaceView
     * @param options              濡傛灉鏈変簺绗笁鏂瑰簱 Activity 涓嶉渶瑕?swipeBack 鍙娇鐢∣ption 閰嶇疆
     * @param delegate             渚ф粦浠ｇ悊锛屾墿灞曟粦鍔ㄥ紑濮嬪拰缁撴潫浜嬩欢
     */
    public static void init(Application application, List<Class<? extends View>> problemViewClassList, SwipeOptions options, Delegate delegate) {
        SwipeBackManager.getInstance().init(application, problemViewClassList, options, delegate);
    }

    /**
     * 蹇呴』鍦?Application 鐨?onCreate 鏂规硶涓皟鐢?     *
     * @param application application
     */
    public static void init(Application application) {
        SwipeBackManager.getInstance().init(application, null, null, null);
    }

    /**
     * @param activity
     */
    private SwipeBackHelper(Activity activity) {
        mActivity = activity;
        initSwipeBackFinish();
    }

    public static SwipeBackHelper create(Activity activity) {
        return new SwipeBackHelper(activity);
    }

    public void setSlideDelegate(Delegate delegate) {
        if (delegate != null) {
            mDelegate = delegate;
        }
    }

    /**
     * 鍒濆鍖栨粦鍔ㄨ繑鍥?     */
    private void initSwipeBackFinish() {
        SwipeOptions options = SwipeBackManager.getInstance().getOptions();
        if (options.getClassNameList().contains(mActivity.getClass().getSimpleName())) {
            return;
        }
        mSwipeBackLayout = new SwipeBackLayout(mActivity);
        mSwipeBackLayout.attachToActivity(mActivity);

        setIsWeChatStyle(options.getIsWeChatStyle());
        setIsNavigationBarOverlap(options.getIsNavigationBarOverlap());
        setIsNeedShowShadow(options.getIsNeedShowShadow());
        setIsOnlyTrackingLeftEdge(options.getIsOnlyTrackingLeftEdge());
        setIsShadowAlphaGradient(options.getIsShadowAlphaGradient());
        setShadowResId(options.getShadowResId());

        mSwipeBackLayout.setPanelSlideListener(new SwipeBackLayout.PanelSlideListener() {
            private boolean flag;

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if (!flag) {
                    convertActivityFromTranslucent(mActivity);
                    flag = true;
                }
                // 寮€濮嬫粦鍔ㄨ繑鍥炴椂鍏抽棴杞敭鐩?                if (slideOffset < 0.03) {
                    SwipeBackUtil.closeKeyboard(mActivity);
                }
                if (mDelegate != null) {
                    mDelegate.onSwipeBackLayoutSlide(slideOffset);
                }
            }

            @Override
            public void onPanelOpened(View panel) {
                if (mDelegate != null) {
                    mDelegate.onSwipeBackLayoutExecuted(mActivity);
                }
                swipeBackward();
            }

            @Override
            public void onPanelClosed(View panel) {
                if (mDelegate != null) {
                    mDelegate.onSwipeBackLayoutCancel();
                }
            }
        });
    }

    /**
     * 璁剧疆鏄惁浠呬粎璺熻釜宸︿晶杈圭紭鐨勬粦鍔ㄨ繑鍥炪€傞粯璁ゅ€间负 true
     *
     * @param isOnlyTrackingLeftEdge
     * @return
     */
    public SwipeBackHelper setIsOnlyTrackingLeftEdge(boolean isOnlyTrackingLeftEdge) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setIsOnlyTrackingLeftEdge(isOnlyTrackingLeftEdge);
        }
        return this;
    }

    /**
     * 璁剧疆鏄惁鏄井淇℃粦鍔ㄨ繑鍥炴牱寮忋€傞粯璁ゅ€间负 true
     *
     * @param isWeChatStyle
     * @return
     */
    public SwipeBackHelper setIsWeChatStyle(boolean isWeChatStyle) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setIsWeChatStyle(isWeChatStyle);
        }
        return this;
    }

    /**
     * 璁剧疆闃村奖璧勬簮 id銆傞粯璁ゅ€间负 R.drawable.maven_swipeback_shadow
     *
     * @param shadowResId
     * @return
     */
    public SwipeBackHelper setShadowResId(@DrawableRes int shadowResId) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setShadowResId(shadowResId);
        }
        return this;
    }

    /**
     * 璁剧疆鏄惁鏄剧ず婊戝姩杩斿洖鐨勯槾褰辨晥鏋溿€傞粯璁ゅ€间负 true
     *
     * @param isNeedShowShadow
     * @return
     */
    public SwipeBackHelper setIsNeedShowShadow(boolean isNeedShowShadow) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setIsNeedShowShadow(isNeedShowShadow);
        }
        return this;
    }

    /**
     * 璁剧疆闃村奖鍖哄煙鐨勯€忔槑搴︽槸鍚︽牴鎹粦鍔ㄧ殑璺濈娓愬彉銆傞粯璁ゅ€间负 true
     *
     * @param isShadowAlphaGradient
     * @return
     */
    public SwipeBackHelper setIsShadowAlphaGradient(boolean isShadowAlphaGradient) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setIsShadowAlphaGradient(isShadowAlphaGradient);
        }
        return this;
    }

    /**
     * 璁剧疆瑙﹀彂閲婃斁鍚庤嚜鍔ㄦ粦鍔ㄨ繑鍥炵殑闃堝€硷紝榛樿鍊间负 0.3f
     *
     * @param threshold
     */
    public SwipeBackHelper setSwipeBackThreshold(@FloatRange(from = 0.0f, to = 1.0f) float threshold) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setSwipeBackThreshold(threshold);
        }
        return this;
    }

    /**
     * 璁剧疆搴曢儴瀵艰埅鏉℃槸鍚︽偓娴湪鍐呭涓?     *
     * @param overlap
     */
    public SwipeBackHelper setIsNavigationBarOverlap(boolean overlap) {
        if (mSwipeBackLayout != null) {
            mSwipeBackLayout.setIsNavigationBarOverlap(overlap);
        }
        return this;
    }

    /**
     * 鏄惁姝ｅ湪婊戝姩
     *
     * @return
     */
    public boolean isSliding() {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.isSliding();
        }
        return false;
    }

    /**
     * 鎵ц婊戝姩杩斿洖鍒板埌涓婁竴涓?Activity 鐨勫姩鐢?     */
    public void executeSwipeBackAnim() {
        executeSwipeBackAnim(mActivity);
    }

    /**
     * 鎵ц婊戝姩杩斿洖鍒板埌涓婁竴涓?Activity 鐨勫姩鐢汇€傝繖閲屽紕鎴愰潤鎬佹柟娉曪紝鏂逛究鍦?Fragment 涓皟鐢?     */
    public static void executeSwipeBackAnim(Activity activity) {
        activity.overridePendingTransition(R.anim.maven_swipeback_translucent_enter, R.anim.maven_swipeback_translucent_exit);
    }

    /**
     * 婊戝姩杩斿洖涓婁竴涓?Activity锛屽苟閿€姣佸綋鍓?Activity
     */
    public void swipeBackward() {
        if (mActivity.isFinishing() || mActivity == null) {
            return;
        }
        SwipeBackUtil.closeKeyboard(mActivity);
        mActivity.finish();
        executeSwipeBackAnim();
    }

    public interface Delegate {
        /**
         * 姝ｅ湪婊戝姩杩斿洖
         *
         * @param slideOffset 浠?0 鍒?1
         */
        void onSwipeBackLayoutSlide(float slideOffset);

        /**
         * 娌¤揪鍒版粦鍔ㄨ繑鍥炵殑闃堝€硷紝鍙栨秷婊戝姩杩斿洖鍔ㄤ綔锛屽洖鍒伴粯璁ょ姸鎬?         */
        void onSwipeBackLayoutCancel();

        /**
         * 婊戝姩杩斿洖鎵ц瀹屾瘯锛岄攢姣佸綋鍓?Activity
         */
        void onSwipeBackLayoutExecuted(Activity activity);
    }
}

