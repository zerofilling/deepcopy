package com.lightspeedhq.util.collections;

import java.util.Collection;

/**
 * Interface defining operations for collection manipulation during deep copy process.
 * <p>
 * This interface provides a standard way to interact with various types of collections
 * during the deep copy process, allowing for custom handling of different collection types.
 * </p>
 */
public interface ICollectionOp {
    /**
     * Adds an object to the underlying collection.
     *
     * @param o The object to add to the collection
     */
    void add(Object o);

    /**
     * Returns the resulting collection with all added objects.
     *
     * @return The collection containing all previously added objects
     */
    Collection<Object> getCollection();
}
