package com.alek.service;

import com.alek.model.Order;
import com.alek.model.PaymentOrder;
import com.alek.model.User;
import com.stripe.exception.StripeException;

import java.util.Set;

public interface PaymentService {

    PaymentOrder createOrder(User user, Set<Order> orders);
    PaymentOrder getPaymentOrderById(Long orderId) throws Exception;
    PaymentOrder getPaymentOrderByPaymentId(String orderId) throws Exception;
//    Boolean ProceedPaymentOrder(PaymentOrder paymentOrder,
//                                String paymentId,
//                                String paymentLinkId);
    String createStripePaymentLink(User user,
                                   Long amount, Long orderId) throws StripeException;
}
