package com.from.view.picture.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.RequiresApi;

import com.from.view.picture.bean.ImageFolder;
import com.from.view.picture.bean.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 鑾峰彇绯荤粺鐩稿唽涓殑鍥剧墖
 *
 * @author ym.li
 * @version 2.9.1
 * @since 2018骞?1鏈?2鏃?1:13:19
 */
public class ImageDataSource {
    /**
     * 浠嶴DCard鍔犺浇鍥剧墖
     *
     * @param context  涓婁笅鏂?     * @param callback DataCallback
     */
    public static void loadImageForSDCard(final Context context, final DataCallback callback) {
        //鐢变簬鎵弿鍥剧墖鏄€楁椂鐨勬搷浣滐紝鎵€浠ヨ鍦ㄥ瓙绾跨▼澶勭悊銆?        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                //鎵弿鍥剧墖
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = context.getContentResolver();
                Cursor mCursor = mContentResolver.query(mImageUri, new String[]{
                        MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.WIDTH,
                        MediaStore.Images.Media.HEIGHT,
                        MediaStore.Images.Media.SIZE,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.MIME_TYPE},
                    null,
                    null,
                    MediaStore.Images.Media.DATE_ADDED);
                ArrayList<ImageItem> images = new ArrayList<>();
                //璇诲彇鎵弿鍒扮殑鍥剧墖
                if (mCursor != null) {
                    while (mCursor.moveToNext()) {
                        // 鑾峰彇鍥剧墖鐨勮矾寰?                        String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        //鑾峰彇鍥剧墖鍚嶇О
                        String name = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                        //鑾峰彇鍥剧墖鏃堕棿
                        long time = mCursor.getLong(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                        //鑾峰彇鍥剧墖绫诲瀷
                        String mimeType = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
                        //鑾峰彇鍥剧墖瀹藉害
                        int width = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                        //鑾峰彇鍥剧墖瀹藉害
                        int height = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                        //鑾峰彇鍥剧墖鐨勫ぇ灏?                        int size = mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                        //杩囨护鏈笅杞藉畬鎴愭垨鑰呬笉瀛樺湪鐨勬枃浠?                        if (!"downloading".equals(getExtensionName(path)) && checkImgExists(path)) {
                            images.add(new ImageItem(path, time, name, mimeType, width, height, size));
                        }
                    }
                    mCursor.close();
                }
                Collections.reverse(images);
                callback.onSuccess(splitFolder(images));
            }
        }).start();
    }

    /**
     * Java鏂囦欢鎿嶄綔 鑾峰彇鏂囦欢鎵╁睍鍚?     */
    private static String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf('.');
            if (dot > -1 && dot < filename.length() - 1) {
                return filename.substring(dot + 1);
            }
        }
        return "";
    }

    /**
     * 妫€鏌ュ浘鐗囨槸鍚﹀瓨鍦ㄣ€侰ontentResolver鏌ヨ澶勭悊鐨勬暟鎹湁鍙兘鏂囦欢璺緞骞朵笉瀛樺湪銆?     *
     * @param filePath 鏂囦欢璺緞
     * @return 鏂囦欢澶规槸鍚﹀瓨鍦?     */
    private static boolean checkImgExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * 鎶婂浘鐗囨寜鏂囦欢澶规媶鍒嗭紝绗竴涓枃浠跺す淇濆瓨鎵€鏈夌殑鍥剧墖
     *
     * @param images 鍥剧墖鏂囦欢
     * @return 杩斿洖鍥剧墖鏂囦欢闆嗗悎
     */
    private static ArrayList<ImageFolder> splitFolder(ArrayList<ImageItem> images) {
        ArrayList<ImageFolder> folders = new ArrayList<>();
        folders.add(new ImageFolder("鍏ㄩ儴鍥剧墖", images));
        if (images != null && !images.isEmpty()) {
            int size = images.size();
            for (int i = 0; i < size; i++) {
                String path = images.get(i).path;
                String name = getFolderName(path);
                if (null != name && name.length() > 0) {
                    ImageFolder folder = getFolder(name, folders);
                    folder.addImage(images.get(i));
                }
            }
        }
        return folders;
    }

    /**
     * 鏍规嵁鍥剧墖璺緞锛岃幏鍙栧浘鐗囨枃浠跺す鍚嶇О
     *
     * @param path 鍥剧墖璺緞
     * @return 杩斿洖鏂囦欢澶硅矾寰?     */
    private static String getFolderName(String path) {
        if (null != path && path.length() > 0) {
            String[] strings = path.split(File.separator);
            if (strings.length >= 2) {
                return strings[strings.length - 2];
            }
        }
        return "";
    }

    private static ImageFolder getFolder(String name, List<ImageFolder> folders) {
        if (!folders.isEmpty()) {
            int size = folders.size();
            for (int i = 0; i < size; i++) {
                ImageFolder folder = folders.get(i);
                if (name.equals(folder.name)) {
                    return folder;
                }
            }
        }
        ImageFolder newFolder = new ImageFolder(name);
        folders.add(newFolder);
        return newFolder;
    }

    /**
     * 璧勬簮鍔犺浇鍥炶皟
     */
    public interface DataCallback {
        /**
         * 鑾峰彇鐩稿唽鍥剧墖璧勬簮鍥炶皟
         *
         * @param folders List
         */
        void onSuccess(ArrayList<ImageFolder> folders);
    }
}

