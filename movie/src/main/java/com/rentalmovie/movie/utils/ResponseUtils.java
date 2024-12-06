package com.rentalmovie.movie.utils;

import java.util.Map;

public class ResponseUtils {

    private ResponseUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Map<String, String> createMessageResponse(String message) {
        return Map.of("message", message);
    }
}
