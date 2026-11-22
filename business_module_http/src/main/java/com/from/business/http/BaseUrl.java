package com.from.business.http;

import androidx.annotation.NonNull;

import okhttp3.HttpUrl;

/**
 * ================================================
 * 閽堝浜?BaseUrl 鍦?App 鍚姩鏃朵笉鑳界‘瀹?闇€瑕佽姹傛湇鍔″櫒鎺ュ彛鍔ㄦ€佽幏鍙栫殑搴旂敤鍦烘櫙
 * <p>
 * Created by JessYan on 11/07/2017 14:58
 * ================================================
 */
public interface BaseUrl {
    /**
     * 鍦ㄨ皟鐢?Retrofit API 鎺ュ彛涔嬪墠,浣跨敤 Okhttp 鎴栧叾浠栨柟寮?璇锋眰鍒版纭殑 BaseUrl 骞堕€氳繃姝ゆ柟娉曡繑鍥?     *
     * @return
     */
    @NonNull
    HttpUrl url();
}

