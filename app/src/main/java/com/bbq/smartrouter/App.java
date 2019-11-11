package com.bbq.smartrouter;

import android.app.Application;

import com.bbq.smart_router.core.RouterDebugger;
import com.bbq.smart_router.core.laucher.SmartRouter;
import com.bbq.smart_router.utils.RouterLogger;

/**
 * balabala..
 * Created by bangbang.qiu on 2019/11/11.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SmartRouter.getInstance().init(this,"smartApp","openUrl");
        RouterDebugger.setLogger(new RouterLogger());
        RouterDebugger.setEnableDebug(BuildConfig.DEBUG);
        RouterDebugger.setEnableLog(BuildConfig.DEBUG);
    }
}
