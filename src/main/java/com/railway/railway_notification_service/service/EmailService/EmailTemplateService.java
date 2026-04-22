package com.railway.railway_notification_service.service.EmailService;

import com.railway.railway_notification_service.model.EmailTemplateModel;
import com.railway.railway_notification_service.model.EmailType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Renders Thymeleaf HTML templates into HTML strings.
 *
 * WHY separate from ResendEmailService:
 * If you add AwsSesEmailService tomorrow, it reuses this same
 * renderer without duplicating any template logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailTemplateService {

  private final TemplateEngine templateEngine;

  /**
   * Renders a template with the provided model data.
   * Template files live in resources/templates/email/
   */
  public String render(EmailType emailType, EmailTemplateModel model) {
    Context context = new Context();

    // Each variable here maps to ${variableName} in the HTML template
    context.setVariable("recipientName", model.getRecipientName());
    context.setVariable("actionUrl", model.getActionUrl());
    context.setVariable("appName", model.getAppName());
    context.setVariable("supportEmail", model.getSupportEmail());
    context.setVariable("currentYear", model.getCurrentYear());

    String templatePath = "email/" + emailType.getTemplateName();
    log.debug("Rendering template: {}", templatePath);

    return templateEngine.process(templatePath, context);
  }
}
