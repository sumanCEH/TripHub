# TripHub Backend

This repository contains the initial backend foundation for the TripHub event management platform using a Spring Boot microservices architecture.

## Modules

- triphub-shared: common models, exceptions, response wrapper
- triphub-gateway: API gateway
- triphub-identity-service: authentication and user management
- triphub-event-service: events and admin management
- triphub-booking-service: registrations and reservation flow
- triphub-payment-service: order and payment orchestration
- triphub-ticket-service: ticket issuance and validation
- triphub-notification-service: event-driven notifications

## Run

```bash
mvn clean install
```

Then start services individually.
