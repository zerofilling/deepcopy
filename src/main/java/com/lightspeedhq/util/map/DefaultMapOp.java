package com.lightspeedhq.util.map;

import com.lightspeedhq.util.InstantiateUtils;

import java.util.Map;

/**
 * Default implementation of IMapOp for handling standard map types.
 * <p>
 * This class provides a generic implementation for handling standard Java maps
 * during the deep copy process. It instantiates a new map of the same type as
 * the original and populates it with copied elements.
 * </p>
 */
public class DefaultMapOp implements IMapOp {

    private final Map<Object, Object> map;

    /**
     * Creates a new DefaultMapOp for the specified map class.
     * <p>
     * Instantiates a new map of the specified class to store copied elements.
     * </p>
     *
     * @param mapClass The class of map to instantiate
     * @throws Exception If an error occurs during instantiation
     */
    @SuppressWarnings("unchecked")
    public DefaultMapOp(Class<Map<Object, Object>> mapClass) throws Exception {
        this.map = (Map<Object, Object>) InstantiateUtils.instantiate(mapClass);
    }

    @Override
    public void put(Object key, Object value) {
        map.put(key, value);
    }

    @Override
    public Map<Object, Object> getMap() {
        return map;
    }
}
