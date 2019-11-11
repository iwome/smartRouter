package com.bbq.smart_router.utils;

import android.util.Log;

import com.bbq.smart_router.core.RouterDebugger;

/**
 * 自定义Logger
 */
public class RouterLogger implements RouterDebugger.Logger {

    @Override
    public void d(String msg, Object... args) {
        Log.d(RouterDebugger.LOG_TAG, format(msg, args));
    }

    @Override
    public void i(String msg, Object... args) {
        Log.i(RouterDebugger.LOG_TAG, format(msg, args));
    }

    @Override
    public void w(String msg, Object... args) {
        Log.w(RouterDebugger.LOG_TAG, format(msg, args));
    }

    @Override
    public void w(Throwable t) {
        Log.w(RouterDebugger.LOG_TAG, t);
    }

    @Override
    public void e(String msg, Object... args) {
        Log.e(RouterDebugger.LOG_TAG, format(msg, args));
    }

    @Override
    public void e(Throwable t) {
        Log.e(RouterDebugger.LOG_TAG, "", t);
    }

    @Override
    public void fatal(String msg, Object... args) {
        Log.e(RouterDebugger.LOG_TAG, format(msg, args));
        handleError(new RuntimeException(format(msg, args)));
    }

    @Override
    public void fatal(Throwable t) {
        Log.e(RouterDebugger.LOG_TAG, "", t);
        handleError(t);
    }

    /**
     * 处理fatal级别的错误。默认行为是在调试环境下抛出异常，非调试环境不做处理。
     */
    protected void handleError(Throwable t) {
        if (RouterDebugger.isEnableDebug()) {
            if (t instanceof RuntimeException) {
                throw ((RuntimeException) t);
            } else {
                throw new RuntimeException(t);
            }
        }
    }

    protected String format(String msg, Object... args) {
        if (args != null && args.length > 0) {
            try {
                return String.format(msg, args);
            } catch (Throwable t) {
                e(t);
            }
        }
        return msg;
    }
}
