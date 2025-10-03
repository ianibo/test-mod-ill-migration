package com.k_int.ill.utils;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import groovy.util.logging.Slf4j;

/**
 * Contains utilities relating to json
 *
 * @author Chas
 *
 */
@Slf4j
public class Json {

    /**
     * Converts object to a json string
     * @param objectThe json to be converted
     * @return The string it has turned into
     */
    static public String toJson(Object object) {
        String json = null;

        // Do we have an object
        if (object != null) {
            // We have an object
            try {
                // We have an object, so we need to generate the json as a string
                JsonMapper jsonMapper = new JsonMapper().enable(SerializationFeature.INDENT_OUTPUT);
                jsonMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                json = jsonMapper.writeValueAsString(object);
            } catch (Exception e) {
                log.error("Exception thrown while converting object to json", e);
            }
        }

        // Return the json to the caller
        return(json);
    }
}
