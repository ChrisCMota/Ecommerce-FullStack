package com.foodapp.FoodApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
//@RequiredArgsConstructor -- TESTING EMAIL SENDER
public class FoodAppApplication {

//	private final INotificationService notificationService; -- TESTING EMAIL SENDER

	public static void main(String[] args) {
		SpringApplication.run(FoodAppApplication.class, args);
	}


//-- TESTING EMAIL SENDER	
//	@Bean
//	CommandLineRunner runner(){
//		return args -> {
//			NotificationDTO notificationDTO = NotificationDTO.builder()
//					.recipient("christian.kfsn@yahoo.com.br")
//					.subject("Sent from foodapp api - Testing !!!!")
//					.body("This is an API test !!!!!")
//					.type(NotificationType.EMAIL)
//					.build();
//
//			notificationService.sendEmail(notificationDTO);
//		};
//	}

}
