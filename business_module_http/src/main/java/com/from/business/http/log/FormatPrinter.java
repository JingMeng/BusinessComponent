package com.from.business.http.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.from.business.http.module.http.HttpConfigModule;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;

/**
 * ================================================
 * 瀵?OkHttp 鐨勮姹傚拰鍝嶅簲淇℃伅杩涜鏇磋鑼冨拰娓呮櫚鐨勬墦鍗? 寮€鍙戣€呭彲鏇存牴鎹嚜宸辩殑闇€姹傝嚜琛屾墿灞曟墦鍗版牸寮? *
 * @see DefaultFormatPrinter
 * @see HttpConfigModule.Builder#formatPrinter(FormatPrinter)
 * ================================================
 */
public interface FormatPrinter {
    /**
     * 鎵撳嵃缃戠粶璇锋眰淇℃伅, 褰撶綉缁滆姹傛椂 {{@link okhttp3.RequestBody}} 鍙互瑙ｆ瀽鐨勬儏鍐?     *
     * @param request
     * @param bodyString 鍙戦€佺粰鏈嶅姟鍣ㄧ殑璇锋眰浣撲腑鐨勬暟鎹?宸茶В鏋?
     */
    void printJsonRequest(@NonNull Request request, @NonNull String bodyString);

    /**
     * 鎵撳嵃缃戠粶璇锋眰淇℃伅, 褰撶綉缁滆姹傛椂 {{@link okhttp3.RequestBody}} 涓?{@code null} 鎴栦笉鍙В鏋愮殑鎯呭喌
     *
     * @param request
     */
    void printFileRequest(@NonNull Request request);

    /**
     * 鎵撳嵃缃戠粶鍝嶅簲淇℃伅, 褰撶綉缁滃搷搴旀椂 {{@link okhttp3.ResponseBody}} 鍙互瑙ｆ瀽鐨勬儏鍐?     *
     * @param chainMs      鏈嶅姟鍣ㄥ搷搴旇€楁椂(鍗曚綅姣)
     * @param isSuccessful 璇锋眰鏄惁鎴愬姛
     * @param code         鍝嶅簲鐮?     * @param headers      璇锋眰澶?     * @param contentType  鏈嶅姟鍣ㄨ繑鍥炴暟鎹殑鏁版嵁绫诲瀷
     * @param bodyString   鏈嶅姟鍣ㄨ繑鍥炵殑鏁版嵁(宸茶В鏋?
     * @param segments     鍩熷悕鍚庨潰鐨勮祫婧愬湴鍧€
     * @param message      鍝嶅簲淇℃伅
     * @param responseUrl  璇锋眰鍦板潃
     */
    void printJsonResponse(long chainMs, boolean isSuccessful, int code, @NonNull String headers, @Nullable MediaType contentType,
        @Nullable String bodyString, @NonNull List<String> segments, @NonNull String message, @NonNull String responseUrl);

    /**
     * 鎵撳嵃缃戠粶鍝嶅簲淇℃伅, 褰撶綉缁滃搷搴旀椂 {{@link okhttp3.ResponseBody}} 涓?{@code null} 鎴栦笉鍙В鏋愮殑鎯呭喌
     *
     * @param chainMs      鏈嶅姟鍣ㄥ搷搴旇€楁椂(鍗曚綅姣)
     * @param isSuccessful 璇锋眰鏄惁鎴愬姛
     * @param code         鍝嶅簲鐮?     * @param headers      璇锋眰澶?     * @param segments     鍩熷悕鍚庨潰鐨勮祫婧愬湴鍧€
     * @param message      鍝嶅簲淇℃伅
     * @param responseUrl  璇锋眰鍦板潃
     */
    void printFileResponse(long chainMs, boolean isSuccessful, int code, @NonNull String headers,
        @NonNull List<String> segments, @NonNull String message, @NonNull String responseUrl);
}

