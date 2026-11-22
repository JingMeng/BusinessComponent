package com.from.business.http.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.from.business.http.module.http.HttpConfigModule;
import com.from.business.http.utils.CharacterHandler;
import com.from.business.http.utils.LogUtils;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.Request;

/**
 * ================================================
 * 瀵?OkHttp 鐨勮姹傚拰鍝嶅簲淇℃伅杩涜鏇磋鑼冨拰娓呮櫚鐨勬墦鍗? 姝ょ被涓烘鏋堕粯璁ゅ疄鐜? 浠ラ粯璁ゆ牸寮忔墦鍗颁俊鎭? 鑻ヨ寰楅粯璁ゆ墦鍗版牸寮? * 骞朵笉鑳芥弧瓒宠嚜宸辩殑闇€姹? 鍙嚜琛屾墿灞曡嚜宸辩悊鎯崇殑鎵撳嵃鏍煎紡
 *
 * @see HttpConfigModule.Builder#formatPrinter(FormatPrinter)
 * ================================================
 */
public class DefaultFormatPrinter implements FormatPrinter {
    private static final String TAG = "BusinessHttpLog";
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String DOUBLE_SEPARATOR = LINE_SEPARATOR + LINE_SEPARATOR;
    private static final String[] OMITTED_RESPONSE = {LINE_SEPARATOR, "Omitted response body"};
    private static final String[] OMITTED_REQUEST = {LINE_SEPARATOR, "Omitted request body"};
    private static final String N = "\n";
    private static final String T = "\t";
    private static final String REQUEST_UP_LINE = "   鈹屸攢鈹€鈹€鈹€鈹€鈹€ Request 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€";
    private static final String END_LINE = "   鈹斺攢鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€";
    private static final String RESPONSE_UP_LINE = "   鈹屸攢鈹€鈹€鈹€鈹€鈹€ Response 鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€鈹€";
    private static final String BODY_TAG = "Body:";
    private static final String URL_TAG = "URL: ";
    private static final String METHOD_TAG = "Method: @";
    private static final String HEADERS_TAG = "Headers:";
    private static final String STATUS_CODE_TAG = "Status Code: ";
    private static final String RECEIVED_TAG = "Received in: ";
    private static final String CORNER_UP = "鈹?";
    private static final String CORNER_BOTTOM = "鈹?";
    private static final String CENTER_LINE = "鈹?";
    private static final String DEFAULT_LINE = "鈹?";

    private static boolean isEmpty(String line) {
        return TextUtils.isEmpty(line) || N.equals(line) || T.equals(line) || TextUtils.isEmpty(line.trim());
    }

    /**
     * 鎵撳嵃缃戠粶璇锋眰淇℃伅, 褰撶綉缁滆姹傛椂 {{@link okhttp3.RequestBody}} 鍙互瑙ｆ瀽鐨勬儏鍐?     *
     * @param request
     * @param bodyString
     */
    @Override
    public void printJsonRequest(@NonNull Request request, @NonNull String bodyString) {
        final String requestBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyString;
        final String tag = getTag(true);

        LogUtils.debugInfo(tag, REQUEST_UP_LINE);
        logLines(tag, new String[]{URL_TAG + request.url()}, false);
        logLines(tag, getRequest(request), true);
        logLines(tag, requestBody.split(LINE_SEPARATOR), true);
        LogUtils.debugInfo(tag, END_LINE);
    }

    /**
     * 鎵撳嵃缃戠粶璇锋眰淇℃伅, 褰撶綉缁滆姹傛椂 {{@link okhttp3.RequestBody}} 涓?{@code null} 鎴栦笉鍙В鏋愮殑鎯呭喌
     *
     * @param request
     */
    @Override
    public void printFileRequest(@NonNull Request request) {
        final String tag = getTag(true);

        LogUtils.debugInfo(tag, REQUEST_UP_LINE);
        logLines(tag, new String[]{URL_TAG + request.url()}, false);
        logLines(tag, getRequest(request), true);
        logLines(tag, OMITTED_REQUEST, true);
        LogUtils.debugInfo(tag, END_LINE);
    }

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
    @Override
    public void printJsonResponse(long chainMs, boolean isSuccessful, int code, @NonNull String headers, @Nullable MediaType contentType,
        @Nullable String bodyString, @NonNull List<String> segments, @NonNull String message, @NonNull final String responseUrl) {
        bodyString = RequestInterceptor.isJson(contentType) ? CharacterHandler.jsonFormat(bodyString)
            : RequestInterceptor.isXml(contentType) ? CharacterHandler.xmlFormat(bodyString) : bodyString;

        final String responseBody = LINE_SEPARATOR + BODY_TAG + LINE_SEPARATOR + bodyString;
        final String tag = getTag(false);
        final String[] urlLine = {URL_TAG + responseUrl, N};

        LogUtils.debugInfo(tag, RESPONSE_UP_LINE);
        logLines(tag, urlLine, true);
        logLines(tag, getResponse(headers, chainMs, code, isSuccessful, segments, message), true);
        logLines(tag, responseBody.split(LINE_SEPARATOR), true);
        LogUtils.debugInfo(tag, END_LINE);
    }

    /**
     * 鎵撳嵃缃戠粶鍝嶅簲淇℃伅, 褰撶綉缁滃搷搴旀椂 {{@link okhttp3.ResponseBody}} 涓?{@code null} 鎴栦笉鍙В鏋愮殑鎯呭喌
     *
     * @param chainMs      鏈嶅姟鍣ㄥ搷搴旇€楁椂(鍗曚綅姣)
     * @param isSuccessful 璇锋眰鏄惁鎴愬姛
     * @param code         鍝嶅簲鐮?     * @param headers      璇锋眰澶?     * @param segments     鍩熷悕鍚庨潰鐨勮祫婧愬湴鍧€
     * @param message      鍝嶅簲淇℃伅
     * @param responseUrl  璇锋眰鍦板潃
     */
    @Override
    public void printFileResponse(long chainMs, boolean isSuccessful, int code, @NonNull String headers,
        @NonNull List<String> segments, @NonNull String message, @NonNull final String responseUrl) {
        final String tag = getTag(false);
        final String[] urlLine = {URL_TAG + responseUrl, N};

        LogUtils.debugInfo(tag, RESPONSE_UP_LINE);
        logLines(tag, urlLine, true);
        logLines(tag, getResponse(headers, chainMs, code, isSuccessful, segments, message), true);
        logLines(tag, OMITTED_RESPONSE, true);
        LogUtils.debugInfo(tag, END_LINE);
    }

    /**
     * 瀵?{@code lines} 涓殑淇℃伅杩涜閫愯鎵撳嵃
     *
     * @param tag
     * @param lines
     * @param withLineSize 涓?{@code true} 鏃? 姣忚鐨勪俊鎭暱搴︿笉浼氳秴杩?10, 瓒呰繃鍒欒嚜鍔ㄦ崲琛?     */
    private static void logLines(String tag, String[] lines, boolean withLineSize) {
        for (String line : lines) {
            int lineLength = line.length();
            int MAX_LONG_SIZE = withLineSize ? 110 : lineLength;
            for (int i = 0; i <= lineLength / MAX_LONG_SIZE; i++) {
                int start = i * MAX_LONG_SIZE;
                int end = (i + 1) * MAX_LONG_SIZE;
                end = end > line.length() ? line.length() : end;
                LogUtils.debugInfo(resolveTag(tag), DEFAULT_LINE + line.substring(start, end));
            }
        }
    }

    private static ThreadLocal<Integer> last = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private static final String[] ARMS = new String[]{"-V-", "-E-", "-A-"};

    private static String computeKey() {
        if (last.get() >= 3) {
            last.set(0);
        }
        String s = ARMS[last.get()];
        last.set(last.get() + 1);
        return s;
    }

    /**
     * 姝ゆ柟娉曟槸涓轰簡瑙ｅ喅鍦?AndroidStudio v3.1 浠ヤ笂 Logcat 杈撳嚭鐨勬棩蹇楁棤娉曞榻愮殑闂
     * <p>
     * 姝ら棶棰樺紩璧风殑鍘熷洜, 鎹?JessYan 鐚滄祴, 鍙兘鏄洜涓?AndroidStudio v3.1 浠ヤ笂灏嗘瀬鐭椂闂村唴浠ョ浉鍚?tag 杈撳嚭澶氭鐨?log 鑷姩鍚堝苟涓轰竴娆¤緭鍑?     * 瀵艰嚧鏈潵瀵圭О鐨勮緭鍑烘棩蹇? 鍑虹幇涓嶅绉扮殑闂
     * AndroidStudio v3.1 姝ゆ瀵硅緭鍑烘棩蹇楃殑浼樺寲, 涓嶅皬蹇冧娇甯傞潰涓婃墍鏈夊叿鏈夋棩蹇楁牸寮忓寲杈撳嚭鍔熻兘鐨勬棩蹇楁鏋舵棤娉曟甯稿伐浣?     * 鐜板湪鏆傛椂鑳芥兂鍒扮殑瑙ｅ喅鏂规鏈変袱涓? 1. 鏀瑰彉姣忚鐨?tag (姣忚 tag 閮藉姞涓€涓彲鍙樺寲鐨?token) 2. 寤惰繜姣忚鏃ュ織鎵撳嵃鐨勯棿闅旀椂闂?     * <p>
     * {@link #resolveTag(String)} 浣跨敤绗竴绉嶈В鍐虫柟妗?     *
     * @param tag
     */
    private static String resolveTag(String tag) {
        return computeKey() + tag;
    }

    private static String[] getRequest(Request request) {
        String log;
        String header = request.headers().toString();
        log = METHOD_TAG + request.method() + DOUBLE_SEPARATOR +
            (isEmpty(header) ? "" : HEADERS_TAG + LINE_SEPARATOR + dotHeaders(header));
        return log.split(LINE_SEPARATOR);
    }

    private static String[] getResponse(String header, long tookMs, int code, boolean isSuccessful,
        List<String> segments, String message) {
        String log;
        String segmentString = slashSegments(segments);
        log = ((!TextUtils.isEmpty(segmentString) ? segmentString + " - " : "") + "is success : "
            + isSuccessful + " - " + RECEIVED_TAG + tookMs + "ms" + DOUBLE_SEPARATOR + STATUS_CODE_TAG +
            code + " / " + message + DOUBLE_SEPARATOR + (isEmpty(header) ? "" : HEADERS_TAG + LINE_SEPARATOR +
            dotHeaders(header)));
        return log.split(LINE_SEPARATOR);
    }

    private static String slashSegments(List<String> segments) {
        StringBuilder segmentString = new StringBuilder();
        for (String segment : segments) {
            segmentString.append("/").append(segment);
        }
        return segmentString.toString();
    }

    /**
     * 瀵?{@code header} 鎸夎瀹氱殑鏍煎紡杩涜澶勭悊
     *
     * @param header
     * @return
     */
    private static String dotHeaders(String header) {
        String[] headers = header.split(LINE_SEPARATOR);
        StringBuilder builder = new StringBuilder();
        String tag = "鈹€ ";
        if (headers.length > 1) {
            for (int i = 0; i < headers.length; i++) {
                if (i == 0) {
                    tag = CORNER_UP;
                } else if (i == headers.length - 1) {
                    tag = CORNER_BOTTOM;
                } else {
                    tag = CENTER_LINE;
                }
                builder.append(tag).append(headers[i]).append("\n");
            }
        } else {
            for (String item : headers) {
                builder.append(tag).append(item).append("\n");
            }
        }
        return builder.toString();
    }

    private static String getTag(boolean isRequest) {
        if (isRequest) {
            return TAG + "-Request";
        } else {
            return TAG + "-Response";
        }
    }
}

