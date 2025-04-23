package com.lightspeedhq.util.map;

import java.util.Map;

/**
 * Interface defining operations for map manipulation during deep copy process.
 * <p>
 * This interface provides a standard way to interact with various types of maps
 * during the deep copy process, allowing for custom handling of different map types.
 * </p>
 */
public interface IMapOp {
    /**
     * Adds a key-value pair to the underlying map.
     *
     * @param key The key to add to the map
     * @param value The value to associate with the key
     */
    void put(Object key, Object value);
    
    /**
     * Returns the resulting map with all added key-value pairs.
     *
     * @return The map containing all previously added entries
     */
    Map<Object, Object> getMap();
}
