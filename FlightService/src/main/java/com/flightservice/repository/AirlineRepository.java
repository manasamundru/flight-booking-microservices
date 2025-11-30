package com.flightservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flightservice.entity.Airline;

public interface AirlineRepository extends JpaRepository<Airline,Long> {
	Optional<Airline> findByName(String name);
}
