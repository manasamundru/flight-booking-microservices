package com.bookingservice.kafka;

import lombok.*;
import java.util.List;

@Getter
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class BookingEvent {
    private String pnr;
    private Long flightId;
    private String email;
    private String mealType;
    private String journeyDate;
    private List<PassengerEvent> passengers;
}