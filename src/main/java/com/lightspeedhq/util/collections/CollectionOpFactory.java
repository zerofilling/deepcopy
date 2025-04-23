package com.lightspeedhq.util.collections;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CollectionOpFactory {

    private final static Map<String, Supplier<ICollectionOp>> IMMUTABLE_COLLECTIONS =
            Map.of("java.util.Arrays$ArrayList", ArrayListInArrayOp::new);

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

    private CollectionOpFactory() {

    }

    @SuppressWarnings("unchecked")
    public static ICollectionOp of(String className) throws ClassNotFoundException {
        Supplier<ICollectionOp> immutableCollectionSupplier = IMMUTABLE_COLLECTIONS.get(className);
        return immutableCollectionSupplier == null
                ? DEFAULT_COLLECTION.apply((Class<Collection<Object>>) Class.forName(className))
                : immutableCollectionSupplier.get();
    }
}
