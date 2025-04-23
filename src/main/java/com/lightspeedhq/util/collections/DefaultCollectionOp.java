package com.lightspeedhq.util.collections;

import com.lightspeedhq.util.InstantiateUtils;

import java.util.Collection;

/**
 * Default implementation of ICollectionOp for handling standard collection types.
 * <p>
 * This class provides a generic implementation for handling standard Java collections
 * during the deep copy process. It instantiates a new collection of the same type as
 * the original and populates it with copied elements.
 * </p>
 */
public class DefaultCollectionOp implements ICollectionOp {

    private final Collection<Object> collection;

    /**
     * Creates a new DefaultCollectionOp for the specified collection class.
     * <p>
     * Instantiates a new collection of the specified class to store copied elements.
     * </p>
     *
     * @param collectionClass The class of collection to instantiate
     * @throws Exception If an error occurs during instantiation
     */
    @SuppressWarnings("unchecked")
    public DefaultCollectionOp(Class<Collection<Object>> collectionClass) throws Exception {
        this.collection = (Collection<Object>) InstantiateUtils.instantiate(collectionClass);
    }

    /**
     * Adds the specified element to the collection.
     *
     * @param o The element to add
     */
    @Override
    public void add(Object o) {
        collection.add(o);
    }

    /**
     * Returns the collection of copied elements.
     *
     * @return The collection of copied elements
     */
    @Override
    public Collection<Object> getCollection() {
        return collection;
    }
}
