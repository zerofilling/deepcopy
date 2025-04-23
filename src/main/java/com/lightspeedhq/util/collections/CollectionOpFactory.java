package com.lightspeedhq.util.collections;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Factory for creating appropriate ICollectionOp instances based on collection type.
 * <p>
 * This factory determines the appropriate implementation of ICollectionOp to use
 * for different collection types during the deep copy process. It handles special cases
 * for immutable collections and provides a default implementation for standard collections.
 * </p>
 */
public final class CollectionOpFactory {

    /**
     * Map of immutable collection class names to their specialized ICollectionOp suppliers.
     * <p>
     * This map provides specialized handling for immutable collections that cannot be
     * instantiated and populated directly.
     * </p>
     */
    private final static Map<String, Supplier<ICollectionOp>> IMMUTABLE_COLLECTIONS =
            Map.of("java.util.Arrays$ArrayList", ArrayListInArrayOp::new);

    /**
     * Default function for creating an ICollectionOp for standard collection types.
     * <p>
     * This function creates a DefaultCollectionOp for standard collection classes
     * that are not specifically handled as immutable collections.
     * </p>
     */
    private final static Function<Class<Collection<Object>>, ICollectionOp> DEFAULT_COLLECTION = new Function<Class<Collection<Object>>, ICollectionOp>() {
        @Override
        public ICollectionOp apply(Class<Collection<Object>> objectClass) {
            try {
                return new DefaultCollectionOp(objectClass);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CollectionOpFactory() {

    }

    /**
     * Creates an appropriate ICollectionOp instance for the specified collection class.
     * <p>
     * This method determines whether to use a specialized ICollectionOp for immutable
     * collections or the default implementation for standard collections.
     * </p>
     *
     * @param className The fully qualified name of the collection class
     * @return An appropriate ICollectionOp instance for the collection type
     * @throws ClassNotFoundException If the specified class cannot be found
     */
    @SuppressWarnings("unchecked")
    public static ICollectionOp of(String className) throws ClassNotFoundException {
        Supplier<ICollectionOp> immutableCollectionSupplier = IMMUTABLE_COLLECTIONS.get(className);
        return immutableCollectionSupplier == null
                ? DEFAULT_COLLECTION.apply((Class<Collection<Object>>) Class.forName(className))
                : immutableCollectionSupplier.get();
    }
}
