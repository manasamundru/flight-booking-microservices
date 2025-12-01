package com.bookingservice.dto;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class BookingRequestDto {
    @NotNull
    private Long flightId;
    @NotNull
    private String email;
    @NotNull
    private String mealType;
    @NotNull
    private String journeyDate;
    @NotNull
    private List<PassengerDto> passengers;
}
