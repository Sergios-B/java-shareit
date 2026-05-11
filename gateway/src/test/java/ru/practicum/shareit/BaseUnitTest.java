package ru.practicum.shareit;

import org.springframework.http.HttpHeaders;

public abstract class BaseUnitTest {

    protected static final long USER_ID = 12;

    protected static final String NOT_MISSING_HEADER =
            "Required request header 'X-Sharer-User-Id' for method parameter type Long is not present";

    public static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(USER_ID));
        return headers;
    }
}
