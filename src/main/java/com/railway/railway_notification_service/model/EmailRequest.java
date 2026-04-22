// model/EmailRequest.java
package com.railway.railway_notification_service.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Bundles everything needed to send one email.
 *
 * WHY a model instead of method params:
 * sendEmail(String to, String subject, EmailType type, String name, String url)
 * is hard to read and breaks every time you add a field.
 * EmailRequest is extensible — add cc, bcc, replyTo without changing
 * the EmailService interface.
 */
@Getter
@Builder
public class EmailRequest {
  private final String to;
  private final String subject;
  private final EmailType emailType;
  private final EmailTemplateModel templateModel;
}
