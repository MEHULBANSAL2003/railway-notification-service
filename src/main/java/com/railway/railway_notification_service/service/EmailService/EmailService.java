package com.railway.railway_notification_service.service.EmailService;

import com.railway.railway_notification_service.model.EmailRequest;

public interface EmailService {

  void sendEmail(EmailRequest request);
}
