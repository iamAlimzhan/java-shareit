package ru.practicum.shareit.booking;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.HeaderConstants;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.InputBookingDto;
import ru.practicum.shareit.booking.position.BookingPosition;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.status.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    private final int from = 0;
    private final int size = 10;
    private final BookingPosition position = BookingPosition.ALL;
    private final InputBookingDto bookingDtoFrontend = new InputBookingDto(null, LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusMinutes(20));
    private final UserDto userDto = new UserDto(1L, "name", "name.na@mail.com");
    private final ItemDto itemDto = new ItemDto(null, "name2", "desc", null,
            null, null, null, null);
    private final BookingDto bookingDto = new BookingDto(1L, userDto, itemDto, null, null, BookingStatus.APPROVED);
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mvc;

    @Test
    @SneakyThrows
    void updateBooking_ShouldReturnBookingDto() {
        boolean approved = true;
        when(bookingService.updateBooking(1L, 1L, approved))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header(HeaderConstants.X_SHARER_USER_ID, 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    @SneakyThrows
    void findById_ShouldReturnBookingDto() {
        when(bookingService.findById(1L, 1L))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(HeaderConstants.X_SHARER_USER_ID, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString()), String.class));
    }

    @Test
    @SneakyThrows
    void findAllBookingsByUserId_ShouldReturnListOfBookingDto() {
        when(bookingService.findAllBookingsByUserId(1L, position, from, size))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    @SneakyThrows
    void findAllBookingsByItemOwner_ShouldReturnListOfBookingDto() {
        when(bookingService.findAllBookingsByOwner(1L, position, from, size))
                .thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingDto.getBooker().getId()), Long.class));
    }

    @Test
    void updateBooking_WithInvalidApprovedParameter_ShouldReturnBadRequest() throws Exception {
        mvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", 1)
                        .header(HeaderConstants.X_SHARER_USER_ID, 1)
                        .param("approved", "invalidValue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
