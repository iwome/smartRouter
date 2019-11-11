package com.bbq.smart_router.interfaces;


import com.bbq.smart_router.core.ResultCode;
import com.bbq.smart_router.core.RouterRequest;

/**
 * 路由跳转回调接口
 */
public interface RouterCallback {
    /**
     * 分发成功
     */
    void onComplete(RouterRequest request);

    /**
     * 分发失败
     *
     * @param resultCode 错误代码，可参考 {@link ResultCode}
     */
    void onError(RouterRequest request, int resultCode);
}
