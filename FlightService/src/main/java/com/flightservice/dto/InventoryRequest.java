package com.flightservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {
    @NotBlank
    private String airlineName;
    private String logoUrl;
    @NotBlank
    private String fromPlace;
    @NotBlank
    private String toPlace;
    @NotNull
    private LocalDateTime departureTime;
    @NotNull
    private LocalDateTime arrivalTime;
    @NotNull
    @PositiveOrZero
    private Double price;
    @NotNull
    @Positive
    private Integer totalSeats;
}
