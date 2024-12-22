package com.rentalmovie.payment.services.impl;

import com.rentalmovie.payment.dtos.PaymentCommandDTO;
import com.rentalmovie.payment.dtos.PaymentRequestDTO;
import com.rentalmovie.payment.enums.PaymentControl;
import com.rentalmovie.payment.model.PaymentModel;
import com.rentalmovie.payment.publishers.PaymentCommandPublisher;
import com.rentalmovie.payment.publishers.PaymentEventPublisher;
import com.rentalmovie.payment.repositories.PaymentRepository;
import com.rentalmovie.payment.services.PaymentService;
import com.rentalmovie.payment.services.PaymentStripeService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Log4j2
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCommandPublisher paymentCommandPublisher;
    private final PaymentStripeService paymentStripeService;
    private final PaymentEventPublisher paymentEventPublisher;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentCommandPublisher paymentCommandPublisher,
            PaymentStripeService paymentStripeService,
            PaymentEventPublisher paymentEventPublisher
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentCommandPublisher = paymentCommandPublisher;
        this.paymentStripeService = paymentStripeService;
        this.paymentEventPublisher = paymentEventPublisher;
    }

    @Transactional
    @Override
    public void requestPayment(PaymentRequestDTO paymentRequestDTO) {
        PaymentModel paymentModel = savePayment(paymentRequestDTO);
        log.info("Payment request to orderId: {}", paymentModel.getOrderId());

        try {
            var paymentCommandDTO = new PaymentCommandDTO();
            paymentCommandDTO.setOrderId(paymentModel.getOrderId());
            paymentCommandDTO.setPaymentId(paymentModel.getPaymentId());
            paymentCommandDTO.setPaymentMethodId(paymentRequestDTO.getPaymentMethodId());
            paymentCommandPublisher.publishPaymentProcessCommand(paymentCommandDTO);
        } catch (Exception e) {
            log.warn("Error sending payment command!");
        }
    }

    private PaymentModel savePayment(PaymentRequestDTO paymentRequestDTO) {
        PaymentModel paymentModel = PaymentModel.builder()
                .paymentControl(PaymentControl.REQUESTED)
                .paymentRequestDate(LocalDateTime.now(ZoneId.of("UTC")))
                .valuePaid(paymentRequestDTO.getTotalAmount())
                .userId(paymentRequestDTO.getUserId())
                .orderId(paymentRequestDTO.getOrderId())
                .build();

        return paymentRepository.save(paymentModel);
    }

    @Transactional
    @Override
    public void makePayment(PaymentCommandDTO paymentCommandDTO) {

        var paymentModel = paymentRepository.findById(paymentCommandDTO.getPaymentId()).get();

        log.info("Payment process to orderId: {}", paymentModel.getOrderId());

        paymentModel = paymentStripeService.processStripePayment(paymentModel, paymentCommandDTO.getPaymentMethodId());
        paymentRepository.save(paymentModel);

        log.info("Payment status {} to orderId: {}", paymentModel.getPaymentControl(), paymentModel.getOrderId());

        paymentEventPublisher.publishPaymentEvent(paymentModel.convertToPaymentEventDto());
    }
}
