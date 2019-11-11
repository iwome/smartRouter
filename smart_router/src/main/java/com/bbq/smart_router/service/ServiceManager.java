package com.bbq.smart_router.service;

import androidx.annotation.NonNull;

import com.bbq.smart_router.bean.RouterTable;
import com.bbq.smart_router.core.ResultCode;
import com.bbq.smart_router.core.RouterDebugger;
import com.bbq.smart_router.core.RouterRequest;
import com.bbq.smart_router.core.laucher.SmartRouter;
import com.bbq.smart_router.interfaces.IService;
import com.bbq.smart_router_annotion.bean.RouterMeta;
import com.bbq.smart_router_annotion.bean.RouterType;

public class ServiceManager {

    private ServiceManager() {
    }

    private static class ServiceManagerHolder {
        private static final ServiceManager instance = new ServiceManager();
    }

    public static ServiceManager getInstance() {
        return ServiceManagerHolder.instance;
    }

    /**
     * 获取服务类实例
     *
     * @param request
     * @return
     */
    public IService getService(@NonNull RouterRequest request) {
        RouterMeta routerMeta = RouterTable.findRoute(request);
        if (routerMeta == null) {
            SmartRouter.getInstance().onError("service router not found", ResultCode.CODE_NOT_FOUND, request);
            return null;
        }
        //设置服务
        switch (routerMeta.getType()) {
            case RouterType.SERVICE:
                Class<?> destination = routerMeta.getDestination();
                IService iService = RouterTable.serviceMap.get(destination);
                if (iService == null) {
                    try {
                        iService = (IService) destination.getConstructor().newInstance();
                        RouterTable.serviceMap.put(destination, iService);
                        request.setService(iService);
                    } catch (Exception e) {
                        SmartRouter.getInstance().onError("get service router failed", ResultCode.CODE_ERROR, request);
                        RouterDebugger.fatal(e);
                    }
                }
                return iService;
            default:
                break;
        }
        return null;
    }
}
