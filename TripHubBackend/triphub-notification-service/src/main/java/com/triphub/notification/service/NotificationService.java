package com.triphub.notification.service;

import com.triphub.notification.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public String sendEmail(NotificationRequest request) {
        log.info("Sending email to {} with subject {}", request.getRecipient(), request.getSubject());
        return "Email sent successfully";
    }
}
