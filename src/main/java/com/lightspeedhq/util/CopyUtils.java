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

/**
 * Utility class for creating deep copies of objects.
 * <p>
 * This class provides functionality to create deep copies (clones) of objects,
 * including handling of complex object graphs with circular references.
 * It uses reflection and the Unsafe API to handle objects without default constructors.
 * </p>
 */
public final class CopyUtils {

    /**
     * Set of immutable classes that don't need deep copying.
     * <p>
     * Objects of these classes can be shared between the original and copied object
     * graph since they cannot be modified.
     * </p>
     */
    private static final Set<Class<?>> IMMUTABLES = Set.of(
            Integer.class, Long.class,
            String.class, Boolean.class,
            Double.class, Float.class,
            Character.class, Byte.class,
            Short.class, Void.class
    );

    /**
     * Reference to the Unsafe instance for low-level memory operations.
     * <p>
     * Used to create objects without invoking their constructors when
     * no default constructor is available.
     * </p>
     */
    private static final Unsafe UNSAFE = getUnsafe();

    /**
     * Creates a deep copy of the provided object.
     * <p>
     * This method creates a completely independent copy of the object and all its
     * non-immutable properties, handling circular references appropriately.
     * </p>
     *
     * @param obj The object to deep copy
     * @param <T> The type of the object
     * @return A deep copy of the provided object
     * @throws Exception If an error occurs during the copying process
     */
    @SuppressWarnings("unchecked")
    public static <T> T deepCopy(final T obj) throws Exception {
        return (T) internalDeepCopy(new IdentityHashMap<>(), obj);
    }

    /**
     * Internal recursive implementation of deep copy functionality.
     * <p>
     * This method handles the actual copying process, maintaining a map of
     * already copied objects to handle circular references.
     * </p>
     *
     * @param converted Map of original objects to their corresponding copies
     * @param obj       The object to copy
     * @return A deep copy of the provided object
     * @throws Exception If an error occurs during the copying process
     */
    @SuppressWarnings("unchecked")
    private static Object internalDeepCopy(final Map<Object, Object> converted, final Object obj) throws Exception {
        if (obj == null) {
            return null;
        }

        final Class<?> clazz = obj.getClass();

        // Immutable objects handled as is
        if (clazz.isPrimitive() || clazz.isEnum() || IMMUTABLES.contains(clazz)) {
            return obj;
        } else if (converted.containsKey(obj)) {
            return converted.get(obj);
        }

        // Handling array objects
        if (clazz.isArray()) {
            final int length = Array.getLength(obj);
            final Object arrayCopy = Array.newInstance(clazz.componentType(), length);
            for (int i = 0; i < length; i++) {
                final Object element = Array.get(obj, i);
                final Object copyElement = internalDeepCopy(converted, element);
                Array.set(arrayCopy, i, copyElement);
            }
            converted.put(obj, arrayCopy);
            return arrayCopy;
        }

        // Handling collections
        if (obj instanceof Collection<?>) {
            final Collection<Object> collection = (Collection<Object>) obj;
            final Collection<Object> collectionCopy = (Collection<Object>) instantiate(clazz);
            for (Object o : collection) {
                collectionCopy.add(internalDeepCopy(converted, o));
            }
            converted.put(collection, collectionCopy);
            return collectionCopy;
        }

        // Handling maps
        if (obj instanceof Map<?, ?>) {
            final Map<Object, Object> map = (Map<Object, Object>) obj;
            final Map<Object, Object> mapCopy = (Map<Object, Object>) instantiate(map.getClass());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                final Object copyKey = internalDeepCopy(converted, entry.getKey());
                final Object copyValue = internalDeepCopy(converted, entry.getValue());
                mapCopy.put(copyKey, copyValue);
            }
            converted.put(map, mapCopy);
            return mapCopy;
        }

        // Handling other objects
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
        converted.put(obj, objCopy);
        return objCopy;
    }

    /**
     * Visits a class and all its superclasses, applying the provided consumer to each.
     * <p>
     * This method traverses the class hierarchy from the given class up to Object,
     * applying the provided consumer to each class in the hierarchy.
     * </p>
     *
     * @param clazz         The starting class
     * @param classConsumer Consumer to apply to each class in the hierarchy
     */
    private static void visitSupers(final Class<?> clazz, final Consumer<Class<?>> classConsumer) {
        Class<?> current = clazz;
        while (current != null) {
            classConsumer.accept(current);
            current = current.getSuperclass();
        }
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
