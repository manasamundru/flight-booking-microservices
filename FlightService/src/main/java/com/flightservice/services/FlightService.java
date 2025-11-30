package com.flightservice.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.flightservice.dto.InventoryRequest;
import com.flightservice.dto.SearchRequest;
import com.flightservice.entity.Airline;
import com.flightservice.entity.FlightInventory;
import com.flightservice.repository.AirlineRepository;
import com.flightservice.repository.FlightInventoryRepository;

import jakarta.transaction.Transactional;

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
	
	@Transactional
	public FlightInventory addInventory(InventoryRequest req) {
		Airline airline = airlineRepository.findByName(req.getAirlineName()).orElseGet(() -> airlineRepository
				.save(Airline.builder()
						.name(req.getAirlineName())
						.logoUrl(req.getLogoUrl())
						.build()));
		FlightInventory flight = FlightInventory.builder()
				.airline(airline)
				.fromPlace(req.getFromPlace())
				.toPlace(req.getToPlace())
				.departureTime(req.getDepartureTime())
				.arrivalTime(req.getArrivalTime())
				.price(req.getPrice())
				.totalSeats(req.getTotalSeats())
				.seatsAvailable(req.getTotalSeats())
				.build();
		return inventoryRepository.save(flight);
	}
	
    public Integer getAvailability(Long flightId) {
        return inventoryRepository.findById(flightId)
                .map(FlightInventory::getSeatsAvailable)
                .orElse(null);
    }

    @Transactional
    public boolean reserveSeats(Long flightId, int count) {
        return inventoryRepository.findById(flightId).map(f -> {
            if (f.getSeatsAvailable() >= count) {
                f.setSeatsAvailable(f.getSeatsAvailable() - count);
                inventoryRepository.save(f);
                return true;
            }
            return false;
        }).orElse(false);
    }

    @Transactional
    public boolean releaseSeats(Long flightId, int count) {
        return inventoryRepository.findById(flightId).map(f -> {
            f.setSeatsAvailable(f.getSeatsAvailable() + count);
            inventoryRepository.save(f);
            return true;
        }).orElse(false);
    }
	
	

}
