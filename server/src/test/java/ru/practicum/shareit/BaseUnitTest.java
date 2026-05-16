package ru.practicum.shareit;

import org.springframework.http.HttpHeaders;

public abstract class BaseUnitTest {

    protected static final long USER_ID = 12;

    public static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Sharer-User-Id", String.valueOf(USER_ID));
        return headers;
    }
}
