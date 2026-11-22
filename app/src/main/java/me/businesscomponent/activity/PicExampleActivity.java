package me.businesscomponent.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import com.from.view.picture.PictureSelector;
import com.from.view.picture.imageloader.GlideImageLoader;

import java.util.List;

import me.businesscomponent.R;
import me.businesscomponent.utils.PopUtils;
import me.businesscomponent.view.TipDialog;

import static me.businesscomponent.ConstantsPermission.CAMERA;

/**
 * @author Vea
 * @since 2019-01-16
 */
public class PicExampleActivity extends AppCompatActivity {
    // need implements ISwipeBack
    //    @Override
//    public boolean isEnableGesture() {
//        // 閫氳繃杩斿洖鍊兼帶鍒舵槸鍚﹀彲浠ヤ晶婊?//        return false;
//    }
    private static final int SELECT_IMAGE = 0x006F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);

        findViewById(R.id.btn)
            .setOnClickListener(v -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                CAMERA);
                        } else {
                            PictureSelector.with(PicExampleActivity.this)
                                .selectSpec()
                                .setMaxSelectImage(6)
                                .setOpenCamera(true)
                                .setImageLoader(new GlideImageLoader())
                                .setSpanCount(3)
                                .startForResult(SELECT_IMAGE);
                        }
                    }
                }
            );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_IMAGE) {
            List<String> paths = PictureSelector.obtainPathResult(data);
            String pathsStr = "";
            for (String path : paths) {
                pathsStr += path;
            }
            PopUtils.getTipDialog(this, pathsStr, TipDialog.Builder.ICON_TYPE_SUCCESS).show();
        }
    }
}

