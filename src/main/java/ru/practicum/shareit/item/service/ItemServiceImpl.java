package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoOwner;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto addItem(long userId, ItemDto itemDto) {
        User owner = userRepository.checkUser(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item itemAfterSave = itemRepository.save(item);
        return ItemMapper.toItemDto(itemAfterSave);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = validationUpdate(userId, itemId, itemDto);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItemById(long userId, long itemId) {
        Item item = itemRepository.checkItem(itemId);
        ItemDto itemDto = ItemMapper.toItemDtoForOwner(item);
        if (userId == (item.getOwner().getId())) {
            itemDto.setLastBooking(bookingService.getLastBooking(itemId));
            itemDto.setNextBooking(bookingService.getNextBooking(itemId));
        } else {
            itemDto = ItemMapper.toItemDto(item);
        }
        itemDto.setComments(commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        return itemDto;
    }

    public List<ItemDto> getItemsByUserId(long userId) {
        userRepository.checkUser(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);

        Map<Item, List<Comment>> commentsMap = commentRepository.findAllByItemIn(items, Sort.by(Sort.Order.desc("created")))
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        Map<Item, List<Booking>> bookingsMap = bookingRepository.findAllByItemInAndStatus(items, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));

        List<ItemDto> itemsDto = items.stream()
                .map(item -> {
                    ItemDto itemDto = ItemMapper.toItemDtoForOwner(item);

                    List<Booking> bookingByItem = bookingsMap.getOrDefault(item, Collections.emptyList());
                    itemDto.setLastBooking(bookingByItem.stream()
                            .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                            .map(BookingMapper::toBookingDtoForOwner)
                            .max(Comparator.comparing(BookingDtoOwner::getEnd))
                            .orElse(null));
                    itemDto.setNextBooking(bookingByItem.stream()
                            .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                            .map(BookingMapper::toBookingDtoForOwner)
                            .min(Comparator.comparing(BookingDtoOwner::getStart))
                            .orElse(null));

                    itemDto.setComments(commentsMap.getOrDefault(item, Collections.emptyList())
                            .stream()
                            .map(CommentMapper::toCommentDto)
                            .collect(Collectors.toList()));

                    return itemDto;
                })
                .collect(Collectors.toList());

        return itemsDto;
    }


    @Override
    public List<ItemDto> getItemsByText(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.getItemByText(text.toLowerCase());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        User author = userRepository.checkUser(userId);
        Item item = itemRepository.checkItem(itemId);
        checkAuthor(userId, itemId);
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, author, item));
        return CommentMapper.toCommentDto(comment);
    }

    private void checkAuthor(long userId, long itemId) {
        boolean booking = bookingRepository.existsByItemIdAndBookerIdAndEndIsBeforeAndStatus(itemId,
                userId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (booking == false) {
            throw new ValidationException("Пользователь не может оставить комментарий");
        }
    }

    private Item validationUpdate(long userId, long itemId, ItemDto itemDto) {
        userRepository.checkUser(userId);
        Item item = itemRepository.checkItem(itemId);
        User owner = item.getOwner();
        if (owner.getId() != userId) {
            throw new NotFoundException("Пользователь не является собственником вещи и не имеет прав на её изменение");
        }
        if (itemDto.getName() != null) {
            if (itemDto.getName().isBlank()) {
                throw new ValidationException("Имя вещи не должно быть пустым");
            }
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            if (itemDto.getDescription().isBlank()) {
                throw new ValidationException("Описание вещи не должно быть пустым");
            }
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return item;
    }
}
