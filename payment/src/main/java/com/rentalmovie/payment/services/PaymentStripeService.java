package com.rentalmovie.payment.services;

import com.rentalmovie.payment.model.PaymentModel;

public interface PaymentStripeService {

    PaymentModel processStripePayment(PaymentModel paymentModel, String paymentMethodId);
}
