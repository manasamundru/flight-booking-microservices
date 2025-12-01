package com.flightservice.controller;


import com.flightservice.controllers.FlightController;
import com.flightservice.dto.InventoryRequest;
import com.flightservice.dto.SearchRequest;
import com.flightservice.entity.FlightInventory;
import com.flightservice.services.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightControllerTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddInventory_success() {
        InventoryRequest req = InventoryRequest.builder()
                .airlineName("Air India")
                .fromPlace("Delhi")
                .toPlace("Mumbai")
                .departureTime(LocalDateTime.now())
                .arrivalTime(LocalDateTime.now().plusHours(2))
                .price(5000.0)
                .totalSeats(100)
                .build();

        FlightInventory saved = new FlightInventory();
        when(flightService.addInventory(req)).thenReturn(saved);

        ResponseEntity<FlightInventory> response = controller.add(req);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved, response.getBody());
    }

    @Test
    void testSearchFlights_success() {
        SearchRequest req = SearchRequest.builder()
                .fromPlace("Delhi")
                .toPlace("Mumbai")
                .journeyDate(LocalDate.now())
                .build();

        List<FlightInventory> mockList = List.of(new FlightInventory());
        when(flightService.searchFlights(req)).thenReturn(mockList);

        ResponseEntity<List<FlightInventory>> response = controller.search(req);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockList, response.getBody());
    }


    @Test
    void testAvailability_found() {
        when(flightService.getAvailability(1L)).thenReturn(20);

        ResponseEntity<Integer> response = controller.availability(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(20, response.getBody());
    }

    @Test
    void testAvailability_notFound() {
        when(flightService.getAvailability(1L)).thenReturn(null);

        ResponseEntity<Integer> response = controller.availability(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testReserve_success() {
        when(flightService.reserveSeats(1L, 5)).thenReturn(true);

        ResponseEntity<Void> response = controller.reserve(1L, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testReserve_conflict() {
        when(flightService.reserveSeats(1L, 5)).thenReturn(false);

        ResponseEntity<Void> response = controller.reserve(1L, 5);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }


    @Test
    void testRelease_success() {
        when(flightService.releaseSeats(1L, 5)).thenReturn(true);

        ResponseEntity<Void> response = controller.release(1L, 5);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRelease_notFound() {
        when(flightService.releaseSeats(1L, 5)).thenReturn(false);

        ResponseEntity<Void> response = controller.release(1L, 5);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

