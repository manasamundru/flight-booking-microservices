package com.flightservice.services;


import com.flightservice.dto.InventoryRequest;
import com.flightservice.dto.SearchRequest;
import com.flightservice.entity.Airline;
import com.flightservice.entity.FlightInventory;
import com.flightservice.repository.AirlineRepository;
import com.flightservice.repository.FlightInventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    @Mock
    private AirlineRepository airlineRepo;

    @Mock
    private FlightInventoryRepository invRepo;

    @InjectMocks
    private FlightService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSearchFlights() {
        SearchRequest req = SearchRequest.builder()
                .fromPlace("Delhi")
                .toPlace("Mumbai")
                .journeyDate(LocalDate.now())
                .build();

        List<FlightInventory> expected = List.of(new FlightInventory());
        when(invRepo.findByFromPlaceIgnoreCaseAndToPlaceIgnoreCaseAndDepartureTimeBetween(
                anyString(), anyString(), any(), any()
        )).thenReturn(expected);

        List<FlightInventory> result = service.searchFlights(req);

        assertEquals(expected.size(), result.size());
    }

    @Test
    void testAddInventory_airlineExists() {
        InventoryRequest req = InventoryRequest.builder()
                .airlineName("Indigo")
                .logoUrl("logo.png")
                .fromPlace("Delhi")
                .toPlace("Hyd")
                .departureTime(LocalDateTime.now())
                .arrivalTime(LocalDateTime.now().plusHours(2))
                .price(4000.0)
                .totalSeats(100)
                .build();

        Airline existing = Airline.builder().id(1L).name("Indigo").build();

        when(airlineRepo.findByName("Indigo")).thenReturn(Optional.of(existing));

        FlightInventory saved = new FlightInventory();
        when(invRepo.save(any())).thenReturn(saved);

        FlightInventory result = service.addInventory(req);

        assertEquals(saved, result);
        verify(airlineRepo, never()).save(any());
    }

    @Test
    void testAddInventory_airlineNotExists() {
        InventoryRequest req = InventoryRequest.builder()
                .airlineName("NewAir")
                .logoUrl("xyz.png")
                .fromPlace("Delhi")
                .toPlace("Hyd")
                .departureTime(LocalDateTime.now())
                .arrivalTime(LocalDateTime.now().plusHours(1))
                .price(3500.0)
                .totalSeats(80)
                .build();

        when(airlineRepo.findByName("NewAir")).thenReturn(Optional.empty());
        when(airlineRepo.save(any())).thenReturn(new Airline());
        when(invRepo.save(any())).thenReturn(new FlightInventory());

        FlightInventory result = service.addInventory(req);

        assertNotNull(result);
        verify(airlineRepo).save(any());
    }

    @Test
    void testGetAvailability_found() {
        FlightInventory inv = new FlightInventory();
        inv.setSeatsAvailable(50);

        when(invRepo.findById(1L)).thenReturn(Optional.of(inv));

        assertEquals(50, service.getAvailability(1L));
    }

    @Test
    void testGetAvailability_notFound() {
        when(invRepo.findById(1L)).thenReturn(Optional.empty());

        assertNull(service.getAvailability(1L));
    }

    @Test
    void testReserveSeats_success() {
        FlightInventory inv = new FlightInventory();
        inv.setSeatsAvailable(50);

        when(invRepo.findById(1L)).thenReturn(Optional.of(inv));

        assertTrue(service.reserveSeats(1L, 10));
        assertEquals(40, inv.getSeatsAvailable());
        verify(invRepo).save(inv);
    }

    @Test
    void testReserveSeats_insufficient() {
        FlightInventory inv = new FlightInventory();
        inv.setSeatsAvailable(5);

        when(invRepo.findById(1L)).thenReturn(Optional.of(inv));

        assertFalse(service.reserveSeats(1L, 10));
        verify(invRepo, never()).save(any());
    }

    @Test
    void testReserveSeats_notFound() {
        when(invRepo.findById(1L)).thenReturn(Optional.empty());

        assertFalse(service.reserveSeats(1L, 10));
    }

    @Test
    void testReleaseSeats_success() {
        FlightInventory inv = new FlightInventory();
        inv.setSeatsAvailable(30);

        when(invRepo.findById(1L)).thenReturn(Optional.of(inv));

        assertTrue(service.releaseSeats(1L, 5));
        assertEquals(35, inv.getSeatsAvailable());
        verify(invRepo).save(inv);
    }

    @Test
    void testReleaseSeats_notFound() {
        when(invRepo.findById(1L)).thenReturn(Optional.empty());

        assertFalse(service.releaseSeats(1L, 5));
    }
}
