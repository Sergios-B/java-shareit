package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.comment.CreateCommentDto;
import ru.practicum.shareit.item.dto.item.CreateItemDto;
import ru.practicum.shareit.item.dto.item.UpdateItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public Object createItem(CreateItemDto itemDto, Long userId) {
        return post("", userId, null, itemDto);
    }

    public Object updateItem(UpdateItemDto itemDto, Long userId, Long itemId) {
        return patch("/" + itemId,  userId, itemDto);
    }

    public Object getItemById(Long itemId) {
        return get("/" + itemId);
    }

    public Object searchItemsByName(Long userid, String text) {

        Map<String, Object> parameters = Map.of("text", text);

        return get("/search?text={text}", userid, parameters);
    }

    public Object findItemsWithAfterAndBeforeBookingDateByUserId(Long userId) {
        return get("", userId);
    }

    public Object addComment(Long itemId, Long userId, CreateCommentDto commentDto) {
        return post(String.format("/%d/comment", itemId), userId, commentDto);
    }
}
