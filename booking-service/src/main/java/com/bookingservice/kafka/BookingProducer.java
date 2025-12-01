package com.bookingservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingProducer {

    private final KafkaTemplate<String, BookingEvent> kafkaTemplate;

    public void sendBookingEvent(BookingEvent event) {
        kafkaTemplate.send("ticket-booked", event);
    }
}
