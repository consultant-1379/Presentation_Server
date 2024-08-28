/*******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************/

package com.ericsson.nms.presentation.service.json.parsers;

import com.ericsson.nms.presentation.exceptions.JsonParserException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Parser used to read the JSON files.
 */
public class JsonParser {

    /**
     * Return an object representation of the given JSON content
     * @param jsonFile json content to be parsed
     * @param destinationClass class to be deserialized.
     * @param <T> generic type of the class to be deserialized.
     * @return An instance of T representing the JSON contents.
     * @throws JsonParserException when the parser fails to deserialize the json content.
     */
    public <T> T parseToObject(final File jsonFile, final Class<T> destinationClass) {
        try {
            return new ObjectMapper().readValue(jsonFile, destinationClass);
        } catch (final IOException e) {
            throw new JsonParserException("Failed to parse the json context to "+ destinationClass, e);
        }
    }

    public JsonNode readNode(final File file) {

        try {
            return new ObjectMapper().readTree(file);

        } catch (final IOException e) {
            throw new JsonParserException("Failed to parse the json contents", e);
        }
    }
}

