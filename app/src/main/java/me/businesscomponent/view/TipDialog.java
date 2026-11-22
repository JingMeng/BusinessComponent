package me.businesscomponent.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import me.businesscomponent.R;
import me.businesscomponent.utils.ScreenUtils;

/**
 * 涓€浜涘熀鏈牱寮忕殑dialog
 *
 * @author ym.li
 * @version 1.2.0
 * @since 2018/7/28/028
 */
public class TipDialog extends Dialog {
    private static final int LOADING_SIZE = 30;
    private static final float TIP_WORD_TOP_MARGIN = 12;
    private static final float TIP_WORD_TEXT_SIZE = 14;
    private static final int DIALOG_MIN_SIZE = 90;

    /**
     * 鏋勯€犲櫒
     *
     * @param context 涓婁笅鏂?     */
    public TipDialog(Context context) {
        this(context, R.style.TipDialog);
    }

    /**
     * 鏋勯€犲櫒
     *
     * @param context    涓婁笅鏂?     * @param themeResId 涓婚璩囨簮
     */
    public TipDialog(Context context, int themeResId) {
        super(context, themeResId);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDialogWidth();
    }

    private void initDialogWidth() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams wmLp = window.getAttributes();
            wmLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(wmLp);
        }
    }

    /**
     * 鐢熸垚榛樿鐨?{@link TipDialog}
     * <p>
     * 鎻愪緵浜嗕竴涓浘鏍囧拰涓€琛屾枃瀛楃殑鏍峰紡, 鍏朵腑鍥炬爣鏈夊嚑绉嶇被鍨嬪彲閫夈€傝 {@link IconType}
     * </p>
     *
     * @see CustomBuilder
     */
    public static class Builder {
        /**
         * 涓嶆樉绀轰换浣昳con
         */
        public static final int ICON_TYPE_NOTHING = 0;
        /**
         * 鏄剧ず Loading 鍥炬爣
         */
        public static final int ICON_TYPE_LOADING = 1;
        /**
         * 鏄剧ず鎴愬姛鍥炬爣
         */
        public static final int ICON_TYPE_SUCCESS = 2;
        /**
         * 鏄剧ず澶辫触鍥炬爣
         */
        public static final int ICON_TYPE_FAIL = 3;
        /**
         * 鏄剧ず淇℃伅鍥炬爣
         */
        public static final int ICON_TYPE_INFO = 4;

        /**
         * 闄愬畾绫诲瀷
         */
        @IntDef({ICON_TYPE_NOTHING, ICON_TYPE_LOADING, ICON_TYPE_SUCCESS, ICON_TYPE_FAIL, ICON_TYPE_INFO})
        @Retention(RetentionPolicy.SOURCE)
        public @interface IconType {
        }

        private @IconType
        int mCurrentIconType = ICON_TYPE_NOTHING;

        private Context mContext;

        private CharSequence mTipWord;

        /**
         * 鏋勯€犲櫒
         *
         * @param context 涓婁笅鏂?         */
        public Builder(Context context) {
            mContext = context;
        }

        /**
         * 璁剧疆 icon 鏄剧ず鐨勫唴瀹?         *
         * @param iconType icon绫诲瀷
         * @return 褰撳墠绫?         */
        public Builder setIconType(@IconType int iconType) {
            mCurrentIconType = iconType;
            return this;
        }

        /**
         * 璁剧疆鏄剧ず鐨勬枃妗?         *
         * @param tipWord 鎻愮ず鏂囨
         * @return 褰撳墠绫?         */
        public Builder setTipWord(CharSequence tipWord) {
            mTipWord = tipWord;
            return this;
        }

        /**
         * 鍒涘缓TipDialog
         *
         * @return 杩斿洖TipDialog
         */
        public TipDialog create() {
            return create(true);
        }

        /**
         * 鍒涘缓 Dialog, 浣嗘病鏈夊脊鍑烘潵, 濡傛灉瑕佸脊鍑烘潵, 璇疯皟鐢ㄨ繑鍥炲€肩殑 {@link Dialog#show()} 鏂规硶
         *
         * @param cancelable 鎸夌郴缁熻繑鍥為敭鏄惁鍙互鍙栨秷
         * @return 鍒涘缓鐨?Dialog
         */
        public TipDialog create(boolean cancelable) {
            TipDialog dialog = new TipDialog(mContext);
            dialog.setCancelable(cancelable);
            dialog.setContentView(R.layout.tip_dialog_layout);
            ViewGroup contentWrap = dialog.findViewById(R.id.contentWrap);
            if (mCurrentIconType == ICON_TYPE_LOADING) {
                LoadingView loadingView = new LoadingView(mContext);
                loadingView.setColor(Color.WHITE);
                loadingView.setSize(ScreenUtils.dip2px(LOADING_SIZE));
                LinearLayout.LayoutParams loadingViewLP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                loadingView.setLayoutParams(loadingViewLP);
                contentWrap.addView(loadingView);
            } else if (mCurrentIconType == ICON_TYPE_SUCCESS || mCurrentIconType == ICON_TYPE_FAIL || mCurrentIconType == ICON_TYPE_INFO) {
                ImageView imageView = new ImageView(mContext);
                LinearLayout.LayoutParams imageViewLP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(imageViewLP);
                if (mCurrentIconType == ICON_TYPE_SUCCESS) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_notify_done));
                } else if (mCurrentIconType == ICON_TYPE_FAIL) {
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_notify_error));
                } else {
                    imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.icon_notify_info));
                }
                contentWrap.addView(imageView);
            }
            if (mTipWord != null && mTipWord.length() > 0) {
                TextView tipView = new TextView(mContext);
                LinearLayout.LayoutParams tipViewLP = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                if (mCurrentIconType != ICON_TYPE_NOTHING) {
                    tipViewLP.topMargin = ScreenUtils.dip2px(TIP_WORD_TOP_MARGIN);
                }
                tipView.setLayoutParams(tipViewLP);
                tipView.setEllipsize(TextUtils.TruncateAt.END);
                tipView.setGravity(Gravity.CENTER);
                tipView.setMaxLines(2);
                tipView.setTextColor(ContextCompat.getColor(mContext, android.R.color.white));
                tipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TIP_WORD_TEXT_SIZE);
                tipView.setText(mTipWord);
                contentWrap.setMinimumWidth(ScreenUtils.dip2px(DIALOG_MIN_SIZE));
                contentWrap.setMinimumHeight(ScreenUtils.dip2px(DIALOG_MIN_SIZE));
                contentWrap.addView(tipView);
            } else {
                contentWrap.setMinimumWidth(0);
                contentWrap.setMinimumHeight(0);
            }
            return dialog;
        }
    }

    /**
     * 浼犲叆鑷畾涔夌殑甯冨眬骞朵娇鐢ㄨ繖涓竷灞€鐢熸垚 TipDialog
     */
    public static class CustomBuilder {
        private Context mContext;
        private int mContentLayoutId;

        /**
         * 鏋勯€犲櫒
         *
         * @param context 涓婁笅鏂?         */
        public CustomBuilder(Context context) {
            mContext = context;
        }

        /**
         * 鑷畾涔夊竷灞€id
         *
         * @param layoutId 甯冨眬鏂囦欢id
         * @return 杩斿洖褰撳墠绫?         */
        public CustomBuilder setContent(@LayoutRes int layoutId) {
            mContentLayoutId = layoutId;
            return this;
        }

        /**
         * 鍒涘缓 Dialog, 浣嗘病鏈夊脊鍑烘潵, 濡傛灉瑕佸脊鍑烘潵, 璇疯皟鐢ㄨ繑鍥炲€肩殑 {@link Dialog#show()} 鏂规硶
         *
         * @return 鍒涘缓鐨?Dialog
         */
        public TipDialog create() {
            TipDialog dialog = new TipDialog(mContext);
            dialog.setContentView(R.layout.tip_dialog_layout);
            ViewGroup contentWrap = dialog.findViewById(R.id.contentWrap);
            LayoutInflater.from(mContext).inflate(mContentLayoutId, contentWrap, true);
            return dialog;
        }
    }
}

