package com.bbq.smart_router.core.laucher;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.bbq.smart_router.bean.RouterTable;
import com.bbq.smart_router.core.ResultCode;
import com.bbq.smart_router.core.RouterDebugger;
import com.bbq.smart_router.core.RouterRequest;
import com.bbq.smart_router.handler.ActionHandler;
import com.bbq.smart_router.handler.ActivityLaucherHandler;
import com.bbq.smart_router.handler.FragmentHandler;
import com.bbq.smart_router.interfaces.IInterceptor;
import com.bbq.smart_router.interfaces.IService;
import com.bbq.smart_router.service.ServiceManager;
import com.bbq.smart_router.utils.RouterUtils;
import com.bbq.smart_router_annotion.bean.RouterMeta;
import com.bbq.smart_router_annotion.bean.RouterType;

/**
 * Uri分发器
 */
public class RouteDispatcher {
    //处理activity跳转
    private ActivityLaucherHandler activityLaucherHandler;
    //处理fragment
    private FragmentHandler fragmentHandler;
    //处理隐式调用
    private ActionHandler actionHandler;

    private RouteDispatcher() {
        init();
    }

    protected static RouteDispatcher getInstance() {
        return RouteDispatcherHolder.instance;
    }

    private static class RouteDispatcherHolder {
        private static final RouteDispatcher instance = new RouteDispatcher();
    }

    private void init() {
        if (activityLaucherHandler == null)
            activityLaucherHandler = new ActivityLaucherHandler();
        if (fragmentHandler == null)
            fragmentHandler = new FragmentHandler();
        if (actionHandler == null)
            actionHandler = new ActionHandler();
    }

    private boolean isNative(@NonNull Uri uri) {
        return SmartRouter.SCHEME.equals(uri.getScheme());
    }

    private boolean isHttpOrHttps(@NonNull Uri uri) {
        String scheme = uri.getScheme();
        if ("http".equals(scheme) || "https".equals(scheme)) {
            return true;
        }
        return false;
    }

    private boolean hostIsOpenUrl(@NonNull Uri uri) {
        return SmartRouter.SCHEME.equals(uri.getScheme()) && SmartRouter.ACTIVITY_OPENURL.equals(uri.getHost());
    }

    /**
     * 开始分发
     *
     * @param request
     * @return
     */
    protected Object start(@NonNull RouterRequest request) {
        Uri uri = request.getUri();
        if (!RouterUtils.isUriNotNull(uri)) {
            SmartRouter.getInstance().onError("uri is null", ResultCode.CODE_BAD_REQUEST, request);
            return false;
        }
        try {
            if (isHttpOrHttps(uri)) {//H5 url重新拼接 路由地址重定向为web容器
                uri = Uri.parse(SmartRouter.SCHEME + "://" + SmartRouter.ACTIVITY_OPENURL + "?url=" + uri.toString());
            }
            if (isNative(uri)) { //启动native页面
                //创建请求对象
                boolean createRequest = produceRouteRequest(request);
                if (!createRequest) {
                    SmartRouter.getInstance().onError("not found router by " + request.getUri(), ResultCode.CODE_NOT_FOUND, request);
                    return false;
                }
                switch (request.getType()) {
                    case RouterType.ACTIVITY: // 页面跳转
                        setInterceptor(activityLaucherHandler, request);
                        activityLaucherHandler.handleInternal(request);
                        request.setResultCode(ResultCode.CODE_SUCCESS);
                        if (request.getOnCompleteListener() != null)
                            request.getOnCompleteListener().onComplete(request);
                        return true;
                    case RouterType.FRAGMENT:// 获取Fragment实例
                        return fragmentHandler.handleInternal(request);
                    case RouterType.SERVICE:// 获取服务
                        IService iService = ServiceManager.getInstance().getService(request);
                        if (iService != null) {
                            //初始化服务
                            iService.init(request);
                            SmartRouter.getInstance().onComplete(request);
                        }
                        return iService;
                    default:// 未知路由
                        request.setType(RouterType.UNKNOW);
                        request.setErrorMsg("unknow route");
                        SmartRouter.getInstance().onError("not find router by " + uri, ResultCode.CODE_NOT_FOUND, request);
                        break;
                }
            } else {
                //尝试隐式调用
                actionHandler.handleInternal(request);
                return true;
            }
        } catch (Exception e) {
            RouterDebugger.fatal(e);
            SmartRouter.getInstance().onError(e.getMessage(), ResultCode.CODE_ERROR, request);
        }
        return null;
    }

    /**
     * 准备请求对象
     *
     * @param card 跳转卡
     * @return
     */
    private boolean produceRouteRequest(@NonNull RouterRequest card) {
        //查询路由表信息
        RouterMeta routerMeta = RouterTable.findRoute(card);
        if (routerMeta == null) {//路由查找失败
            return false;
        }
        //设置要跳转的类
        card.setDestination(routerMeta.getDestination());
        //设置要跳转的类型
        card.setType(routerMeta.getType());
        //设置拦截器
        card.setInterceptors(routerMeta.getInterceptors());
        return true;
    }

    /**
     * 设置拦截器
     *
     * @param handler
     * @param request
     */
    private void setInterceptor(@NonNull ActivityLaucherHandler handler, @NonNull RouterRequest request) {
        handler.clearInterceptors();
        String[] interceptors = request.getInterceptors();
        if (interceptors != null && interceptors.length > 0) {
            for (String interceptorClassName : interceptors) {
                try {
                    IInterceptor interceptor = (IInterceptor) Class.forName(interceptorClassName).newInstance();
                    if (interceptor != null) {
                        handler.addInterceptor(interceptor);
                    }
                } catch (Exception e) {
                    RouterDebugger.fatal(e);
                }
            }
        }
    }
}
