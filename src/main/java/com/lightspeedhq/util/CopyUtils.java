package com.lightspeedhq.util;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class CopyUtils {

    public static <T> T deepCopy(T obj, Map<Class<?>, Supplier<?>> extraInstances) {
        Map<?, ?> converted = new IdentityHashMap<>();
        return internalDeepCopy(converted, obj, extraInstances);
    }

    private static <T> T internalDeepCopy(Map<?, ?> converted, T obj, Map<Class<?>, Supplier<?>> extraInstances) {
        if (obj == null) {
            return null;
        }
        return null;
    }
}
