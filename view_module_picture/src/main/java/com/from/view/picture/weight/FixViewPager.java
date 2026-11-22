package com.from.view.picture.weight;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 鍦ㄤ娇鐢╬hotoView缂╂斁鏃堕伩鍏嶅紓甯哥殑鍑虹幇
 * <p>
 * author: ym.li
 * since: 2018/11/3
 */

public class FixViewPager extends ViewPager {
    public FixViewPager(@NonNull Context context) {
        this(context, null);
    }

    public FixViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //uncomment if you really want to see these errors
            //e.printStackTrace();
            return false;
        }
    }
}

