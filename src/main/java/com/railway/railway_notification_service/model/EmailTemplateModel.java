// model/EmailTemplateModel.java
package com.railway.railway_notification_service.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Dynamic data injected into Thymeleaf templates.
 *
 * WHY @Builder.Default for appName, supportEmail, currentYear:
 * These rarely change. Default values mean callers only set
 * what's unique per email (name, actionUrl).
 * If you rebrand, change defaults here — all templates update.
 */
@Getter
@Builder
public class EmailTemplateModel {

  // "Hi {recipientName},"
  private final String recipientName;

  // The button URL — verify page, reset page, etc.
  private final String actionUrl;

  @Builder.Default
  private final String appName = "RailTick";

  @Builder.Default
  private final String supportEmail = "support@railtick.in";

  @Builder.Default
  private final String currentYear = String.valueOf(java.time.Year.now().getValue());
}
