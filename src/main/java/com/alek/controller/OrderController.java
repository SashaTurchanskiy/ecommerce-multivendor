package com.alek.controller;

import com.alek.domain.PaymentMethod;
import com.alek.model.*;
import com.alek.repository.PaymentOrderRepo;
import com.alek.response.PaymentLinkResponse;
import com.alek.service.*;
import com.stripe.model.PaymentLink;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final CartService cartService;
    private final SellerService sellerService;
    private final SellerReportService sellerReportService;
    private final PaymentService paymentService;
    private final PaymentOrderRepo paymentOrderRepo;


    public OrderController(OrderService orderService, UserService userService, CartService cartService, SellerService sellerService, SellerReportService sellerReportService, PaymentService paymentService, PaymentOrderRepo paymentOrderRepo) {
        this.orderService = orderService;
        this.userService = userService;
        this.cartService = cartService;
        this.sellerService = sellerService;
        this.sellerReportService = sellerReportService;
        this.paymentService = paymentService;
        this.paymentOrderRepo = paymentOrderRepo;
    }

    @PostMapping
    public ResponseEntity<PaymentLinkResponse> createOrderHandler(
            @RequestBody Address shippingAddress,
            @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.findUserCart(user);
        Set<Order> orders = orderService.createOrder(user, shippingAddress, cart);

        PaymentOrder paymentOrder = paymentService.createOrder(user, orders);

        PaymentLinkResponse res = new PaymentLinkResponse();

        String paymentUrl = paymentService.createStripePaymentLink(user,
                    paymentOrder.getAmount(),
                    paymentOrder.getId());

            res.setPayment_link_url(paymentUrl);

            paymentOrder.setPaymentLinkId(paymentUrl);
            paymentOrderRepo.save(paymentOrder);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Order>> userOrderHistoryHandler(
            @RequestHeader ("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        List<Order> orders = orderService.userOrderHistory(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long orderId,
            @RequestHeader ("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Order orders = orderService.findOrderById(orderId);
        return new ResponseEntity<>(orders, HttpStatus.ACCEPTED);
    }

    @GetMapping("/items/{orderItemId}")
    public ResponseEntity<OrderItem> getOrderItemById(
            @PathVariable Long orderItemId,
            @RequestHeader ("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        OrderItem orderItem = orderService.getOrderById(orderItemId);
        return new ResponseEntity<>(orderItem, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader ("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.cancelOrder(orderId, user);

        Seller seller = sellerService.getSellerById(order.getSellerId());
        SellerReport report = sellerReportService.getSellerReport(seller);

        report.setCanceledOrders(report.getCanceledOrders() + 1);
        report.setTotalRefunds(report.getTotalRefunds() + order.getTotalSellingPrice());
        sellerReportService.updateSellerReport(report);

        return ResponseEntity.ok(order);
    }
    



}
