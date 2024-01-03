package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User addUser(User user);

    User updateUser(long id, User user);

    User getUserById(long id);

    boolean deleteUser(long id);

    boolean isEmailExistInRepository(User user);

    List<User> getAllUsers();
}
