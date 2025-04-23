package com.lightspeedhq.util.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Implementation of ICollectionOp for handling Arrays.ArrayList collections.
 * <p>
 * This class provides specialized handling for Arrays.ArrayList collections which are immutable.
 * It collects items in a standard ArrayList and then converts it to Arrays.asList format
 * when retrieving the collection.
 * </p>
 */
public class ArrayListInArrayOp implements ICollectionOp {

    private final List<Object> list = new ArrayList<>();

    /**
     * Adds an object to the internal ArrayList.
     *
     * @param o The object to add to the collection
     */
    @Override
    public void add(Object o) {
        list.add(o);
    }

    /**
     * Returns the collection as an immutable list created by Arrays.asList.
     * <p>
     * This method creates an immutable list that matches the behavior of Arrays$ArrayList.
     * </p>
     *
     * @return An immutable list containing all added objects
     */
    @Override
    public Collection<Object> getCollection() {
        return Arrays.asList(list.toArray());
    }
}
