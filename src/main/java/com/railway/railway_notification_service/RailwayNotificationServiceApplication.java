package com.railway.railway_notification_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication(scanBasePackages = "com.railway")
@EnableKafka
public class RailwayNotificationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RailwayNotificationServiceApplication.class, args);
	}

}
