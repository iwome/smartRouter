package com.bbq.smart_router.core;

/**
 * 拦截器回调
 */
public interface InterceptorCallback {
    void onContinue(RouterRequest request);

    void onComplete(RouterRequest request);
}
