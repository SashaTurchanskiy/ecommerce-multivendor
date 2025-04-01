package com.alek.service.impl;

import com.alek.domain.PaymentOrderStatus;
import com.alek.exception.PaymentOrderNotFoundException;
import com.alek.model.Order;
import com.alek.model.PaymentOrder;
import com.alek.model.PaymentStatus;
import com.alek.model.User;
import com.alek.repository.OrderRepo;
import com.alek.repository.PaymentOrderRepo;
import com.alek.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentOrderRepo paymentOrderRepo;
    private final OrderRepo orderRepo;

    @Value("${stripe.api.key}")
    private String apiKey;

    @Value("${stripe.secret.key}")
    private String stripesecretKey;

    public PaymentServiceImpl(PaymentOrderRepo paymentOrderRepo, OrderRepo orderRepo) {
        this.paymentOrderRepo = paymentOrderRepo;
        this.orderRepo = orderRepo;
    }


    @Override
    public PaymentOrder createOrder(User user, Set<Order> orders) {
        Long amount = orders.stream().mapToLong(Order::getTotalSellingPrice).sum();

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setOrders(orders);
        paymentOrder.setAmount(amount);

        return paymentOrderRepo.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long orderId) throws Exception {
        return paymentOrderRepo.findById(orderId).orElseThrow(()->
                new PaymentOrderNotFoundException("Payment order not found"));
    }

    @Override
    public PaymentOrder getPaymentOrderByPaymentId(String orderId) throws Exception {
        PaymentOrder paymentOrder = paymentOrderRepo.findByPaymentLinkId(orderId);

        if (paymentOrder == null){
            throw new PaymentOrderNotFoundException("Payment order not found with provided payment link");
        }
        return paymentOrder;
    }

    @Override
    public Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId, String paymentLinkId) throws StripeException {
        if (paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)) {
            Stripe.apiKey = stripesecretKey;

            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentId);

            if ("succeeded".equals(paymentIntent.getStatus())) {
                Set<Order> orders = paymentOrder.getOrders();
                for (Order order : orders) {
                    order.setPaymentStatus(PaymentStatus.COMPLETED);
                    orderRepo.save(order);
                }
                paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                paymentOrderRepo.save(paymentOrder);
                return true;
            } else {
                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepo.save(paymentOrder);
                return false;
            }
        }
        return false;
    }

    @Override
    public String createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException {
        Stripe.apiKey= stripesecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/payment/success" + orderId)
                .setCancelUrl("http://localhost:3000/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount * 100)
                                .setProductData(SessionCreateParams
                                        .LineItem.PriceData.ProductData
                                        .builder().setName("Payment for order")
                                        .build()
                                ).build()
                         ).build()
                ).build();
        Session session = Session.create(params);

        return session.getUrl();
    }
}
