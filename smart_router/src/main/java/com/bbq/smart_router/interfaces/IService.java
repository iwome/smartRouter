package com.bbq.smart_router.interfaces;


import com.bbq.smart_router.core.RouterRequest;

/**
 * 路由服务接口，继承此接口实现模块间解耦
 */
public interface IService {
    void init(RouterRequest request);
}
