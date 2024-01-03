package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();

    private long generateId = 1;

    @Override
    public User addUser(User user) {
        user.setId(generateId++);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с id {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(long id, User user) {
        users.put(id, user);
        log.info("Обновлен пользователь с id {}", user.getId());
        return user;
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean deleteUser(long id) {
        users.remove(id);
        log.info("Удален пользователь с id {}", id);
        return true;
    }

    @Override
    public boolean isEmailExistInRepository(User user) {
        boolean isExist = false;
        for (User userAnother : users.values()) {
            if (userAnother.getEmail().equals(user.getEmail())) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }
}
