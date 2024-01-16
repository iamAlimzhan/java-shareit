package ru.practicum.shareit.validation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;


@Component
@AllArgsConstructor
public class Validation {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    //Эти проверки можно было бы реализовать в default методах интерфейсов где они используется, или следует
    //завсети отдельный интерфейс
    public Item checkItem(long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);

        if (optionalItem.isPresent()) {
            return optionalItem.get();
        } else {
            throw new NotFoundException(String.format("Предмета с id = %s нет в базе", itemId));
        }
    }

    public User checkUser(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new NotFoundException(String.format("Предмета с id = %s нет в базе", userId));
        }
    }

}
