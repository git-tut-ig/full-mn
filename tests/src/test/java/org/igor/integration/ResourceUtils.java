package org.igor.integration;

import org.igor.integration.authors.AuthorsTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourceUtils {
    public static String readJsonFileAsString(String filePath) throws IOException {
        try (InputStream inputStream = AuthorsTest.class.getClassLoader()
                .getResourceAsStream(filePath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + filePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
