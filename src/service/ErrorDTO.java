package service;

import java.util.HashMap;
import java.util.Map;

public class ErrorDTO {
    private final Map<Integer, String> details = new HashMap<>();

    public Map<Integer, String> getDetails() {
        return details;
    }
}
