package com.rentalmovie.payment.services;

import com.rentalmovie.payment.dtos.PaymentCommandDTO;
import com.rentalmovie.payment.dtos.PaymentRequestDTO;

public interface PaymentService {
    void requestPayment(PaymentRequestDTO paymentRequestDTO);
    void makePayment(PaymentCommandDTO paymentCommandDTO);
}
