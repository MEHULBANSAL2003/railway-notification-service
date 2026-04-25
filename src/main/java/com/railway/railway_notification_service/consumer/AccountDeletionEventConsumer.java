package com.railway.railway_notification_service.consumer;


import com.railway.common.event.auth.AccountDeletionEvent;
import com.railway.common.event.auth.AccountDeletionRequestEvent;
import com.railway.common.kafka.KafkaTopics;
import com.railway.railway_notification_service.model.EmailRequest;
import com.railway.railway_notification_service.model.EmailTemplateModel;
import com.railway.railway_notification_service.model.EmailType;
import com.railway.railway_notification_service.service.EmailService.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountDeletionEventConsumer {

  private final EmailService emailService;

  @Value("${app.frontend-url}")
  private String frontendUrl;

  @KafkaListener(
    topics = KafkaTopics.Auth.ACCOUNT_DELETION_REQUEST,
    groupId = "notification-service-group"
  )
  public void handleAccountDeletionRequest(AccountDeletionRequestEvent event) {

    // Restore correlationId from event onto this Kafka listener thread
    // WHY: Kafka listener thread has empty MDC by default
    // This makes all logs in this method traceable to the original request
    String correlationId = event.correlationId();
    if (correlationId != null) {
      MDC.put("correlationId", correlationId);
    } else {
      // Fallback: generate fresh one if not present
      // Handles events published before this change was deployed
      MDC.put("correlationId", java.util.UUID.randomUUID().toString());
    }

    try {
      log.info("📩 Received account deletion request userId={} email={}",
        event.userId(), event.email());

      var request = EmailRequest.builder()
        .to(event.email())
        .subject("Your RailTick account deletion request")
        .emailType(EmailType.ACCOUNT_DELETION_REQUEST)
        .templateModel(EmailTemplateModel.builder()
          .recipientName(event.fullName())
          .actionUrl(frontendUrl + "/login")
          .recoverPeriodDays(event.recoverPeriod())
          .build())
        .build();

      emailService.sendEmail(request);

    } finally {
      // WHY finally: Kafka reuses listener threads
      // Must clean MDC after each message or correlationId
      // bleeds into the next consumed message on same thread
      MDC.clear();
    }
  }

  @KafkaListener(
    topics = KafkaTopics.Auth.ACCOUNT_DELETION,
    groupId = "notification-service-group"
  )
  public void handleAccountDeletion(AccountDeletionEvent event) {

    // Restore correlationId from event onto this Kafka listener thread
    // WHY: Kafka listener thread has empty MDC by default
    // This makes all logs in this method traceable to the original request
    String correlationId = event.correlationId();
    if (correlationId != null) {
      MDC.put("correlationId", correlationId);
    } else {
      // Fallback: generate fresh one if not present
      // Handles events published before this change was deployed
      MDC.put("correlationId", java.util.UUID.randomUUID().toString());
    }

    try {
      log.info("📩 Received account deletion confirmation userId={} email={}",
        event.userId(), event.email());

      var request = EmailRequest.builder()
        .to(event.email())
        .subject("Your RailTick account has been deleted")
        .emailType(EmailType.ACCOUNT_DELETE_PERMANENT)
        .templateModel(EmailTemplateModel.builder()
          .recipientName(event.fullName())
          .build())
        .build();

      emailService.sendEmail(request);

    } finally {
      // WHY finally: Kafka reuses listener threads
      // Must clean MDC after each message or correlationId
      // bleeds into the next consumed message on same thread
      MDC.clear();
    }
  }
}
