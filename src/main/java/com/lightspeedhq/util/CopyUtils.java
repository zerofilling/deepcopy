package com.lightspeedhq.util;

import sun.misc.Unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public final class CopyUtils {

    private static final Set<Class<?>> IMMUTABLES = Set.of(
            Integer.class, Long.class,
            String.class, Boolean.class,
            Double.class, Float.class,
            Character.class, Byte.class,
            Short.class, Void.class
    );

    private static final Unsafe UNSAFE = getUnsafe();

    public static <T> T deepCopy(final T obj) throws Exception {
        return (T) internalDeepCopy(new IdentityHashMap<>(), obj);
    }

    private static Object internalDeepCopy(final Map<Object, Object> converted, final Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        final Class<?> clazz = obj.getClass();
        if (clazz.isPrimitive() || clazz.isEnum() || IMMUTABLES.contains(clazz)) {
            return obj;
        } else if (converted.containsKey(obj)) {
            return converted.get(obj);
        }
        if (clazz.isArray()) {
            final int length = Array.getLength(obj);
            final Object newArray = Array.newInstance(clazz.componentType(), length);
            for (int i = 0; i < length; i++) {
                final Object element = Array.get(obj, i);
                final Object copyElement = internalDeepCopy(converted, element);
                Array.set(newArray, i, copyElement);
            }
            converted.put(obj, newArray);
            return newArray;
        }
        if (obj instanceof Collection<?>) {
            final Collection<Object> collection = (Collection<Object>) obj;
            final Collection<Object> newCollection = (Collection<Object>) instantiate(clazz);
            for (Object o : collection) {
                newCollection.add(internalDeepCopy(converted, o));
            }
            converted.put(collection, newCollection);
            return newCollection;
        }
        if (obj instanceof Map<?, ?>) {
            final Map<Object, Object> map = (Map<Object, Object>) obj;
            final Map<Object, Object> newMap = (Map<Object, Object>) instantiate(map.getClass());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                final Object copyKey = internalDeepCopy(converted, entry.getKey());
                final Object copyValue = internalDeepCopy(converted, entry.getValue());
                newMap.put(copyKey, copyValue);
            }
            converted.put(map, newMap);
            return newMap;
        }


        final Object objCopy = instantiate(clazz);
        visitSupers(clazz, aClass -> {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(obj);
                        Object copyValue = internalDeepCopy(converted, value);
                        field.set(objCopy, copyValue);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        return objCopy;
    }

    private static void visitSupers(final Class<?> clazz, final Consumer<Class<?>> classConsumer) {
        Class<?> current = clazz;
        while (current != null) {
            classConsumer.accept(current);
            current = current.getSuperclass();
        }
    }

    private static Object instantiate(Class<?> cls) throws Exception {
        try {
            Constructor<?> ctor = cls.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException e) {
            // if there is no default ctor allocate without constructor
            return UNSAFE.allocateInstance(cls);
        }
    }

    private static Unsafe getUnsafe() {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to access Unsafe", e);
        }
    }
}
