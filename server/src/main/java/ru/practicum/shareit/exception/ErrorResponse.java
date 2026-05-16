package ru.practicum.shareit.exception;

import java.util.Map;

public record ErrorResponse(String error, Map<String, String> details) {
}
