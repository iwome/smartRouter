package com.bbq.smart_router.bean;


import com.bbq.smart_router.core.RouterRequest;
import com.bbq.smart_router.interfaces.IRouterGroup;
import com.bbq.smart_router.interfaces.IService;
import com.bbq.smart_router_annotion.bean.RouterMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * 路由表
 */
public class RouterTable {
    /**
     * 存储路由表的分组信息
     * HashMap 默认容器大小为16，为了减少HashMap 扩容导致的性能损耗，这里将容器大小设置大一些，具体根据项目预算要多少个。
     */
    public static final Map<String, Class<? extends IRouterGroup>> rootMap = new HashMap<>(30);

    /**
     * 存储路由表保存组的具体组的信息
     */
    public static final Map<String, RouterMeta> groupMap = new HashMap<>(50);

    /**
     * 存储路由表保存服务信息
     */
    public static final Map<Class, IService> serviceMap = new HashMap<>(50);

    public static void clearAll() {
        rootMap.clear();
        groupMap.clear();
        serviceMap.clear();
    }

    /**
     * 路由查找
     *
     * @param request
     * @return
     */
    public static RouterMeta findRoute(RouterRequest request) {
        //获取仓库中存储的 具体每个组的信息
        RouterMeta routerMeta = RouterTable.groupMap.get(request.getPath());
        if (routerMeta == null) {//路由查找失败
            //再次尝试使用host查找
            routerMeta = RouterTable.groupMap.get(request.getUri().getHost());
        }
        return routerMeta;
    }
}
