package com.flightservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "airline")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Airline {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String name;
	private String logoUrl;
}
