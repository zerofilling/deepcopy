package com.lightspeedhq.util.collections;

import com.lightspeedhq.util.InstantiateUtils;

import java.util.Collection;

public class DefaultCollectionOp implements ICollectionOp {

    private final Collection<Object> collection;

    @SuppressWarnings("unchecked")
    public DefaultCollectionOp(Class<Collection<Object>> collectionClass) throws Exception {
        this.collection = (Collection<Object>) InstantiateUtils.instantiate(collectionClass);
    }

    @Override
    public void add(Object o) {
        collection.add(o);
    }

    @Override
    public Collection<Object> getCollection() {
        return collection;
    }
}
