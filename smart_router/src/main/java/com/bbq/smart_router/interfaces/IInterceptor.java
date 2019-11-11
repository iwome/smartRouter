package com.bbq.smart_router.interfaces;


import androidx.annotation.NonNull;

import com.bbq.smart_router.core.InterceptorCallback;
import com.bbq.smart_router.core.RouterRequest;

/**
 * 自定义拦截器都要实现这个接口
 */
public interface IInterceptor {
    /**
     * 拦截处理完成后，
     * 要调用 {@link InterceptorCallback#onContinue(RouterRequest)} ()} 或
     * {@link InterceptorCallback#onComplete(RouterRequest)} 方法
     */
    void intercept(RouterRequest request, @NonNull InterceptorCallback callback);

    /**
     * 数值越小，优先级越高
     *
     * @return
     */
    int priority();
}
