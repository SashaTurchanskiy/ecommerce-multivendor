package com.alek.service.impl;

import com.alek.domain.OrderStatus;
import com.alek.exception.OrderException;
import com.alek.exception.OrderNotFoundException;
import com.alek.model.*;
import com.alek.repository.AddressRepo;
import com.alek.repository.OrderItemRepo;
import com.alek.repository.OrderRepo;
import com.alek.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepo orderRepo;
    private final AddressRepo addressRepo;
    private final OrderItemRepo orderItemRepo;

    public OrderServiceImpl(OrderRepo orderRepo, AddressRepo addressRepo, OrderItemRepo orderItemRepo) {
        this.orderRepo = orderRepo;
        this.addressRepo = addressRepo;
        this.orderItemRepo = orderItemRepo;
    }

    @Override
    public Set<Order> createOrder(User user, Address shippingAddress, Cart cart) {

        if (!user.getAddresses().contains(shippingAddress)){
            user.getAddresses().add(shippingAddress);
        }
        Address address = addressRepo.save(shippingAddress);

        Map<Long, List<CartItem>> itemsBySeller = cart.getCartItems().stream()
                .collect(Collectors.groupingBy(item -> item.getProduct()
                        .getSeller().getId()));

        Set<Order> orders = new HashSet<>();

        for (Map.Entry<Long, List<CartItem>> entry : itemsBySeller.entrySet()) {
           Long sellerId = entry.getKey();

              List<CartItem> items = entry.getValue();

              int totalOrderPrice = items.stream().mapToInt(CartItem::getSellingPrice).sum();
              int totalItem = items.stream().mapToInt(CartItem::getQuantity).sum();

              Order createdOrder = new Order();
              createdOrder.setUser(user);
              createdOrder.setSellerId(sellerId);
              createdOrder.setTotalMrpPrice(totalOrderPrice);
              createdOrder.setTotalSellingPrice(totalOrderPrice);
              createdOrder.setTotalItem(totalItem);
              createdOrder.setShippingAddress(address);
              createdOrder.setOrderStatus(OrderStatus.PENDING);
              createdOrder.getPaymentsDetails().setStatus(PaymentStatus.PENDING);

              Order savedOrder = orderRepo.save(createdOrder);
              orders.add(savedOrder);

              List<OrderItem> orderItems = new ArrayList<>();

              for (CartItem item : items){
                  OrderItem orderItem = new OrderItem();
                  orderItem.setOrder(savedOrder);
                  orderItem.setMrpPrice(item.getMrpPrice());
                  orderItem.setProduct(item.getProduct());
                  orderItem.setQuantity(item.getQuantity());
                  orderItem.setSize(item.getSize());
                  orderItem.setUserId(item.getUserId());
                  orderItem.setSellingPrice(item.getSellingPrice());

                  savedOrder.getOrderItems().add(orderItem);
                  OrderItem savedOrderItem = orderItemRepo.save(orderItem);
                  orderItems.add(savedOrderItem);
              }
        }
        return orders;
    }

    @Override
    public Order findOrderById(Long id) throws Exception {
        return orderRepo.findById(id).orElseThrow(()->
                new OrderNotFoundException("Order not found with id " + id));
    }

    @Override
    public List<Order> userOrderHistory(Long userId) {
        return orderRepo.findByUserId(userId);
    }

    @Override
    public List<Order> sellersOrder(Long sellerId) {
        return orderRepo.findBySellerId(sellerId);
    }

    @Override
    public Order updateOrderStatus(Long orderId, OrderStatus orderStatus) throws Exception {
        Order order = findOrderById(orderId);
        order.setOrderStatus(orderStatus);
        return orderRepo.save(order);
    }

    @Override
    public Order cancelOrder(Long orderId, User user) throws Exception {
        Order order = findOrderById(orderId);
        if (!user.getId().equals(order.getUser().getId())){
            throw new OrderException("You dont have permission to cancel this order");
        }
        order.setOrderStatus(OrderStatus.CANCELLED);
        return orderRepo.save(order);
    }

    @Override
    public OrderItem getOrderById(Long id) throws Exception {
        return orderItemRepo.findById(id).orElseThrow(() ->
                new OrderException("Order item not exit ... "));
    }
}
