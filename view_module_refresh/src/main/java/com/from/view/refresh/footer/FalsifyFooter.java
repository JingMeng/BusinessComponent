package com.from.view.refresh.footer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.from.view.refresh.R;
import com.from.view.refresh.api.RefreshFooter;
import com.from.view.refresh.api.RefreshKernel;
import com.from.view.refresh.api.RefreshLayout;
import com.from.view.refresh.constant.RefreshState;
import com.from.view.refresh.internal.InternalAbstract;
import com.from.view.refresh.util.DensityUtil;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * 铏氬亣鐨?Footer
 * 鐢ㄤ簬 姝ｇ湡鐨?Footer 鍦?RefreshLayout 澶栭儴鏃讹紝
 * Created by SCWANG on 2017/6/14.
 */
@SuppressWarnings("unused")
public class FalsifyFooter extends InternalAbstract implements RefreshFooter {

    private RefreshKernel mRefreshKernel;

    //<editor-fold desc="FalsifyHeader">
    public FalsifyFooter(Context context) {
        this(context, null);
    }

    public FalsifyFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FalsifyFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        final View thisView = this;
        if (thisView.isInEditMode()) {//杩欐浠ｇ爜鍦ㄨ繍琛屾椂涓嶄細鎵ц锛屽彧浼氬湪Studio缂栬緫棰勮鏃惰繍琛岋紝涓嶇敤鍦ㄦ剰鎬ц兘闂
            int d = DensityUtil.dp2px(5);

            Paint paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(0xcccccccc);
            paint.setStrokeWidth(DensityUtil.dp2px(1));
            paint.setPathEffect(new DashPathEffect(new float[]{d, d, d, d}, 1));
            canvas.drawRect(d, d, thisView.getWidth() - d, thisView.getBottom() - d, paint);

            TextView textView = new TextView(thisView.getContext());
            textView.setText(thisView.getResources().getString(R.string.srl_component_falsify, getClass().getSimpleName(), DensityUtil.px2dp(thisView.getHeight())));
            textView.setTextColor(0xcccccccc);
            textView.setGravity(Gravity.CENTER);
            //noinspection UnnecessaryLocalVariable
            View view = textView;
            view.measure(makeMeasureSpec(thisView.getWidth(), EXACTLY), makeMeasureSpec(thisView.getHeight(), EXACTLY));
            view.layout(0, 0, thisView.getWidth(), thisView.getHeight());
            view.draw(canvas);
        }
    }
    //</editor-fold>

    //<editor-fold desc="RefreshFooter">

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {
        mRefreshKernel = kernel;
        kernel.getRefreshLayout().setEnableAutoLoadMore(false);
    }

    @Override
    public void onReleased(@NonNull RefreshLayout layout, int height, int maxDragHeight) {
        if (mRefreshKernel != null) {
            mRefreshKernel.setState(RefreshState.None);
            //onReleased 鐨勬椂鍊?璋冪敤 setState(RefreshState.None); 骞朵笉浼氱珛鍒绘敼鍙樻垚 None
            //鑰屾槸鍏堟墽琛屼竴涓洖寮瑰姩鐢伙紝LoadFinish 鏄粙浜?Refreshing 鍜?None 涔嬮棿鐨勭姸鎬?            //LoadFinish 鐢ㄤ簬鍦ㄥ洖寮瑰姩鐢荤粨鏉熸椂鍊欒兘椤哄埄鏀瑰彉涓?None
            mRefreshKernel.setState(RefreshState.LoadFinish);
        }
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return false;
    }

    //</editor-fold>

}

