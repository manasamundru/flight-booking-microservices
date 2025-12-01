package com.bookingservice.controller;



import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.entity.Booking;
import com.bookingservice.services.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    @Mock
    private BookingService service;

    @InjectMocks
    private BookingController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testBookTicket() {
        BookingRequestDto req = new BookingRequestDto();
        Booking booking = new Booking();
        when(service.bookTicket(req)).thenReturn(booking);

        ResponseEntity<Booking> response = controller.book(req);

        assertEquals(200, response.getStatusCode().value());

        assertEquals(booking, response.getBody());
    }


    @Test
    void testGetTicket() {
        Booking booking = new Booking();
        when(service.getTicket("PNR123")).thenReturn(booking);

        ResponseEntity<Booking> response = controller.getTicket("PNR123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booking, response.getBody());
    }

    @Test
    void testHistory() {
        List<Booking> bookings = List.of(new Booking(), new Booking());
        when(service.history("test@test.com")).thenReturn(bookings);

        ResponseEntity<List<Booking>> response = controller.history("test@test.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(bookings, response.getBody());
    }

    @Test
    void testCancel() {
        doNothing().when(service).cancelTicket("PNR123");

        ResponseEntity<String> response = controller.cancel("PNR123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Ticket cancelled successfully", response.getBody());
       
    }
}

