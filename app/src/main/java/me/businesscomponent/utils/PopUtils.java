package me.businesscomponent.utils;

import android.app.Activity;
import android.content.Context;
import android.icu.util.VersionInfo;
import androidx.fragment.app.FragmentManager;

import me.businesscomponent.view.TipDialog;

/**
 * 寮圭獥绠＄悊绫? *
 * @author : ym.li
 * @version : v2.6.0
 * @since : 2018/9/3/003
 */
public class PopUtils {
    private PopUtils() {

    }

    public static TipDialog getTipDialog(Context context, boolean cancelable) {
        return new TipDialog.Builder(context)
            .setIconType(TipDialog.Builder.ICON_TYPE_LOADING)
            .create(cancelable);
    }

    /**
     * 鑾峰彇鑿婅姳褰㈢姸Dialog
     *
     * @param context 涓婁笅鏂?     * @return 杩斿洖TipDialog
     */
    public static TipDialog getTipDialog(Context context) {
        return getTipDialog(context, true);
    }

    /**
     * 鑾峰彇甯︽枃瀛楁彁绀虹殑鑿婅姳褰㈢姸Dialog
     *
     * @param context  涓婁笅鏂?     * @param word     鎻愮ず鏂囧瓧
     * @param iconType icon绫诲瀷
     * @return 杩斿洖TipDialog
     */
    public static TipDialog getTipDialog(Context context, CharSequence word, @TipDialog.Builder.IconType int iconType) {
        return new TipDialog
            .Builder(context)
            .setIconType(iconType)
            .setTipWord(word)
            .create();
    }
}

