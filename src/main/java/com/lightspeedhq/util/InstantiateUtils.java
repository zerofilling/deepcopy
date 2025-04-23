package com.lightspeedhq.util;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public final class InstantiateUtils {

    /**
     * Reference to the Unsafe instance for low-level memory operations.
     * <p>
     * Used to create objects without invoking their constructors when
     * no default constructor is available.
     * </p>
     */
    private static final Unsafe UNSAFE = getUnsafe();

    private InstantiateUtils() {
    }

    /**
     * Creates a new instance of the specified class.
     * <p>
     * This method attempts to create a new instance using the default constructor.
     * If no default constructor is available, it uses Unsafe to allocate an instance
     * without invoking any constructor.
     * </p>
     *
     * @param cls The class to instantiate
     * @return A new instance of the specified class
     * @throws Exception If an error occurs during instantiation
     */
    public static Object instantiate(Class<?> cls) throws Exception {
        try {
            Constructor<?> ctor = cls.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException e) {
            // if there is no default ctor allocate without constructor
            return UNSAFE.allocateInstance(cls);
        }
    }

    /**
     * Retrieves the Unsafe instance using reflection.
     * <p>
     * This method uses reflection to access the otherwise inaccessible Unsafe class,
     * which provides low-level memory operations.
     * </p>
     *
     * @return The Unsafe instance
     * @throws RuntimeException If unable to access the Unsafe instance
     */
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
