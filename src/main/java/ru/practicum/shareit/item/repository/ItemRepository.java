package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(long userId);

    @Query(value = "select i from Item as i where (lower(i.name) like %?1% or lower(i.description) like %?1%) and " +
            "i.available=true")
    List<Item> getItemByText(String text);

    default Item checkItem(long itemId) {
        return findById(itemId).orElseThrow(() -> new NotFoundException(String.format("Предмета с id = %s нет в базе",
                itemId)));
    }
}
