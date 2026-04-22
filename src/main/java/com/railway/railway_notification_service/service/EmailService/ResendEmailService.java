package com.railway.railway_notification_service.service.EmailService;

import com.railway.railway_notification_service.config.properties.EmailProperties;
import com.railway.railway_notification_service.model.EmailRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.email.provider", havingValue = "resend")
public class ResendEmailService implements EmailService {

  private static final String RESEND_API_URL = "https://api.resend.com";
  private static final String EMAILS_ENDPOINT = "/emails";

  private final EmailProperties.Resend resendConfig;
  private final EmailTemplateService templateService;
  private RestClient restClient;

  public ResendEmailService(EmailProperties emailProperties,
                            EmailTemplateService templateService) {
    this.resendConfig = emailProperties.getResend();
    this.templateService = templateService;
  }

  @PostConstruct
  private void init() {
    validateConfig();
    this.restClient = RestClient.builder()
      .baseUrl(RESEND_API_URL)
      .defaultHeader("Authorization", "Bearer " + resendConfig.getApiKey())
      .build();
    log.info("ResendEmailService initialized with from: {}",
      resendConfig.getFromAddress());
  }

  @Override
  public void sendEmail(EmailRequest request) {
    try {
      // Render HTML from Thymeleaf template
      String html = templateService.render(
        request.getEmailType(),
        request.getTemplateModel()
      );

      var body = Map.of(
        "from", resendConfig.getFromAddress(),
        "to", List.of(request.getTo()),
        "subject", request.getSubject(),
        "html", html
      );

      restClient.post()
        .uri(EMAILS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body)
        .retrieve()
        .toBodilessEntity();

      log.info("✅ Email sent type={} to={}",
        request.getEmailType(), request.getTo());

    } catch (Exception e) {
      // WHY not rethrow:
      // Email failure is non-critical. Kafka consumer should not
      // retry infinitely for a transient email failure.
      // Cron will retry naturally in 2 days.
      log.error("❌ Failed to send email type={} to={} error={}",
        request.getEmailType(), request.getTo(), e.getMessage());
    }
  }

  private void validateConfig() {
    if (resendConfig.getApiKey() == null || resendConfig.getApiKey().isBlank()) {
      throw new IllegalStateException(
        "Resend API key is not configured (app.email.resend.api-key)");
    }
    if (resendConfig.getFromAddress() == null || resendConfig.getFromAddress().isBlank()) {
      throw new IllegalStateException(
        "Resend from address is not configured (app.email.resend.from-address)");
    }
  }
}
