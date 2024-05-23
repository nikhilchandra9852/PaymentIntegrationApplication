package com.paymmentIntegration.payment.service;


import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaypalService {

    private  final APIContext apiContext;


    public Payment createPayment(Double total,
                                 String Currency,
                                 String method,
                                 String intent,
                                 String description,
                                 String cancelUrl,
                                 String successUrl) throws PayPalRESTException {

        // create Anmount
        Amount amount = new Amount();
        amount.setCurrency(Currency);
        amount.setTotal(String.format(Locale.forLanguageTag(Currency),"%.2f",total)); // 9.99$ -- 9,99 Euros
        // create Transaction
        Transaction transaction = new Transaction();
        transaction.setDescription(description);
        transaction.setAmount(amount);

        // mutliple Transactions
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);

        // payment method
        Payer payer = new Payer();
        payer.setPaymentMethod(method);

        // payment
        Payment payment = new Payment();
        payment.setIntent(intent);
        payment.setPayer(payer);
        payment.setTransactions(transactionList);


        // redirect urls
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    // exceute the payment.
    public Payment exceutePayment(String paymentId,
                                  String payerId) throws PayPalRESTException {

        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution  paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext,paymentExecution);
    }
}
