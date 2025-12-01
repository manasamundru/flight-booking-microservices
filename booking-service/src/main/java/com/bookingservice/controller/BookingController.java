package com.bookingservice.controller;

import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.entity.Booking;
import com.bookingservice.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flight")
public class BookingController {
    private final BookingService service;
    @PostMapping("/booking")
    public ResponseEntity<Booking> book(@RequestBody BookingRequestDto req) {
        return ResponseEntity.ok(service.bookTicket(req));
    }
    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<Booking> getTicket(@PathVariable String pnr) {
        return ResponseEntity.ok(service.getTicket(pnr));
    }
    @GetMapping("/booking/history/{email}")
    public ResponseEntity<List<Booking>> history(@PathVariable String email) {
        return ResponseEntity.ok(service.history(email));
    }
    @DeleteMapping("/booking/cancel/{pnr}")
    public ResponseEntity<String> cancel(@PathVariable String pnr) {
        service.cancelTicket(pnr);
        return ResponseEntity.ok("Ticket cancelled successfully");
    }
}
