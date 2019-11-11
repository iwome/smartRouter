package com.bbq.smart_router.handler;


import android.content.Intent;

import androidx.annotation.NonNull;

import com.bbq.smart_router.core.AbsHandler;
import com.bbq.smart_router.core.ResultCode;
import com.bbq.smart_router.core.RouterDebugger;
import com.bbq.smart_router.core.RouterRequest;
import com.bbq.smart_router.core.laucher.SmartRouter;
import com.bbq.smart_router_annotion.bean.RouterType;

/**
 * 当页面跳转失败时最终尝试隐式调用
 */
public class ActionHandler extends AbsHandler {
    @Override
    public boolean shouldHandle(@NonNull RouterRequest request) {
        return true;
    }

    @Override
    public Object handleInternal(@NonNull RouterRequest request) {
        //尝试隐式调用
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(request.getUri());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SmartRouter.getInstance().getApplication().startActivity(intent);
            SmartRouter.getInstance().onComplete(request);
        } catch (Exception e) {
            request.setType(RouterType.UNKNOW);
            request.setErrorMsg("unknow route");
            SmartRouter.getInstance().onError(ResultCode.CODE_ERROR, request);
            RouterDebugger.fatal(e);
        }
        return null;
    }
}
