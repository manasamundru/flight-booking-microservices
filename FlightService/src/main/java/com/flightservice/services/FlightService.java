package com.flightservice.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.flightservice.dto.SearchRequest;
import com.flightservice.entity.FlightInventory;
import com.flightservice.repository.AirlineRepository;
import com.flightservice.repository.FlightInventoryRepository;

@Service
public class FlightService {
	private final AirlineRepository airlineRepository;
	private final FlightInventoryRepository inventoryRepository;

	public FlightService(AirlineRepository airlineRepository, FlightInventoryRepository inventoryRepository) {
		this.airlineRepository = airlineRepository;
		this.inventoryRepository = inventoryRepository;
	}

	public List<FlightInventory> searchFlights(SearchRequest req) {

		LocalDate journeyDate = req.getJourneyDate();
		LocalDateTime start = journeyDate.atStartOfDay();
		LocalDateTime end = journeyDate.atTime(LocalTime.MAX);
		return inventoryRepository.findByFromPlaceIgnoreCaseAndToPlaceIgnoreCaseAndDepartureTimeBetween(
				req.getFromPlace(), req.getToPlace(), start, end);
	}

}
