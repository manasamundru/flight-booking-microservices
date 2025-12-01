package com.emailservice.kafka;

import lombok.*;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class PassengerEvent {
    private String name;
    private String gender;
    private Integer age;
    private String seatNumber;
}
