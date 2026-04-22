// src/main/java/com/railway/railway_notification_service/consumer/AuthEventConsumer.java
package com.railway.railway_notification_service.consumer;

import com.railway.common.event.auth.EmailVerificationReminderEvent;
import com.railway.common.kafka.KafkaTopics;
import com.railway.railway_notification_service.model.EmailRequest;
import com.railway.railway_notification_service.model.EmailTemplateModel;
import com.railway.railway_notification_service.model.EmailType;
import com.railway.railway_notification_service.service.EmailService.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthEventConsumer {

  private final EmailService emailService;

  @Value("${app.frontend-url}")
  private String frontendUrl;

  @KafkaListener(
    topics = KafkaTopics.Auth.EMAIL_VERIFICATION_REMINDER,
    groupId = "notification-service-group"
  )
  public void handleEmailVerificationReminder(
    EmailVerificationReminderEvent event) {

    log.info("📩 Received verification reminder userId={} email={}",
      event.userId(), event.email());

    var request = EmailRequest.builder()
      .to(event.email())
      .subject("Please verify your RailTick email address")
      .emailType(EmailType.EMAIL_VERIFICATION_REMINDER)
      .templateModel(EmailTemplateModel.builder()
        .recipientName(event.fullName())
        .actionUrl(frontendUrl + "/verify-email")
        .build())
      .build();

    emailService.sendEmail(request);
  }
}
