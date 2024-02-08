package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    private final User user = new User(1L, "John", "john.doe@mail.com");

    @Test
    void getAllUsersTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> userDtoList = userService.getAllUsers();

        assertNotNull(userDtoList);
        assertEquals(1, userDtoList.size());
        assertEquals(user.getId(), userDtoList.get(0).getId());
        assertEquals(user.getName(), userDtoList.get(0).getName());
        assertEquals(user.getEmail(), userDtoList.get(0).getEmail());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserByIdTest() {
        Long userId = user.getId();
        when(userRepository.checkUser(userId)).thenReturn(user);

        UserDto userDto = userService.getUserById(userId);

        assertNotNull(userDto);
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
        verify(userRepository, times(1)).checkUser(anyLong());
    }

    @Test
    void getUserByIdNotFound() {
        Long userId = user.getId();

        when(userRepository.checkUser(userId)).thenThrow(new NotFoundException("User not found"));

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    void deleteUserNotFound() {
        Long userId = user.getId();
        doThrow(new NotFoundException("User not found")).when(userRepository).deleteById(userId);

        assertThrows(NotFoundException.class,
                () -> userService.deleteUser(userId));
    }

    @Test
    void updateUserWithEmailAlreadyExists() {
        Long userId = user.getId();
        UserDto updatedUserDto = UserDto.builder().email("existing.email@mail.com").build();
        when(userRepository.checkUser(userId)).thenReturn(user);
        when(userRepository.existsByEmail("existing.email@mail.com")).thenReturn(true);

        assertThrows(EmailException.class,
                () -> userService.updateUser(userId, updatedUserDto));
    }
}

