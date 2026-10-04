package com.dairymart.dairyappserver.util;

import com.google.gson.Gson;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ApiMessages {

    private static final Gson GSON = new Gson();

    private ApiMessages() {
    }

    public static String of(String message) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", message == null || message.isBlank()
                ? "That request was not valid. Check the details and try again."
                : message);
        return GSON.toJson(body);
    }
}
