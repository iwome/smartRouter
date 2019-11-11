package com.bbq.smart_router_compile.utils;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

public class RouterCompileUtils {
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isEmpty(final Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean isEmpty(@Nullable String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isEmpty(@Nullable Object object) {
        return object == null;
    }

    public static boolean isStartWithSplash(String url) {
        return url.startsWith("/");
    }
}
