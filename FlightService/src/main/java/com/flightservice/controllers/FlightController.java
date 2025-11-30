package com.flightservice.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flightservice.dto.InventoryRequest;
import com.flightservice.dto.SearchRequest;
import com.flightservice.entity.FlightInventory;
import com.flightservice.services.FlightService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/flight")
public class FlightController {
	private final FlightService flightService;
	
	@PostMapping("/inventory")
	public ResponseEntity<FlightInventory> add(@Valid @RequestBody InventoryRequest request){
		FlightInventory saved = flightService.addInventory(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}
	
	@PostMapping("/search")
	public ResponseEntity<List<FlightInventory>> search(@RequestBody SearchRequest req) {
        List<FlightInventory> results = flightService.searchFlights(req);
        return ResponseEntity.ok(results);
    }
	
    @GetMapping("/{id}/availability")
    public ResponseEntity<Integer> availability(@PathVariable Long id) {
        Integer avail = flightService.getAvailability(id);
        if (avail == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(avail);
    }
    
    @PutMapping("/{id}/reserve/{seats}")
    public ResponseEntity<Void> reserve(@PathVariable Long id,
                                        @PathVariable int seats) {
        boolean ok = flightService.reserveSeats(id, seats);
        if (!ok) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/release/{seats}")
    public ResponseEntity<Void> release(@PathVariable Long id,
                                        @PathVariable int seats) {
        boolean ok = flightService.releaseSeats(id, seats);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().build();
    }

}
