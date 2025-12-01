package com.bookingservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String gender;
    private Integer age;
    private String seatNumber;
    @ManyToOne
    @JoinColumn(name = "pnr")
    private Booking booking;
}
