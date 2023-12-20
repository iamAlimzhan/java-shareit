package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) {
        validationAdd(userDto);
        if (userRepository.isEmailExistInRepository(UserMapper.toUser(userDto))) {
            throw new EmailException(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.addUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = validationUpdate(userId, userDto);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.toUserDto(userRepository.getUserById(userId));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream().map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteUser(long userId) {
        return userRepository.deleteUser(userId);
    }

    private void validationAdd(UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new ValidationException("Email не может быть пустым");
        }
        if (userDto.getName() == null) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }
    }

    private User validationUpdate(long userId, UserDto userDto) {
        User user = userRepository.getUserById(userId);
        if (userDto.getName() != null) {
            if (userDto.getName().isBlank()) {
                throw new ValidationException("Имя пользователя не может быть пустым");
            }
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isBlank()) {
                throw new EmailException("Email не может быть пустым");
            }
            if (!user.getEmail().equals(userDto.getEmail()) && userRepository.isEmailExistInRepository(UserMapper
                    .toUser(userDto))) {
                throw new EmailException(String.format("%s уже существует в базе", userDto.getEmail()));
            }
            user.setEmail(userDto.getEmail());
        }
        return user;
    }
}
