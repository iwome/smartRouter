package com.bbq.smart_router.core;


import androidx.annotation.NonNull;

import com.bbq.smart_router.interfaces.RouterCallback;

/**
 * 默认的全局跳转监听器，在跳转失败时弹Toast提示
 */
public class GlobalRouterCallback implements RouterCallback {
    public static final GlobalRouterCallback INSTANCE = new GlobalRouterCallback();

    @Override
    public void onComplete(@NonNull RouterRequest request) {
        RouterDebugger.e(request.getUri() + " has complete!");
    }

    @Override
    public void onError(@NonNull RouterRequest request, int resultCode) {
        RouterDebugger.e(request.getErrorMsg(), resultCode);
    }
}
