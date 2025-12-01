package com.bookingservice.services;

import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.entity.Booking;
import com.bookingservice.entity.Passenger;
import com.bookingservice.feign.FlightClient;
import com.bookingservice.kafka.BookingEvent;
import com.bookingservice.kafka.BookingProducer;
import com.bookingservice.kafka.PassengerEvent;
import com.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final FlightClient flightClient;
    private final BookingRepository bookingRepo;
    private final BookingProducer producer;

    @CircuitBreaker(name = "flightService", fallbackMethod = "flightUnavailable")
    public Booking bookTicket(BookingRequestDto req) {

        int seats = req.getPassengers().size();
        Integer available = flightClient.checkAvailability(req.getFlightId());
        if (available < seats)
            throw new RuntimeException("Not enough seats available");
        flightClient.reserve(req.getFlightId(), seats);
        String pnr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = Booking.builder()
                .pnr(pnr)
                .flightId(req.getFlightId())
                .email(req.getEmail())
                .mealType(req.getMealType())
                .totalSeats(seats)
                .journeyDate(LocalDate.parse(req.getJourneyDate()).atStartOfDay())
                .bookingTime(LocalDateTime.now())
                .status("BOOKED")
                .build();
        List<Passenger> passengers = req.getPassengers().stream()
                .map(p -> Passenger.builder()
                        .name(p.getName())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .booking(booking)
                        .build())
                .toList();
        booking.setPassengers(passengers);
        Booking saved = bookingRepo.save(booking);
        List<PassengerEvent> passengerEvents = passengers.stream()
                .map(p -> PassengerEvent.builder()
                        .name(p.getName())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .seatNumber(p.getSeatNumber())
                        .build())
                .toList();
        BookingEvent event = BookingEvent.builder()
                .pnr(saved.getPnr())
                .flightId(saved.getFlightId())
                .email(saved.getEmail())
                .mealType(saved.getMealType())
                .journeyDate(req.getJourneyDate())
                .passengers(passengerEvents)
                .build();
        producer.sendBookingEvent(event);
        return saved;
    }

    public Booking flightUnavailable(BookingRequestDto req, Throwable t) {
        throw new RuntimeException("Flight Service down. Try after some time.");
    }

    public Booking getTicket(String pnr) {
        return bookingRepo.findById(pnr)
                .orElseThrow(() -> new RuntimeException("PNR not found"));
    }

    public void cancelTicket(String pnr) {
        Booking booking = getTicket(pnr);
        booking.setStatus("CANCELLED");
        bookingRepo.save(booking);
        flightClient.release(booking.getFlightId(), booking.getTotalSeats());
    }

    public List<Booking> history(String email) {
        return bookingRepo.findByEmail(email);
    }
}
