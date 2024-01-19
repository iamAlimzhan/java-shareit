package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    public default User checkUser(long userId) {
        Optional<User> optionalUser = findById(userId);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new NotFoundException(String.format("Предмета с id = %s нет в базе", userId));
        }
    }
}
