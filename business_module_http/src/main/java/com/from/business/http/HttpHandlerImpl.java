package com.from.business.http;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 灞曠ず {@link HttpHandler} 鐨勭敤娉? * <p>
 * ================================================
 */
public class HttpHandlerImpl implements HttpHandler {
    private Context context;

    public HttpHandlerImpl(Context context) {
        this.context = context;
    }

    /**
     * 杩欓噷鍙互鍏堝鎴风涓€姝ユ嬁鍒版瘡涓€娆?Http 璇锋眰鐨勭粨鏋? 鍙互鍏堣В鏋愭垚 Json, 鍐嶅仛涓€浜涙搷浣? 濡傛娴嬪埌 token 杩囨湡鍚?     * 閲嶆柊璇锋眰 token, 骞堕噸鏂版墽琛岃姹?     *
     * @param httpResult 鏈嶅姟鍣ㄨ繑鍥炵殑缁撴灉 (宸茶妗嗘灦鑷姩杞崲涓哄瓧绗︿覆)
     * @param chain      {@link Interceptor.Chain}
     * @param response   {@link Response}
     * @return {@link Response}
     */
    @NonNull
    @Override
    public Response onHttpResultResponse(@Nullable String httpResult, @NonNull Interceptor.Chain chain, @NonNull Response response) {
//        if (!TextUtils.isEmpty(httpResult) && RequestInterceptor.isJson(response.body().contentType())) {
//            try {
//                List<User> list = ArmsUtils.obtainAppComponent(context).gson().fromJson(httpResult, new TypeToken<List<User>>() {
//                }.getType());
//                User user = list.get(0);
//                LogUtils.debugLongInfo("Result ------> " + user.getLogin() + "    ||   Avatar_url------> " + user.getAvatarUrl());
//            } catch (Exception e) {
//                e.printStackTrace();
//                return response;
//            }
//        }

        /* 杩欓噷濡傛灉鍙戠幇 token 杩囨湡, 鍙互鍏堣姹傛渶鏂扮殑 token, 鐒跺悗鍦ㄦ嬁鏂扮殑 token 鏀惧叆 Request 閲屽幓閲嶆柊璇锋眰
        娉ㄦ剰鍦ㄨ繖涓洖璋冧箣鍓嶅凡缁忚皟鐢ㄨ繃 proceed(), 鎵€浠ヨ繖閲屽繀椤昏嚜宸卞幓寤虹珛缃戠粶璇锋眰, 濡備娇鐢?Okhttp 浣跨敤鏂扮殑 Request 鍘昏姹?        create a new request and modify it accordingly using the new token
        Request newRequest = chain.request().newBuilder().header("token", newToken)
                             .build();

        retry the request

        response.body().close();
        濡傛灉浣跨敤 Okhttp 灏嗘柊鐨勮姹? 璇锋眰鎴愬姛鍚? 鍐嶅皢 Okhttp 杩斿洖鐨?Response return 鍑哄幓鍗冲彲
        濡傛灉涓嶉渶瑕佽繑鍥炴柊鐨勭粨鏋? 鍒欑洿鎺ユ妸鍙傛暟 response 杩斿洖鍑哄幓鍗冲彲*/
        return response;
    }

    /**
     * 杩欓噷鍙互鍦ㄨ姹傛湇鍔″櫒涔嬪墠鎷垮埌 {@link Request}, 鍋氫竴浜涙搷浣滄瘮濡傜粰 {@link Request} 缁熶竴娣诲姞 token 鎴栬€?header 浠ュ強鍙傛暟鍔犲瘑绛夋搷浣?     *
     * @param chain   {@link Interceptor.Chain}
     * @param request {@link Request}
     * @return {@link Request}
     */
    @NonNull
    @Override
    public Request onHttpRequestBefore(@NonNull Interceptor.Chain chain, @NonNull Request request) {
        /* 濡傛灉闇€瑕佸湪璇锋眰鏈嶅姟鍣ㄤ箣鍓嶅仛涓€浜涙搷浣? 鍒欓噸鏂版瀯寤轰竴涓仛杩囨搷浣滅殑 Request 骞?return, 濡傚鍔?Header銆丳arams 绛夎姹備俊鎭? 涓嶅仛鎿嶄綔鍒欑洿鎺ヨ繑鍥炲弬鏁?request
        return chain.request().newBuilder().header("token", tokenId)
                              .build(); */
        return request;
    }
}

