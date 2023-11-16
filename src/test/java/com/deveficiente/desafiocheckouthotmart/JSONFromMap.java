package com.deveficiente.desafiocheckouthotmart;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONFromMap {

    public static String execute(Map<String, String> request) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
        throw new RuntimeException(e);
        }
    }    
}
