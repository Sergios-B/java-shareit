package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest extends BaseUnitTest {

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final long mockBookingId = 1L;

    private BookingDto bookingDto;

    private UserDto bookingUser;

    @BeforeEach
    void setUp() {

        bookingUser = UserDto.builder().id(USER_ID).name("Petia").email("example@mail.com").build();

        bookingDto = BookingDto.builder()
                .id(mockBookingId)
                .booker(bookingUser)
                .build();
    }


    @Test
    void shouldReturnBooking_whenCreated() throws Exception {

        CreateBookingDto createBookingDto = new CreateBookingDto();

        when(bookingService.createBooking(any(CreateBookingDto.class), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(createBookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                                .characterEncoding(StandardCharsets.UTF_8)

                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockBookingId))
                .andExpect(jsonPath("$.booker.name").value(bookingUser.getName()));

        verify(bookingService).createBooking(any(CreateBookingDto.class), eq(USER_ID));
    }

    @Test
    void shouldReturnBooking_whenUpdated() throws Exception {

        when(bookingService.updateBookingApproved(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(
                        patch("/bookings/{bookingId}", mockBookingId)
                                .param("approved", "true")
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBookingId))
                .andExpect(jsonPath("$.booker.name").value(bookingUser.getName()));

        verify(bookingService).updateBookingApproved(eq(mockBookingId), anyBoolean(), eq(USER_ID));
    }

    @Test
    void shouldReturnBooking_whenFindById() throws Exception {

        when(bookingService.findBookingById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(
                        get("/bookings/{bookingId}", mockBookingId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockBookingId))
                .andExpect(jsonPath("$.booker.name").value(bookingUser.getName()));

        verify(bookingService).findBookingById(eq(mockBookingId), eq(USER_ID));
    }

    @Test
    void shouldReturnBookings_whenFindByUserId() throws Exception {

        List<BookingDto> bookingDtoList = List.of(
                BookingDto.builder().id(1L).booker(bookingUser).build(),
                BookingDto.builder().id(2L).booker(bookingUser).build(),
                BookingDto.builder().id(3L).booker(bookingUser).build()
        );

        when(bookingService.findBookingByState(anyLong(), any(BookingState.class), anyBoolean()))
                .thenReturn(bookingDtoList);

        mockMvc.perform(
                        get("/bookings", mockBookingId)
                                .param("state", "ALL")
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                                .characterEncoding(StandardCharsets.UTF_8)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(bookingDtoList.size()))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].booker.id").value(bookingUser.getId()))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].booker.id").value(bookingUser.getId()))
                .andExpect(jsonPath("$[2].id").value(3L));

        verify(bookingService).findBookingByState(eq(USER_ID), eq(BookingState.ALL), eq(false));
    }
}