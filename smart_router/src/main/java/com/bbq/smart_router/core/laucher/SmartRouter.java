package com.bbq.smart_router.core.laucher;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.bbq.smart_router.bean.RouterTable;
import com.bbq.smart_router.core.GlobalRouterCallback;
import com.bbq.smart_router.core.RouterDebugger;
import com.bbq.smart_router.core.RouterRequest;
import com.bbq.smart_router.interfaces.IRouterGroup;
import com.bbq.smart_router.utils.RouterUtils;
import com.bbq.smart_router_annotion.bean.Constans;
import com.bbq.smart_router_annotion.bean.RouterMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

/**
 * 路由核心类
 */
public class SmartRouter {
    private GlobalRouterCallback mGlobalCallback;
    private Application application;
    public Handler mHandler;
    //默认scheme
    public static String SCHEME = null;
    //默认web容器host
    public static String ACTIVITY_OPENURL = "openurl"; //链接url
    //生成路由注册文件的包名
    private static final String PAGENAME = Constans.PAGENAME;
    //生成路由具体分组信息类名前缀
    private static final String GROUP_CLASS_NAME = Constans.GROUP_CLASS_NAME;

    private SmartRouter() {
    }

    public static SmartRouter getInstance() {
        return RouteHolder.instance;
    }

    private static class RouteHolder {
        private static final SmartRouter instance = new SmartRouter();
    }

    /**
     * 设置全局监听器
     *
     * @param defaultRouteCallback
     * @return
     */
    public SmartRouter setGlobalRouteCallback(@NonNull GlobalRouterCallback defaultRouteCallback) {
        this.mGlobalCallback = defaultRouteCallback;
        return this;
    }

    public GlobalRouterCallback getGlobalCallback() {
        return mGlobalCallback;
    }

    public Application getApplication() {
        return application;
    }

    public Handler getMainHandler() {
        if (mHandler == null)
            return (mHandler = new Handler(Looper.getMainLooper()));
        else
            return mHandler;
    }

    /**
     * 初始化-收集路由表，必须在Application中初始化
     *
     * @param application
     * @param scheme      默认scheme
     */
    public void init(Application application, @NonNull String scheme, @NonNull String openUrl) {
        RouterDebugger.e("---------------- SmartRouter init start ----------------");
        RouterDebugger.e(">>> SmartRouter scheme  config: " + scheme);
        RouterDebugger.e(">>> SmartRouter openUrl config: " + openUrl);
        this.application = application;
        this.SCHEME = scheme;
        this.ACTIVITY_OPENURL = openUrl;
        try {
            loadRouteTable();
        } catch (Exception e) {
            RouterDebugger.fatal(e);
        }
        RouterDebugger.e("---------------- SmartRouter init end ----------------");
    }

    /**
     * 收集加载路由表分组信息
     */
    private void loadRouteTable() throws InterruptedException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (application == null) {
            throw new IllegalArgumentException(">>> initRouter(Application application) must Application init");
        }
        //获取所有APT 生成的路由类的全类名
        Set<String> routerMap = RouterUtils.getFileNameByPackageName(application, PAGENAME);
        for (String className : routerMap) {
            //获取注册的路由信息，存储到本地仓库中。
            if (className.startsWith(PAGENAME + "." + GROUP_CLASS_NAME)) {
                Object instance = Class.forName(className).getConstructor().newInstance();
                if (instance instanceof IRouterGroup) {
                    IRouterGroup routeRoot = (IRouterGroup) instance;
                    routeRoot.loadInfo(RouterTable.groupMap);
                }
            }
        }
        RouterDebugger.e(">>> Collecting RouterTable ... ... 「");
        for (Map.Entry<String, RouterMeta> entry : RouterTable.groupMap.entrySet()) {
            RouterDebugger.e("【key --> " + entry.getKey() + ": value --> " + entry.getValue().toString() + "】");
        }
        RouterDebugger.e(" 」 Collecting RouterTable end");
    }

    public Object start(String url) {
        return start(url, null);
    }

    public Object start(Uri uri) {
        return start(uri, null);
    }

    public Object start(String url, Context context) {
        if (TextUtils.isEmpty(url)) {
            RouterDebugger.e("SmartRouter", ">>> url is null");
            return false;
        }
        return start(Uri.parse(url), context);
    }

    public Object start(Uri uri, Context context) {
        if (RouterUtils.isUriNotNull(uri)) {
            RouterRequest request = new RouterRequest(context, uri);
            return start(request);
        }
        RouterDebugger.e("SmartRouter", ">>> uri is null");
        return null;
    }

    /**
     * 发起页面跳转
     *
     * @param request
     * @return
     */
    public Object start(@NonNull RouterRequest request) {
        RouterDebugger.e(">>>>>>>>>>start router:" + request.getUri().toString());
        return RouteDispatcher.getInstance().start(request);
    }

    /**
     * 跳转完成触发回调
     *
     * @param request 请求参数
     */
    public void onComplete(RouterRequest request) {
        if (request.getOnCompleteListener() != null)
            request.getOnCompleteListener().onComplete(request);
        if (mGlobalCallback != null)
            mGlobalCallback.onComplete(request);
    }

    public void onError(int errorCode, RouterRequest request) {
        onError("", errorCode, request);
    }

    /**
     * 跳转失败触发回调
     *
     * @param error     错误描述
     * @param errorCode 错误码
     * @param request   请求参数
     */
    public void onError(String error, int errorCode, RouterRequest request) {
        if (request.getOnCompleteListener() != null)
            request.getOnCompleteListener().onError(request.setErrorMsg(error), errorCode);
        if (mGlobalCallback != null)
            mGlobalCallback.onError(request.setErrorMsg(error), errorCode);
    }
}
