package com.bbq.smart_router.interfaces;


import com.bbq.smart_router_annotion.bean.RouterMeta;

import java.util.Map;

public interface IRouterGroup {
    void loadInfo(Map<String, RouterMeta> routeMetaMap);
}
