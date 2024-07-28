package service;

import java.util.HashMap;
import java.util.Map;

public class ErrorDTO {
    private final Map<String, Object> details = new HashMap<>();

    public Map<String, Object> getDetails() {
        return details;
    }
}
