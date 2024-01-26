package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    default User checkUser(long userId) {
        return findById(userId).orElseThrow(() -> new NotFoundException(String.format("Предмета с id = %s нет в базе",
                userId)));
    }

    boolean existsByEmail(String email);
}

