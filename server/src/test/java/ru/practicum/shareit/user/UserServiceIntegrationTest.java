package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest
public class UserServiceIntegrationTest {
    private final EntityManager manager;
    private final UserService service;

    private final User user = new User(null, "аты", "атыжоны@mail.rk");
    private final UserDto userDto = new UserDto(null, "абай", "абай.кунанбайулы@mail.rk");

    @BeforeEach
    void setUp() {
        manager.persist(user);
    }

    @Test
    void updateUser() {
        UserDto updatedUser = service.updateUser(user.getId(), userDto);

        assertNotNull(updatedUser);
        assertEquals(updatedUser.getId(), user.getId());
        assertEquals(updatedUser.getName(), user.getName());
        assertEquals(updatedUser.getEmail(), user.getEmail());
    }
}
