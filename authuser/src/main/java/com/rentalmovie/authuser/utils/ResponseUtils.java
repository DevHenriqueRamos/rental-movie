package com.rentalmovie.authuser.utils;

import java.util.Map;

public class ResponseUtils {

    public static Map<String, String> createMessageResponse(String message) {
        return Map.of("message", message);
    }
}
