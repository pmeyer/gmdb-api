package com.yellowmoon.gmdb.graphql;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/// The MultipartVariableMapper class provides functionality for mapping multipart variables
/// to a nested structure of maps and lists based on a dot-separated object path. This utility
/// allows dynamic assignment of values into complex structures, such as those encountered
/// when processing multipart data in APIs.
///
/// Core functionality:
/// - Traverse through a multi-level nested structure.
/// - Supports mapping to both `Map<String, Object>` and `List<Object>` data structures.
/// - Ensures intermediate values exist and throws runtime exceptions when invalid paths or
///   unexpected types are encountered.
///
/// Key methods:
/// - `mapVariable(String, Map<String, Object>, P)`: Maps the given value to the target
///   location in the structure, determined by the provided object path.
/// - `determineMapper(Object, String, String)`: Determines the appropriate mapper
///   (Map or List) for traversing or modifying a particular segment of the structure.
///
/// Exceptions:
/// - Throws `RuntimeException` if the object path is invalid or if the intermediate
///   value in the structure is `null`.
/// - Throws `RuntimeException` if the structure contains an unexpected data type or
///   if the target cannot be mapped as expected.
///
/// Internal design:
/// - Utilizes helper interfaces (`Mapper`) for abstracting operations on `Map`
///   and `List` types.
/// - Uses a static `Pattern` for splitting dot-separated object paths into segments.
///
/// Intended usage is typically within contexts where multipart variables are dynamically
/// processed and stored in structured formats, such as for file uploads or parameterized data
/// inputs in APIs.
public class MultipartVariableMapper {

    private static final Pattern PERIOD = Pattern.compile("\\.");

    private static final MultipartVariableMapper.Mapper<Map<String, Object>> MAP_MAPPER = new MultipartVariableMapper.Mapper<>() {
        @Override
        public <P> Object set(final Map<String, Object> location, final String target, final P value) {
            return location.put(target, value);
        }

        @Override
        public Object recurse(final Map<String, Object> location, final String target) {
            return location.get(target);
        }
    };

    private static final MultipartVariableMapper.Mapper<List<Object>> LIST_MAPPER = new MultipartVariableMapper.Mapper<>() {
        @Override
        public <P> Object set(final List<Object> location, final String target, final P value) {
            return location.set(Integer.parseInt(target), value);
        }

        @Override
        public Object recurse(final List<Object> location, final String target) {
            return location.get(Integer.parseInt(target));
        }
    };

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <P> void mapVariable(final String objectPath, final Map<String, Object> variables, final P file) {
        final String[] segments = PERIOD.split(objectPath);

        if (segments.length < 2) {
            throw new RuntimeException("object-path in map must have at least two segments");
        } else if (!"variables".equals(segments[0])) {
            throw new RuntimeException("can only map into variables");
        }

        Object currentLocation = variables;
        for (int i = 1; i < segments.length; i++) {
            final String segmentName = segments[i];
            final MultipartVariableMapper.Mapper mapper = determineMapper(currentLocation, objectPath, segmentName);

            if (i == segments.length - 1) {
                if (null != mapper.set(currentLocation, segmentName, file)) {
                    throw new RuntimeException("expected null value when mapping " + objectPath);
                }
            } else {
                currentLocation = mapper.recurse(currentLocation, segmentName);
                if (null == currentLocation) {
                    throw new RuntimeException("found null intermediate value when trying to map " + objectPath);
                }
            }
        }
    }

    private static MultipartVariableMapper.Mapper<?> determineMapper(final Object currentLocation, final String objectPath, final String segmentName) {
        if (currentLocation instanceof Map) {
            return MAP_MAPPER;
        } else if (currentLocation instanceof List) {
            return LIST_MAPPER;
        }

        throw new RuntimeException("expected a map or list at " + segmentName + " when trying to map " + objectPath);
    }

    interface Mapper<T> {

        <P> Object set(T location, String target, P value);

        Object recurse(T location, String target);
    }
}
