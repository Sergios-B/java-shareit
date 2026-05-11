package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.BaseUnitTest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest extends BaseUnitTest {

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final long bookingId = 1L;

    @Test
    void shouldReturnStatus404_whenUserHeaderIsMissing() throws Exception {

        CreateBookingDto createItemDto = new CreateBookingDto();

        mockMvc.perform(
                        post("/bookings")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createItemDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").isNotEmpty())
                .andExpect(jsonPath("$.error").value(NOT_MISSING_HEADER));

        verify(bookingClient, never()).createBooking(USER_ID, createItemDto);
    }

    @Test
    void shouldReturnStatus201_whenBookingCreated() throws Exception {

        CreateBookingDto createItemDto = new CreateBookingDto();
        createItemDto.setStart(LocalDateTime.now().plusHours(5));
        createItemDto.setEnd(LocalDateTime.now().plusDays(2));
        createItemDto.setItemId(1L);

        when(bookingClient.createBooking(anyLong(), any(CreateBookingDto.class)))
                .thenReturn(ResponseEntity.status(HttpStatus.CREATED).build());

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(createItemDto))
                                .headers(getHeaders())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated());

        verify(bookingClient).createBooking(eq(USER_ID), any(CreateBookingDto.class));
    }

    @Test
    void shouldReturnStatus200_whenBookingUpdated() throws Exception {

        boolean approved = true;

        when(bookingClient.updateBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                        patch("/bookings/{bookingId}", bookingId)
                                .param("approved", String.valueOf(approved))
                                .contentType(MediaType.APPLICATION_JSON)
                                .headers(getHeaders())
                                .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk());

        verify(bookingClient).updateBooking(bookingId, USER_ID, approved);
    }

    @Test
    void shouldReturnStatus200_whenGetBooking() throws Exception {

        when(bookingClient.getBooking(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                get("/bookings/{bookingId}", bookingId)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(getHeaders())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(bookingClient).getBooking(USER_ID, bookingId);
    }

    @Test
    void shouldReturnStatus200_whenGetBookingsAll() throws Exception {

        when(bookingClient.getBookings(anyLong(), any(BookingState.class), anyInt(), anyInt(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(
                get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(getHeaders())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(bookingClient).getBookings(USER_ID, BookingState.ALL, 0, 10, false);
    }
}