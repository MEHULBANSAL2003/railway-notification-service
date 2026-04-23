// model/EmailType.java
package com.railway.railway_notification_service.model;

/**
 * Maps each email type to its Thymeleaf template filename.
 *
 * HOW to add a new email type:
 * 1. Add constant here with template filename
 * 2. Create the HTML file in resources/templates/email/
 * Nothing else changes anywhere.
 */
public enum EmailType {

  EMAIL_VERIFICATION_REMINDER("email-verification-reminder"),
  ACCOUNT_DELETION_REQUEST("account-deletion-request"),
  ACCOUNT_DELETE_PERMANENT("account-deletion-permanent");

  private final String templateName;

  EmailType(String templateName) {
    this.templateName = templateName;
  }

  public String getTemplateName() {
    return templateName;
  }
}
