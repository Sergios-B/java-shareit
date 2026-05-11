package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("""
            select r
            from ItemRequest as r
            where r.requestorId = :userid
            """)
    List<ItemRequest> findItemRequestsByRequestorIdWithItems(@Param("userid") Long userid);

    @Query("""
            select r
            from ItemRequest as r
            left join fetch r.items
            where r.id = :id
            """)
    Optional<ItemRequest> findItemRequestByWithItems(@Param("id") Long id);
}
