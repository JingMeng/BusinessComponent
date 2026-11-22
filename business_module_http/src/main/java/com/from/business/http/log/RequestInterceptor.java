package com.from.business.http.log;

import androidx.annotation.Nullable;

import com.from.business.http.HttpHandler;
import com.from.business.http.module.http.HttpConfigModule;
import com.from.business.http.utils.CharacterHandler;
import com.from.business.http.utils.LogUtils;
import com.from.business.http.utils.UrlEncoderUtils;
import com.from.business.http.utils.ZipHelper;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * ================================================
 * 瑙ｆ瀽妗嗘灦涓殑缃戠粶璇锋眰鍜屽搷搴旂粨鏋?骞朵互鏃ュ織褰㈠紡杈撳嚭,璋冭瘯绁炲櫒
 * 鍙娇鐢?{@link HttpConfigModule.Builder#printHttpLogLevel(Level)} 鎺у埗鎴栧叧闂棩蹇? * <p>
 * ================================================
 */
public class RequestInterceptor implements Interceptor {
    @Nullable
    HttpHandler mHandler;
    FormatPrinter mPrinter;
    Level printLevel;

    public enum Level {
        /**
         * 涓嶆墦鍗發og
         */
        NONE,
        /**
         * 鍙墦鍗拌姹備俊鎭?         */
        REQUEST,
        /**
         * 鍙墦鍗板搷搴斾俊鎭?         */
        RESPONSE,
        /**
         * 鎵€鏈夋暟鎹叏閮ㄦ墦鍗?         */
        ALL
    }

    public RequestInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        boolean logRequest = printLevel == Level.ALL || (printLevel != Level.NONE && printLevel == Level.REQUEST);

        if (logRequest) {
            //鎵撳嵃璇锋眰淇℃伅
            if (request.body() != null && isParseable(request.body().contentType())) {
                mPrinter.printJsonRequest(request, parseParams(request));
            } else {
                mPrinter.printFileRequest(request);
            }
        }

        boolean logResponse = printLevel == Level.ALL || (printLevel != Level.NONE && printLevel == Level.RESPONSE);

        long t1 = logResponse ? System.nanoTime() : 0;
        Response originalResponse;
        try {
            originalResponse = chain.proceed(request);
        } catch (Exception e) {
            LogUtils.debugInfo("Http Error: " + e);
            throw e;
        }
        long t2 = logResponse ? System.nanoTime() : 0;

        ResponseBody responseBody = originalResponse.body();

        //鎵撳嵃鍝嶅簲缁撴灉
        String bodyString = null;
        if (responseBody != null && isParseable(responseBody.contentType())) {
            bodyString = printResult(request, originalResponse, logResponse);
        }

        if (logResponse) {
            final List<String> segmentList = request.url().encodedPathSegments();
            final String header = originalResponse.headers().toString();
            final int code = originalResponse.code();
            final boolean isSuccessful = originalResponse.isSuccessful();
            final String message = originalResponse.message();
            final String url = originalResponse.request().url().toString();

            if (responseBody != null && isParseable(responseBody.contentType())) {
                mPrinter.printJsonResponse(TimeUnit.NANOSECONDS.toMillis(t2 - t1), isSuccessful,
                    code, header, responseBody.contentType(), bodyString, segmentList, message, url);
            } else {
                mPrinter.printFileResponse(TimeUnit.NANOSECONDS.toMillis(t2 - t1),
                    isSuccessful, code, header, segmentList, message, url);
            }
        }

        if (mHandler != null)//杩欓噷鍙互姣斿鎴风鎻愬墠涓€姝ユ嬁鍒版湇鍔″櫒杩斿洖鐨勭粨鏋?鍙互鍋氫竴浜涙搷浣?姣斿token瓒呮椂,閲嶆柊鑾峰彇
            return mHandler.onHttpResultResponse(bodyString, chain, originalResponse);

        return originalResponse;
    }

    /**
     * 鎵撳嵃鍝嶅簲缁撴灉
     *
     * @param request     {@link Request}
     * @param response    {@link Response}
     * @param logResponse 鏄惁鎵撳嵃鍝嶅簲缁撴灉
     * @return 瑙ｆ瀽鍚庣殑鍝嶅簲缁撴灉
     */
    @Nullable
    private String printResult(Request request, Response response, boolean logResponse) {
        try {
            //璇诲彇鏈嶅姟鍣ㄨ繑鍥炵殑缁撴灉
            ResponseBody responseBody = response.newBuilder().build().body();
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            //鑾峰彇content鐨勫帇缂╃被鍨?            String encoding = response
                .headers()
                .get("Content-Encoding");

            Buffer clone = buffer.clone();

            //瑙ｆ瀽response content
            return parseContent(responseBody, encoding, clone);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 瑙ｆ瀽鏈嶅姟鍣ㄥ搷搴旂殑鍐呭
     *
     * @param responseBody {@link ResponseBody}
     * @param encoding     缂栫爜绫诲瀷
     * @param clone        鍏嬮殕鍚庣殑鏈嶅姟鍣ㄥ搷搴斿唴瀹?     * @return 瑙ｆ瀽鍚庣殑鍝嶅簲缁撴灉
     */
    private String parseContent(ResponseBody responseBody, String encoding, Buffer clone) {
        Charset charset = Charset.forName("UTF-8");
        MediaType contentType = responseBody.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {//content 浣跨敤 gzip 鍘嬬缉
            return ZipHelper.decompressForGzip(clone.readByteArray(), convertCharset(charset));//瑙ｅ帇
        } else if (encoding != null && encoding.equalsIgnoreCase("zlib")) {//content 浣跨敤 zlib 鍘嬬缉
            return ZipHelper.decompressToStringForZlib(clone.readByteArray(), convertCharset(charset));//瑙ｅ帇
        } else {//content 娌℃湁琚帇缂? 鎴栬€呬娇鐢ㄥ叾浠栨湭鐭ュ帇缂╂柟寮?            return clone.readString(charset);
        }
    }

    /**
     * 瑙ｆ瀽璇锋眰鏈嶅姟鍣ㄧ殑璇锋眰鍙傛暟
     *
     * @param request {@link Request}
     * @return 瑙ｆ瀽鍚庣殑璇锋眰淇℃伅
     */
    public static String parseParams(Request request) {
        try {
            RequestBody body = request.newBuilder().build().body();
            if (body == null) return "";
            Buffer requestbuffer = new Buffer();
            body.writeTo(requestbuffer);
            Charset charset = Charset.forName("UTF-8");
            MediaType contentType = body.contentType();
            if (contentType != null) {
                charset = contentType.charset(charset);
            }
            String json = requestbuffer.readString(charset);
            if (UrlEncoderUtils.hasUrlEncoded(json)) {
                json = URLDecoder.decode(json, convertCharset(charset));
            }
            return CharacterHandler.jsonFormat(json);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 鏄惁鍙互瑙ｆ瀽
     *
     * @param mediaType {@link MediaType}
     * @return {@code true} 涓哄彲浠ヨВ鏋?     */
    public static boolean isParseable(MediaType mediaType) {
        if (mediaType == null || mediaType.type() == null) return false;
        return isText(mediaType) || isPlain(mediaType)
            || isJson(mediaType) || isForm(mediaType)
            || isHtml(mediaType) || isXml(mediaType);
    }

    public static boolean isText(MediaType mediaType) {
        if (mediaType == null || mediaType.type() == null) return false;
        return mediaType.type().equals("text");
    }

    public static boolean isPlain(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("plain");
    }

    public static boolean isJson(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("json");
    }

    public static boolean isXml(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("xml");
    }

    public static boolean isHtml(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("html");
    }

    public static boolean isForm(MediaType mediaType) {
        if (mediaType == null || mediaType.subtype() == null) return false;
        return mediaType.subtype().toLowerCase().contains("x-www-form-urlencoded");
    }

    public static String convertCharset(Charset charset) {
        String s = charset.toString();
        int i = s.indexOf("[");
        if (i == -1)
            return s;
        return s.substring(i + 1, s.length() - 1);
    }
}

