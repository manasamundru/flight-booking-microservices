package com.flightservice.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {
    private String fromPlace;
    private String toPlace;
    private LocalDate journeyDate;
}
