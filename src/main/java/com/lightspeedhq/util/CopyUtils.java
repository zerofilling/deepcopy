package com.lightspeedhq.util;

import com.lightspeedhq.util.collections.ICollectionOp;
import com.lightspeedhq.util.collections.CollectionOpFactory;

import java.lang.reflect.Array;
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
            converted.put(obj, arrayCopy);
            for (int i = 0; i < length; i++) {
                final Object element = Array.get(obj, i);
                final Object copyElement = internalDeepCopy(converted, element);
                Array.set(arrayCopy, i, copyElement);
            }
            return arrayCopy;
        }

        // Handling collections
        if (obj instanceof Collection<?>) {
            final Collection<Object> collection = (Collection<Object>) obj;


            ICollectionOp collectionCopyOp = CollectionOpFactory.of(clazz.getName());

            for (Object o : collection) {
                collectionCopyOp.add(internalDeepCopy(converted, o));
            }
            Collection<Object> collectionCopy = collectionCopyOp.getCollection();
            converted.put(collection, collectionCopy);
            return collectionCopy;
        }

        // Handling maps
        if (obj instanceof Map<?, ?>) {
            final Map<Object, Object> map = (Map<Object, Object>) obj;
            final Map<Object, Object> mapCopy = (Map<Object, Object>) InstantiateUtils.instantiate(map.getClass());
            converted.put(map, mapCopy);
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                final Object copyKey = internalDeepCopy(converted, entry.getKey());
                final Object copyValue = internalDeepCopy(converted, entry.getValue());
                mapCopy.put(copyKey, copyValue);
            }
            return mapCopy;
        }

        // Handling other objects
        final Object objCopy = InstantiateUtils.instantiate(clazz);
        converted.put(obj, objCopy);
        visitSupers(clazz, aClass -> {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    try {
                        final Object value = field.get(obj);
                        final Object copyValue = internalDeepCopy(converted, value);
                        field.set(objCopy, copyValue);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
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
}
