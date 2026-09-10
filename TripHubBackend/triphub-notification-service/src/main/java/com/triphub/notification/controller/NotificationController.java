package com.triphub.notification.controller;

import com.triphub.notification.dto.NotificationRequest;
import com.triphub.notification.service.NotificationService;
import com.triphub.shared.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/email")
    public ResponseEntity<ApiResponse<String>> sendEmail(@Valid @RequestBody NotificationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.sendEmail(request), "Notification sent"));
    }
}
