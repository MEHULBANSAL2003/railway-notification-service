package com.railway.railway_notification_service.consumer;


import com.railway.common.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountDeletionEventConsumer {


  @KafkaListener(
    topics = KafkaTopics.Auth.ACCOUNT_DELETION_REQUEST,
    groupId = "notification-service-group"
  )
  public void handleAccountDeletionRequest(){

  }

  @KafkaListener(
    topics = KafkaTopics.Auth.ACCOUNT_DELETION,
    groupId = "notification-service-group"
  )
  public void handleAccountDeletion(){

  }
}
