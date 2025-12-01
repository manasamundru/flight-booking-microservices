package com.bookingservice.service;


import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.dto.PassengerDto;
import com.bookingservice.entity.Booking;
import com.bookingservice.feign.FlightClient;
import com.bookingservice.kafka.BookingEvent;
import com.bookingservice.kafka.BookingProducer;
import com.bookingservice.repository.BookingRepository;
import com.bookingservice.services.BookingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private FlightClient flightClient;

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private BookingProducer producer;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookTicket_success() {

        BookingRequestDto req = BookingRequestDto.builder()
                .flightId(1L)
                .email("test@test.com")
                .mealType("VEG")
                .journeyDate("2025-02-01")
                .passengers(List.of(
                        PassengerDto.builder()
                                .name("John").gender("M").age(30).seatNumber("1A")
                                .build()))
                .build();

        when(flightClient.checkAvailability(1L)).thenReturn(10);

        Booking saved = Booking.builder()
                .pnr("ABCDEFGH")
                .flightId(1L)
                .email("test@test.com")
                .passengers(List.of())
                .journeyDate(LocalDateTime.now())
                .bookingTime(LocalDateTime.now())
                .status("BOOKED")
                .build();

        when(bookingRepo.save(any())).thenReturn(saved);

        Booking result = bookingService.bookTicket(req);

        assertNotNull(result);
        verify(flightClient).reserve(eq(1L), eq(1));
        verify(bookingRepo).save(any());
        verify(producer).sendBookingEvent(any(BookingEvent.class));
    }

    @Test
    void testBookTicket_insufficientSeats() {
        BookingRequestDto req = BookingRequestDto.builder()
                .flightId(1L)
                .passengers(List.of(PassengerDto.builder().build()))
                .build();

        when(flightClient.checkAvailability(1L)).thenReturn(0);

        assertThrows(RuntimeException.class, () -> bookingService.bookTicket(req));
        verify(flightClient, never()).reserve(anyLong(), anyInt());
    }

    @Test
    void testFlightUnavailableFallback() {
        BookingRequestDto req = new BookingRequestDto();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> bookingService.flightUnavailable(req, new RuntimeException("down")));

        assertEquals("Flight Service down. Try after some time.", ex.getMessage());
    }

    @Test
    void testGetTicket_found() {
        Booking booking = new Booking();
        when(bookingRepo.findById("PNR1")).thenReturn(Optional.of(booking));

        Booking result = bookingService.getTicket("PNR1");

        assertEquals(booking, result);
    }

    @Test
    void testGetTicket_notFound() {
        when(bookingRepo.findById("PNR1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> bookingService.getTicket("PNR1"));
    }
    @Test
    void testCancelTicket() {

        Booking booking = Booking.builder()
                .pnr("PNR1")
                .flightId(1L)
                .totalSeats(2)
                .status("BOOKED")
                .build();

        when(bookingRepo.findById("PNR1")).thenReturn(Optional.of(booking));

        bookingService.cancelTicket("PNR1");

        assertEquals("CANCELLED", booking.getStatus());
        verify(bookingRepo).save(booking);
        verify(flightClient).release(1L, 2);
    }
    @Test
    void testHistory() {
        List<Booking> bookings = List.of(new Booking());
        when(bookingRepo.findByEmail("test@test.com")).thenReturn(bookings);

        List<Booking> result = bookingService.history("test@test.com");

        assertEquals(1, result.size());
    }
}

