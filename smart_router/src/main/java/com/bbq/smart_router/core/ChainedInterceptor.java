package com.bbq.smart_router.core;



import androidx.annotation.NonNull;

import com.bbq.smart_router.interfaces.IInterceptor;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 责任链模式拦截器
 */
public class ChainedInterceptor implements IInterceptor {
    private final List<IInterceptor> mInterceptors = new LinkedList<>();

    @Override
    public void intercept(@NonNull RouterRequest request, @NonNull InterceptorCallback callback) {
        if (mInterceptors.size() > 0) {
            //先按优先级进行排序
            Collections.sort(mInterceptors, new Comparator<IInterceptor>() {
                @Override
                public int compare(IInterceptor t0, IInterceptor t1) {
                    return t0.priority() - t1.priority();
                }
            });
            next(mInterceptors.iterator(), request, callback);
        }
    }

    @Override
    public int priority() {
        return 0;
    }

    @SuppressWarnings("ConstantConditions")
    public void addInterceptor(@NonNull IInterceptor interceptor) {
        if (interceptor != null) {
            mInterceptors.add(interceptor);
        }
    }

    private void next(@NonNull final Iterator<IInterceptor> iterator, @NonNull final RouterRequest request,
                      @NonNull final InterceptorCallback callback) {
        if (iterator.hasNext()) {
            IInterceptor t = iterator.next();
            if (RouterDebugger.isEnableLog()) {
                RouterDebugger.e("    %s: intercept, request = %s", t.getClass().getSimpleName(), request);
            }
            t.intercept(request, new InterceptorCallback() {
                @Override
                public void onContinue(RouterRequest request) {
                    next(iterator, request, callback);
                }

                @Override
                public void onComplete(RouterRequest request) {
                    callback.onComplete(request);
                }
            });
        } else {
            callback.onComplete(request);
        }
    }

    public boolean isEmpty() {
        return mInterceptors == null || mInterceptors.size() == 0;
    }

    public void clear() {
        if (mInterceptors != null && mInterceptors.size() > 0) {
            mInterceptors.clear();
        }
    }
}
