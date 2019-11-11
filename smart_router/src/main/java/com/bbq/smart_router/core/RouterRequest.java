package com.bbq.smart_router.core;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;

import com.bbq.smart_router.core.laucher.SmartRouter;
import com.bbq.smart_router.interfaces.IService;
import com.bbq.smart_router.interfaces.RouterCallback;
import com.bbq.smart_router.utils.RouterUtils;
import com.bbq.smart_router_annotion.bean.RouterMeta;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 路由请求实体类
 */
public class RouterRequest extends RouterMeta {
    //跳转请求完成的回调
    private RouterCallback onCompleteListener;
    //响应码
    private int resultCode;
    //响应错误提示信息
    private String errorMsg = "";
    @NonNull
    private WeakReference<Context> mContext;
    /**
     * 跳转动画
     */
    //新版 md风格
    private Bundle optionsCompat;
    //老版
    private int enterAnim;
    private int exitAnim;
    //请求码
    private int requestCode = -1;
    /**
     * 是否跳过拦截器
     */
    private boolean isSkipInterceptors = false;

    /**
     * 获取服务调用
     */
    private IService service;

    @NonNull
    private Uri mUri;
    @NonNull
    private HashMap<String, Object> mFields;
    //携带bundle参数
    private Bundle mBundle;
    //Intent.FLAG_ACTIVITY**
    private int flags = -1;

    public RouterCallback getOnCompleteListener() {
        return onCompleteListener;
    }

    public int getResultCode() {
        return resultCode;
    }

    public RouterRequest setResultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public RouterRequest setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    @NonNull
    public Context getContext() {
        return mContext.get();
    }

    public void setFields(@NonNull HashMap<String, Object> mFields) {
        this.mFields = mFields;
    }

    public RouterRequest setFlags(int flags) {
        this.flags = flags;
        return this;
    }

    public Bundle getOptionsCompat() {
        return optionsCompat;
    }

    public RouterRequest setOptionsCompat(Bundle optionsCompat) {
        this.optionsCompat = optionsCompat;
        return this;
    }

    public RouterRequest setEnterAnim(int enterAnim) {
        this.enterAnim = enterAnim;
        return this;
    }

    public RouterRequest setExitAnim(int exitAnim) {
        this.exitAnim = exitAnim;
        return this;
    }

    public boolean isSkipInterceptors() {
        return isSkipInterceptors;
    }

    public RouterRequest setSkipInterceptors(boolean skipInterceptors) {
        isSkipInterceptors = skipInterceptors;
        return this;
    }

    public RouterRequest(@NonNull Context context, String uri) {
        this(context, parseUriSafely(uri));
    }

    public RouterRequest(@NonNull Context context, Uri uri) {
        mContext = new WeakReference<>(context);
        mFields = new HashMap<>();
        mBundle = new Bundle();
        if (!RouterUtils.hasScheme(uri)) {//如果没有scheme，拼接默认的scheme
            setPath(uri.toString());
            String schemeHost = RouterUtils.schemeHost(SmartRouter.SCHEME, uri.toString());
            uri = parseUriSafely(schemeHost);
        }

        setUri(uri);
    }

    @NonNull
    public HashMap<String, Object> getFields() {
        return mFields;
    }

    private static Uri parseUriSafely(@Nullable String uri) {
        return TextUtils.isEmpty(uri) ? Uri.EMPTY : Uri.parse(uri);
    }

    @NonNull
    public Uri getUri() {
        return mUri;
    }

    @SuppressWarnings("ConstantConditions")
    public RouterRequest setUri(Uri uri) {
        if (RouterUtils.isUriNotNull(uri)) {
            mUri = uri;
        } else {
            RouterDebugger.fatal("RouterRequest.setUri不应该传入空值");
        }
        return this;
    }

    public IService getService() {
        return service;
    }

    public void setService(IService service) {
        this.service = service;
    }

    public Bundle getExtras() {
        return mBundle;
    }

    public int getEnterAnim() {
        return enterAnim;
    }

    public int getExitAnim() {
        return exitAnim;
    }

    public Bundle getOptionsBundle() {
        return optionsCompat;
    }

    /**
     * Intent.FLAG_ACTIVITY**
     *
     * @param flag
     * @return
     */
    public RouterRequest withFlags(int flag) {
        this.flags = flag;
        return this;
    }


    public int getFlags() {
        return flags;
    }

    /**
     * 跳转动画
     *
     * @param enterAnim
     * @param exitAnim
     * @return
     */
    public RouterRequest withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    /**
     * 转场动画
     *
     * @param compat
     * @return
     */
    public RouterRequest withOptionsCompat(ActivityOptionsCompat compat) {
        if (null != compat) {
            this.optionsCompat = compat.toBundle();
        }
        return this;
    }

    public RouterRequest withString(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
        return this;
    }


    public RouterRequest withBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }


    public RouterRequest withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }


    public RouterRequest withInt(@Nullable String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }


    public RouterRequest withLong(@Nullable String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }


    public RouterRequest withDouble(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }


    public RouterRequest withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }


    public RouterRequest withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }


    public RouterRequest withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }


    public RouterRequest withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }


    public RouterRequest withStringArray(@Nullable String key, @Nullable String[] value) {
        mBundle.putStringArray(key, value);
        return this;
    }


    public RouterRequest withBooleanArray(@Nullable String key, boolean[] value) {
        mBundle.putBooleanArray(key, value);
        return this;
    }


    public RouterRequest withShortArray(@Nullable String key, short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }


    public RouterRequest withIntArray(@Nullable String key, int[] value) {
        mBundle.putIntArray(key, value);
        return this;
    }


    public RouterRequest withLongArray(@Nullable String key, long[] value) {
        mBundle.putLongArray(key, value);
        return this;
    }


    public RouterRequest withDoubleArray(@Nullable String key, double[] value) {
        mBundle.putDoubleArray(key, value);
        return this;
    }


    public RouterRequest withByteArray(@Nullable String key, byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }


    public RouterRequest withCharArray(@Nullable String key, char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }


    public RouterRequest withFloatArray(@Nullable String key, float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }


    public RouterRequest withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    public RouterRequest withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends
            Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public RouterRequest withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    public RouterRequest withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    public RouterRequest setOnCompleteListener(RouterCallback onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
        return this;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public RouterRequest setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }
}
