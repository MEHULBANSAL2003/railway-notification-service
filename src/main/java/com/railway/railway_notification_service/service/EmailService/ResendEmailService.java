package com.railway.railway_notification_service.service.EmailService;


import com.railway.railway_notification_service.config.properties.EmailProperties;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
@ConditionalOnProperty(name = "app.email.provider", havingValue = "resend")
public class ResendEmailService implements EmailService{

  private static final String RESEND_API_URL = "https://api.resend.com";

  private final EmailProperties.Resend resendConfig;
  private RestClient restClient;

  public ResendEmailService(EmailProperties emailProperties) {
    this.resendConfig = emailProperties.getResend();
  }

  @PostConstruct
  private void init() {
    validateConfig();
    this.restClient = RestClient.builder()
      .baseUrl(RESEND_API_URL)
      .defaultHeader("Authorization", "Bearer " + resendConfig.getApiKey())
      .defaultHeader("Content-Type", "application/json")
      .build();

    log.info("ResendEmailService initialized with from: {}", resendConfig.getFromAddress());
  }

  @Override
  public void sendEmail(String to, String message, String subject) {

  }


  private void validateConfig() {
    if (resendConfig.getApiKey() == null || resendConfig.getApiKey().isBlank()) {
      throw new IllegalStateException("Resend API key is not configured (app.email.resend.api-key)");
    }
    if (resendConfig.getFromAddress() == null || resendConfig.getFromAddress().isBlank()) {
      throw new IllegalStateException("Resend from address is not configured (app.email.resend.from-address)");
    }
  }

}
