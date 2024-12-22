package com.rentalmovie.payment.repositories;

import com.rentalmovie.payment.model.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentModel, UUID> {
}
