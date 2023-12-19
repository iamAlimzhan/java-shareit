package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item addItem(long userId, Item item);

    Item updateItem(Item item);

    Item getItemById(long itemId);

    List<Item> getItemByUserId(long userId);

    List<Item> getItemByText(String text);
}
