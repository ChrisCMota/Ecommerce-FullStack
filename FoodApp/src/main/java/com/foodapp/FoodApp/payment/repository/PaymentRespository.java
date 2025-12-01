package com.foodapp.FoodApp.payment.repository;

import com.foodapp.FoodApp.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRespository extends JpaRepository<Payment, Long> {
}
