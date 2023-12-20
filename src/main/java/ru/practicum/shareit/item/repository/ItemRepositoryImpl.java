package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Long>> listOfItemsIdByUser = new HashMap<>();
    private long generateId = 1;

    @Override
    public Item addItem(long userId, Item item) {
        item.setId(generateId++);
        User owner = User.builder().id(userId).build();
        item.setOwner(owner);
        items.put(item.getId(), item);
        listOfItemsIdByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(item.getId());
        log.info("Добавлена вещь по id {}", item.getId());
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.info("Обновлена вещь по id {}", item.getId());
        return item;
    }

    @Override
    public Item getItemById(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Вещи нет в базе данных");
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemByUserId(long userId) {
        if (!listOfItemsIdByUser.containsKey(userId)) {
            throw new NotFoundException("У пользователя нет вещи");
        }
        return listOfItemsIdByUser.get(userId).stream()
                .map(items::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemByText(String text) {
        List<Item> list = new ArrayList<>();
        final String lowercaseText = text.toLowerCase();
        for (Item item : items.values()) {
            if (item.isAvailable()) {
                if (item.getName().toLowerCase().contains(lowercaseText) ||
                        item.getDescription().toLowerCase().contains(lowercaseText)) {
                    list.add(item);
                }
            }
        }

        return list;
    }

}
