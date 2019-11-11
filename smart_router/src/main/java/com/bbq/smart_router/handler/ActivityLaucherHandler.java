package com.bbq.smart_router.handler;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.bbq.smart_router.core.AbsHandler;
import com.bbq.smart_router.core.ChainedInterceptor;
import com.bbq.smart_router.core.InterceptorCallback;
import com.bbq.smart_router.core.ResultCode;
import com.bbq.smart_router.core.RouterDebugger;
import com.bbq.smart_router.core.RouterRequest;
import com.bbq.smart_router.core.laucher.SmartRouter;
import com.bbq.smart_router.interfaces.IInterceptor;
import com.bbq.smart_router.utils.RouterUtils;
import com.bbq.smart_router_annotion.bean.RouterType;

/**
 * 处理activity跳转
 */
public class ActivityLaucherHandler extends AbsHandler {
    private ChainedInterceptor mInterceptor;

    @SuppressWarnings("ConstantConditions")
    public ActivityLaucherHandler addInterceptor(@NonNull IInterceptor interceptor) {
        if (interceptor != null) {
            if (mInterceptor == null) {
                mInterceptor = new ChainedInterceptor();
            }
            mInterceptor.addInterceptor(interceptor);
        }
        return this;
    }

    public ActivityLaucherHandler addInterceptors(IInterceptor... interceptors) {
        if (interceptors != null && interceptors.length > 0) {
            if (mInterceptor == null) {
                mInterceptor = new ChainedInterceptor();
            }
            for (IInterceptor interceptor : interceptors) {
                mInterceptor.addInterceptor(interceptor);
            }
        }
        return this;
    }

    public ActivityLaucherHandler clearInterceptors() {
        if (mInterceptor != null) {
            mInterceptor.clear();
        }
        return this;
    }

    protected boolean shouldHandle(@NonNull RouterRequest request) {
        return request != null && request.getType() == RouterType.ACTIVITY;
    }

    @Override
    public Object handleInternal(@NonNull final RouterRequest request) {
        if (!request.isSkipInterceptors() && mInterceptor != null && !mInterceptor.isEmpty()) {
            mInterceptor.intercept(request, new InterceptorCallback() {
                @Override
                public void onContinue(RouterRequest request) {

                }

                @Override
                public void onComplete(RouterRequest request) {
                    //拦截器处理了，本次跳转结束
                    next(request);
                }
            });
        } else {
            next(request);
        }
        return null;
    }

    private Object next(@NonNull final RouterRequest request) {
        Context context = request.getContext();
        if (context == null) {
            context = SmartRouter.getInstance().getApplication();
        }
        final Intent intent = new Intent(context, request.getDestination());
        Bundle bundle = request.getExtras();
        //解析uri参数,并导入bundle
        RouterUtils.parseUriParamsToBundle(request.getUri(), bundle);
        intent.putExtras(bundle);
        if (request.getFlags() != -1) {
            intent.setFlags(request.getFlags());
        } else if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        final Context finalContext = context;
        SmartRouter.getInstance().getMainHandler().post(new Runnable() {//在主线程中跳转
            @Override
            public void run() {
                //可能需要返回码
                try {
                    if (request.getRequestCode() > 0) {
                        ActivityCompat.startActivityForResult((Activity) finalContext, intent,
                                request.getRequestCode(), request.getOptionsBundle());
                    } else {
                        ActivityCompat.startActivity(finalContext, intent, request.getOptionsBundle());
                    }

                    if ((0 != request.getEnterAnim() || 0 != request.getExitAnim()) && finalContext instanceof Activity) {
                        //老版本
                        ((Activity) finalContext).overridePendingTransition(request
                                .getEnterAnim(), request.getExitAnim());
                    }
                } catch (ActivityNotFoundException e) {
                    RouterDebugger.fatal(e);
                    SmartRouter.getInstance().onError(e.getMessage(), ResultCode.CODE_NOT_FOUND, request);
                } catch (Exception e) {
                    RouterDebugger.fatal(e);
                    SmartRouter.getInstance().onError(e.getMessage(), ResultCode.CODE_ERROR, request);
                }
                SmartRouter.getInstance().onComplete(request);
            }
        });
        return null;
    }
}
