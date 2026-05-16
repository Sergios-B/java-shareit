package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Repository
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
            where r.id = :id
            """)
    Optional<ItemRequest> findItemRequestByWithItems(@Param("id") Long id);

    Page<ItemRequest> findAllByRequestorIdNot(Long requestorId, Pageable pageable);
}
