package com.bookingservice.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "flight-service")
public interface FlightClient {

    @GetMapping("/api/flight/{id}/availability")
    Integer checkAvailability(@PathVariable Long id);
    @PutMapping("/api/flight/{id}/reserve/{seats}")
    void reserve(@PathVariable Long id, @PathVariable int seats);
    @PutMapping("/api/flight/{id}/release/{seats}")
    void release(@PathVariable Long id, @PathVariable int seats);
}
