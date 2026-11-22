package com.from.view.picture;

import android.app.Activity;
import android.content.Intent;
import androidx.fragment.app.Fragment;

import com.from.view.picture.imageloader.GlideImageLoader;
import com.from.view.picture.imageloader.UIImageLoader;
import com.from.view.picture.ui.SelectImageActivity;

/**
 * Doc  瀵归€変腑鐨勫睘鎬ц繘琛屽寘瑁? *
 * @author ym.li
 * @version 2.9.0
 * @since 2018/11/2/002
 */
public final class SelectorSpec {
    private static final int CROP_SIZE = 200;
    //鍓X杞存瘮渚?    private int mAspectX;
    //鍓Y杞存瘮渚?    private int mAspectY;
    //鍓X澶у皬
    private int mOutputX;
    //鍓Ya澶у皬
    private int mOutputY;
    //鏈€澶氶€変腑鍥剧墖鏁伴噺
    private int mMaxSelectImage;
    //鍥剧墖涓€琛屽睍绀哄嚑寮?    private int mSpanCount;
    //榛樿鍥剧墖鍔犺浇鏂瑰紡
    private boolean mIsOpenCamera;
    private UIImageLoader mImageLoader;
    //鏄惁寮€鍚壀瑁?    private boolean mNeedCrop;
    /**
     * 閫夋嫨鏈€澶?     */
    private long mMaxLength;
    private PictureSelector mPictureSelector;

    private SelectorSpec() {
    }

    public static SelectorSpec getCleanInstance() {
        SelectorSpec selectorSpec = getInstance();
        selectorSpec.resetSpec();
        return selectorSpec;
    }

    public static SelectorSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private void resetSpec() {
        this.mSpanCount = 3;
        this.mAspectX = 1;
        this.mAspectY = 1;
        this.mOutputX = CROP_SIZE;
        this.mOutputY = CROP_SIZE;
        this.mMaxSelectImage = 1;
        this.mIsOpenCamera = false;
        this.mImageLoader = new GlideImageLoader();
    }


    public int getAspectX() {
        return mAspectX;
    }

    public SelectorSpec setAspectX(int aspectX) {
        this.mAspectX = aspectX;
        return this;
    }

    public int getAspectY() {
        return mAspectY;
    }

    public SelectorSpec setAspectY(int aspectY) {
        this.mAspectY = aspectY;
        return this;
    }

    public int getOutputX() {
        return mOutputX;
    }

    public SelectorSpec setOutputX(int outputX) {
        this.mOutputX = outputX;
        return this;
    }

    public int getOutputY() {
        return mOutputY;
    }

    public SelectorSpec setOutputY(int outputY) {
        this.mOutputY = outputY;
        return this;
    }

    public boolean isNeedCrop() {
        return mNeedCrop;
    }

    public SelectorSpec setNeedCrop(boolean needCrop) {
        this.mNeedCrop = needCrop;
        return this;
    }

    public SelectorSpec withPictureSelector(PictureSelector pictureSelector) {
        this.mPictureSelector = pictureSelector;
        return this;
    }

    public int getMaxSelectImage() {
        return mMaxSelectImage;
    }

    public long getMaxLength() {
        return mMaxLength;
    }

    public SelectorSpec setMaxSelectImage(int maxSelectImage) {
        this.mMaxSelectImage = maxSelectImage;
        return this;
    }

    public boolean singleImage() {
        return mMaxSelectImage == 1;
    }

    public int getSpanCount() {
        return mSpanCount;
    }

    public SelectorSpec setSpanCount(int spanCount) {
        this.mSpanCount = spanCount;
        return this;
    }

    public boolean isOpenCamera() {
        return mIsOpenCamera;
    }

    public SelectorSpec setOpenCamera(boolean openCamera) {
        this.mIsOpenCamera = openCamera;
        return this;
    }

    public SelectorSpec setMaxLength(long length) {
        this.mMaxLength = length;
        return this;
    }

    public UIImageLoader getImageLoader() {
        return mImageLoader;
    }


    public SelectorSpec setImageLoader(UIImageLoader imageLoader) {
        mImageLoader = imageLoader;
        return this;
    }

    public void startForResult(int request) {
        Activity activity = mPictureSelector.getActivity();
        if (null == activity) {
            return;
        }
        Intent intent = new Intent(activity, SelectImageActivity.class);
        Fragment fragment = mPictureSelector.getFragment();
        if (null != fragment) {
            fragment.startActivityForResult(intent, request);
        } else {
            activity.startActivityForResult(intent, request);
        }
    }

    private static final class InstanceHolder {
        private static final SelectorSpec INSTANCE = new SelectorSpec();
    }
}

