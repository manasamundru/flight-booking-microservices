package com.emailservice.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailConsumer {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "ticket-booked", groupId = "email-group")
    public void consume(BookingEvent event) {
        StringBuilder body = new StringBuilder();
        body.append("Dear Customer,\n\n");
        body.append("Your booking is confirmed.\n\n");
        body.append("PNR: ").append(event.getPnr()).append("\n");
        body.append("Flight ID: ").append(event.getFlightId()).append("\n");
        body.append("Journey Date: ").append(event.getJourneyDate()).append("\n");
        body.append("Meal Type: ").append(event.getMealType()).append("\n\n");
        body.append("Passenger Details:\n");
        for (PassengerEvent p : event.getPassengers()) {
            body.append("- ").append(p.getName())
                .append(", ").append(p.getGender())
                .append(", Age: ").append(p.getAge())
                .append(", Seat: ").append(p.getSeatNumber())
                .append("\n");
        }
        body.append("\nThank you for choosing us.\n");

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(event.getEmail());
            msg.setSubject("Booking Confirmation - PNR " + event.getPnr());
            msg.setText(body.toString());
            mailSender.send(msg);
            System.out.println("Email sent for PNR: " + event.getPnr());
        } catch (Exception ex) {
            System.err.println("Failed to send email for PNR: " + event.getPnr() + " - " + ex.getMessage());
        }
    }
}
