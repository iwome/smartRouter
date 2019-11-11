package com.bbq.smart_router.utils;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.bbq.smart_router.core.RouterDebugger;
import com.bbq.smart_router.core.laucher.SmartRouter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import dalvik.system.DexFile;

public class RouterUtils {
    /**
     * 转成小写
     */
    public static String toLowerCase(String s) {
        return TextUtils.isEmpty(s) ? s : s.toLowerCase();
    }

    /**
     * 转成非null的字符串，如果为null返回空串
     */
    public static String toNonNullString(String s) {
        return s == null ? "" : s;
    }

    /**
     * 是否为null或长度为0
     */
    public static boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    /**
     * 根据scheme和host生成字符串
     */
    @NonNull
    public static String schemeHost(String scheme, String host) {
        return toNonNullString(toLowerCase(scheme)) + "://" + toNonNullString(toLowerCase(host));
    }

    /**
     * 根据scheme和host生成字符串
     */
    public static String schemeHost(Uri uri) {
        return uri == null ? null : schemeHost(uri.getScheme(), uri.getHost());
    }

    public static boolean isUriNotNull(String uri) {
        return TextUtils.isEmpty(uri);
    }

    public static boolean isUriNotNull(Uri uri) {
        return uri != null && !Uri.EMPTY.equals(uri);
    }

    public static boolean hasScheme(String uri) {
        if (!isUriNotNull(uri)) {
            RouterDebugger.e("uri is null");
            return false;
        }
        return hasScheme(Uri.parse(uri));
    }

    public static boolean hasScheme(Uri uri) {
        if (!isUriNotNull(uri)) {
            RouterDebugger.e("uri is null");
            return false;
        }
        return !TextUtils.isEmpty(uri.getScheme());
    }

    /**
     * 在Uri中添加参数
     *
     * @param uri    原始uri
     * @param params 要添加的参数
     * @return uri    新的uri
     */
    public static Uri appendParams(Uri uri, Map<String, String> params) {
        if (uri != null && params != null && !params.isEmpty()) {
            Uri.Builder builder = uri.buildUpon();
            try {
                for (String key : params.keySet()) {
                    if (TextUtils.isEmpty(key)) continue;
                    final String val = uri.getQueryParameter(key);
                    if (val == null) { // 当前没有此参数时，才会添加
                        final String value = params.get(key);
                        builder.appendQueryParameter(key, value);
                    }
                }
                return builder.build();
            } catch (Exception e) {
                RouterDebugger.fatal(e);
            }
        }
        return uri;
    }

    /**
     * 解析url参数添加到bundle
     *
     * @param bundle
     */
    public static void parseUriParamsToBundle(@NonNull String uri, @NonNull Bundle bundle) {
        parseUriParamsToBundle(uri, bundle);
    }

    public static void parseUriParamsToBundle(@NonNull Uri uri, @NonNull Bundle bundle) {
        Uri paramUri;
        if (SmartRouter.ACTIVITY_OPENURL.equals(uri.getHost())) {
            String httpUrl = uri.getEncodedQuery();
            try {
                paramUri = Uri.parse(httpUrl);
            } catch (Exception e) {
                RouterDebugger.fatal(e);
                return;
            }
        } else {
            paramUri = uri;
        }
        if (!isUriNotNull(paramUri)) {
            return;
        }
        // 得到参数字符串
        String zpParams = paramUri.getEncodedQuery();
        // 拆分获得单个参数
        if (!TextUtils.isEmpty(zpParams)) {
            String[] params = zpParams.split("&");
            for (String param : params) {
                String[] key_Value = param.split("=");
                if (key_Value != null && key_Value.length == 2) {
                    String value = trim(uri.getQueryParameter(key_Value[0]));
                    RouterDebugger.i(">>> router parse url params =key= " + key_Value[0] + " =value= " + key_Value[1] + " | decoder value：" + value);
                    if (isBoolean(key_Value[1])) {
                        bundle.putBoolean(key_Value[0], Boolean.valueOf(key_Value[1]));
                    } else {
                        bundle.putString(key_Value[0], value);
                    }
                }
            }
        }
    }

    /**
     * 添加斜线前缀
     */
    public static String appendSlash(String path) {
        if (path != null && !path.startsWith("/")) {
            path = '/' + path;
        }
        return path;
    }

    public static String trim(String str) {
        String result = "";
        if (!TextUtils.isEmpty(str)) {
            result = str.trim();
        }
        return result;
    }

    /**
     * 获得程序所有的apk(instant run会产生很多split apk)
     *
     * @param context
     * @return
     */
    public static List<String> getSourcePath(Context context) {
        try {
            ApplicationInfo applicationInfo = context
                    .getPackageManager()
                    .getApplicationInfo(context.getPackageName(), 0);//flags Annotation retention policy.
            List<String> sourceList = new ArrayList<>();
            sourceList.add(applicationInfo.sourceDir);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != applicationInfo.splitSourceDirs) {
                    sourceList.addAll(Arrays.asList(applicationInfo.splitSourceDirs));
                }
            }
            return sourceList;
        } catch (PackageManager.NameNotFoundException e) {
            RouterDebugger.fatal(e);
            return null;
        }
    }


    /**
     * 根据包名 找到包下的类
     *
     * @param application
     * @param pageName
     * @return
     */
    public static Set<String> getFileNameByPackageName(Application application, final String pageName) throws InterruptedException {
        final Set<String> classNams = new HashSet<>();
        List<String> sourcePath = getSourcePath(application);//apk 的资源路径
        //使用同步计数器判断均处理完成
        final CountDownLatch countDownLatch = new CountDownLatch(sourcePath.size());
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.newDefaultPoolExecutor(sourcePath.size());
        for (final String path : sourcePath) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    DexFile dexFile = null;
                    try {
                        //加载apk中的dex遍历 获得所有包名为pageName的类
                        dexFile = new DexFile(path);
                        Enumeration<String> entries = dexFile.entries();
                        while (entries.hasMoreElements()) {
                            String className = entries.nextElement();
                            if (className.startsWith(pageName)) {
                                classNams.add(className);
                            }
                        }
                    } catch (IOException e) {
                        RouterDebugger.fatal(e);
                    } finally {
                        if (null != dexFile) {
                            try {
                                dexFile.close();
                            } catch (IOException e) {
                                RouterDebugger.fatal(e);
                            }
                        }
                        //释放1个
                        countDownLatch.countDown();
                    }
                }
            });
        }
        //等待执行完成
        countDownLatch.await();
        return classNams;
    }

    public static boolean isBoolean(String s) {
        String str = toLowerCase(s);
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return "false".equals(str) || "true".equals(str);
    }
}
