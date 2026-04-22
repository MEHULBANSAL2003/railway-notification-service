package com.railway.railway_notification_service.service.EmailService;

public interface EmailService {

  void sendEmail(String to, String message, String subject);
}
