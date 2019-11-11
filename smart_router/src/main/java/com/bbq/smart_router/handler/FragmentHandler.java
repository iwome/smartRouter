package com.bbq.smart_router.handler;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bbq.smart_router.core.AbsHandler;
import com.bbq.smart_router.core.ResultCode;
import com.bbq.smart_router.core.RouterDebugger;
import com.bbq.smart_router.core.RouterRequest;
import com.bbq.smart_router.core.laucher.SmartRouter;

/**
 * 获取fragment实例
 */
public class FragmentHandler extends AbsHandler {
    @Override
    public boolean shouldHandle(@NonNull RouterRequest request) {
        return true;
    }

    @Override
    public Object handleInternal(@NonNull RouterRequest request) {
        Class<?> fragment = request.getDestination();
        try {
            Object instance = fragment.getConstructor().newInstance();
            if (instance instanceof Fragment) {
                ((Fragment) instance).setArguments(request.getExtras());
            } else if (instance instanceof android.app.Fragment) {
                ((android.app.Fragment) instance).setArguments(request.getExtras());
            }
            SmartRouter.getInstance().onComplete(request);
            return instance;
        } catch (Exception e) {
            SmartRouter.getInstance().onError(e.getMessage(), ResultCode.CODE_ERROR, request);
            RouterDebugger.fatal(e);
        }
        return null;
    }
}
