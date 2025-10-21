package com.foodapp.FoodApp;

import com.foodapp.FoodApp.enums.PaymentStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FoodAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodAppApplication.class, args);

		PaymentStatus status = new PaymentStatus();
	}

}
