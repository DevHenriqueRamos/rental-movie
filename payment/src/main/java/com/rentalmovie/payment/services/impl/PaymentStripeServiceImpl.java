package com.rentalmovie.payment.services.impl;

import com.rentalmovie.payment.enums.PaymentControl;
import com.rentalmovie.payment.model.PaymentModel;
import com.rentalmovie.payment.services.PaymentStripeService;
import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Service
public class PaymentStripeServiceImpl implements PaymentStripeService {

    @Value(value = "${rm.stripe.secretKey}")
    private String secretKeyStripe;

    @Override
    public PaymentModel processStripePayment(PaymentModel paymentModel, String paymentMethodId) {

        Stripe.apiKey = secretKeyStripe;
        String paymentIntentId = null;

        try {
            Map<String, Object> paramsPaymentIntent = new HashMap<>();
            paramsPaymentIntent.put("amount", paymentModel.getValuePaid().multiply(new BigDecimal("100")).longValue());
            paramsPaymentIntent.put("currency", "brl");
            paramsPaymentIntent.put("payment_method", paymentMethodId);
            paramsPaymentIntent.put("confirm", true);
            paramsPaymentIntent.put("automatic_payment_methods", Map.of(
                    "enabled", true,
                    "allow_redirects", "never"
            ));

            PaymentIntent paymentIntent = PaymentIntent.create(paramsPaymentIntent);
            paymentIntentId = paymentIntent.getId();

            if (paymentIntent.getStatus().equals("succeeded")) {
                paymentModel.setPaymentControl(PaymentControl.EFFECTED);
                paymentModel.setPaymentMessage("payment effected - paymentIntent: " + paymentIntentId);
                paymentModel.setPaymentCompletionDate(LocalDateTime.now(ZoneId.of("UTC")));
            } else {
                paymentModel.setPaymentControl(PaymentControl.ERROR);
                paymentModel.setPaymentMessage("payment error v1 - paymentIntent: " + paymentIntentId);
            }

        } catch (CardException e) {
            log.error("A payment error occurred: {}", e.getMessage());

            try {
                paymentModel.setPaymentControl(PaymentControl.REFUSED);
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                paymentModel.setPaymentMessage("payment refused v1 - paymentIntent: " + paymentIntentId +
                        ", cause: " + paymentIntent.getLastPaymentError().getCode() +
                        ", message: " + paymentIntent.getLastPaymentError().getMessage());
            } catch (StripeException ex) {
                paymentModel.setPaymentMessage("payment refused v2 - paymentIntent: " + paymentIntentId);
                log.error("A payment error occurred: {}", ex.getMessage());
            }
        } catch (StripeException e) {
            paymentModel.setPaymentControl(PaymentControl.ERROR);
            paymentModel.setPaymentMessage("payment error v2 - paymentIntent: " + paymentIntentId);
            log.error("Stripe exception: {}", e.getMessage());
        }
        return paymentModel;
    }
}
