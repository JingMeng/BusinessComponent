package com.from.business.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.from.business.http.module.http.HttpConfigModule;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 澶勭悊 Http 璇锋眰鍜屽搷搴旂粨鏋滅殑澶勭悊绫? * 浣跨敤 {@link HttpConfigModule.Builder#globalHttpHandler(HttpHandler)} 鏂规硶閰嶇疆
 * <p>
 * ================================================
 */
public interface HttpHandler {
    /**
     * 杩欓噷鍙互鍏堝鎴风涓€姝ユ嬁鍒版瘡涓€娆?Http 璇锋眰鐨勭粨鏋? 鍙互鍏堣В鏋愭垚 Json, 鍐嶅仛涓€浜涙搷浣? 濡傛娴嬪埌 token 杩囨湡鍚?     * 閲嶆柊璇锋眰 token, 骞堕噸鏂版墽琛岃姹?     *
     * @param httpResult 鏈嶅姟鍣ㄨ繑鍥炵殑缁撴灉 (宸茶妗嗘灦鑷姩杞崲涓哄瓧绗︿覆)
     * @param chain      {@link Interceptor.Chain}
     * @param response   {@link Response}
     * @return {@link Response}
     */
    @NonNull
    Response onHttpResultResponse(@Nullable String httpResult, @NonNull Interceptor.Chain chain, @NonNull Response response);

    /**
     * 杩欓噷鍙互鍦ㄨ姹傛湇鍔″櫒涔嬪墠鎷垮埌 {@link Request}, 鍋氫竴浜涙搷浣滄瘮濡傜粰 {@link Request} 缁熶竴娣诲姞 token 鎴栬€?header 浠ュ強鍙傛暟鍔犲瘑绛夋搷浣?     *
     * @param chain   {@link Interceptor.Chain}
     * @param request {@link Request}
     * @return {@link Request}
     */
    @NonNull
    Request onHttpRequestBefore(@NonNull Interceptor.Chain chain, @NonNull Request request);

    /**
     * 绌哄疄鐜?     */
    HttpHandler EMPTY = new HttpHandler() {
        @NonNull
        @Override
        public Response onHttpResultResponse(@Nullable String httpResult, @NonNull Interceptor.Chain chain, @NonNull Response response) {
            //涓嶇鏄惁澶勭悊, 閮藉繀椤诲皢 response 杩斿洖鍑哄幓
            return response;
        }

        @NonNull
        @Override
        public Request onHttpRequestBefore(@NonNull Interceptor.Chain chain, @NonNull Request request) {
            //涓嶇鏄惁澶勭悊, 閮藉繀椤诲皢 request 杩斿洖鍑哄幓
            return request;
        }
    };
}

