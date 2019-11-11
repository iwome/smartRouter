package com.bbq.smart_router.interfaces;

import java.util.Map;

/**
 * 路由表加载分组信息{@link IRouterGroup}接口
 */
public interface IRouterRoot {
    void loadInfo(Map<String, Class<? extends IRouterGroup>> routers);
}
