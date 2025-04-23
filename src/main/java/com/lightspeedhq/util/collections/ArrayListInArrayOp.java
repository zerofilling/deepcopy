package com.lightspeedhq.util.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArrayListInArrayOp implements ICollectionOp {

    private final List<Object> list = new ArrayList<>();

    @Override
    public void add(Object o) {
        list.add(o);
    }

    @Override
    public Collection<Object> getCollection() {
        return Arrays.asList(list.toArray());
    }
}
