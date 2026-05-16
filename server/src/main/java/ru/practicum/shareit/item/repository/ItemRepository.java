package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShortData;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
            SELECT i
            FROM Item AS i
            WHERE i.available = true
            AND (i.name ILIKE concat('%', :name ,'%') OR i.description ILIKE concat('%', :name ,'%'))
            """)
    List<Item> findAllByNameIsLikeOrDescriptionIsLike(@Param("name") String name);

    List<Item> findAllByOwnerId(Long id);

    List<ItemShortData> findAllByRequestIdIn(List<Long> requestIds);
}
