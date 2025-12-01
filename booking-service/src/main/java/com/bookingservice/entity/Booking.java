package com.bookingservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Booking {

    @Id
    private String pnr;

    private Long flightId;
    private String email;
    private Integer totalSeats;
    private String mealType;
    private LocalDateTime bookingTime;
    private LocalDateTime journeyDate;
    private String status;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Passenger> passengers;
}
