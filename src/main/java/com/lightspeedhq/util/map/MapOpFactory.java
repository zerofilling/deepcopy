package com.lightspeedhq.util.map;

import java.util.Map;
import java.util.function.Function;

/**
 * Factory for creating appropriate IMapOp instances based on map type.
 * <p>
 * This factory determines the appropriate implementation of IMapOp to use
 * for different map types during the deep copy process. It handles special cases
 * for immutable maps and provides a default implementation for standard maps.
 * </p>
 */
public final class MapOpFactory {

    /**
     * Default function for creating an IMapOp for standard map types.
     * <p>
     * This function creates a DefaultMapOp for standard map classes.
     * </p>
     */
    private final static Function<Class<Map<Object, Object>>, IMapOp> DEFAULT_MAP = mapClass -> {
        try {
            return new DefaultMapOp(mapClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private MapOpFactory() {
    }

    /**
     * Creates an appropriate IMapOp instance for the specified map class.
     * <p>
     * This method currently uses the default implementation for all maps,
     * but could be extended to handle special map types.
     * </p>
     *
     * @param className The fully qualified name of the map class
     * @return An appropriate IMapOp instance for the map type
     * @throws ClassNotFoundException If the specified class cannot be found
     */
    @SuppressWarnings("unchecked")
    public static IMapOp of(String className) throws ClassNotFoundException {
        return DEFAULT_MAP.apply((Class<Map<Object, Object>>) Class.forName(className));
    }
}
