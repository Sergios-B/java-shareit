package ru.practicum.shareit.item.model;


import org.springframework.beans.factory.annotation.Value;

public interface ItemShortData {

    Long getId();

    String getName();

    @Value("#{target.owner.id}")
    Long getOwner();

    Long getRequestId();
}
