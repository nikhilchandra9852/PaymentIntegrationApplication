package com.paymmentIntegration.payment.controller;


import com.paymmentIntegration.payment.service.PaypalService;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private  final PaypalService paypalService;

    @GetMapping("/")
    public String home(){
        return "index";
    }
    @PostMapping("/payment/create")
    public RedirectView createPayment(
            @RequestParam("method") String method,
            @RequestParam("amount") String amount,
            @RequestParam("currency") String currency,
            @RequestParam("description") String description

    ){
        try {

            String cancelUrl ="http://localhost:8080/payment/cancel";
            String successUrl= "http://localhost:8080/payment/success";

            Payment payment = paypalService.createPayment(
                    Double.valueOf(amount),currency,method,"sale"
                    ,description,cancelUrl,successUrl
            );

            for(Links links : payment.getLinks()){
                if(links.getRel().equals("approval_url")){
                    return new RedirectView(links.getHref());
                }
            }
        }catch (PayPalRESTException ex){
            log.error("Error occured:: ", ex);
        }
        return new RedirectView("/payment/error") ;
    }


    // succesing the payment
    @GetMapping("/payement/success")
    public String paymentSuccess(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId){
        try{
            Payment payment =paypalService.exceutePayment(paymentId,payerId);
            if(payment.getState().equals("approved")){
                return "paymentSuccess";
            }
        }catch (PayPalRESTException ex){
            log.error("Error Occurred :",ex);
        } return "paymentSuccess";
    }
    
    //cancelling the payment
    @GetMapping("/payment/cancel")
    public String paymentCancelling(){
        return "paymentCancel";
    }

    //payment error
    @GetMapping("/payment/error")
    public String paymentError(){
        return "paymentError";
    }

}
