package com.beetclick.paymentservice.controller;

import com.beetclick.paymentservice.dto.PaymentCreditRequest;
import com.beetclick.paymentservice.dto.PaymentResponse;
import com.beetclick.paymentservice.entity.Payment;
import com.beetclick.paymentservice.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @GetMapping("/all")
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public Payment getPaymentById(@PathVariable UUID id) {
        return paymentService.getPaymentById(id);
    }

    @PostMapping("/credit")
    public Payment credit(@RequestBody PaymentCreditRequest request, UUID userId) {

        return paymentService.creditWallet(request, userId);
    }

    @PostMapping("/withdrawal")
    public Payment withdraw(@RequestBody PaymentCreditRequest request, UUID userId) {

        return paymentService.withdrawWallet(request, userId);
    }
}
