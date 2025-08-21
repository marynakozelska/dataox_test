package com.example.dataox.services;

import com.example.dataox.dto.OrderCreateRequest;
import com.example.dataox.entities.Order;
import com.example.dataox.repo.ClientRepository;
import com.example.dataox.repo.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final OrderTransactionalService orderTransactionalService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public Order createOrder(OrderCreateRequest orderRequest) {
        validateRequest(orderRequest);
        LocalDateTime startTime = LocalDateTime.now();

        int delaySeconds = ThreadLocalRandom.current().nextInt(1, 11);

        try {
            TimeUnit.SECONDS.sleep(delaySeconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Order processing interrupted", e);
        }

        return orderTransactionalService.saveOrderTransactional(startTime, orderRequest, orderRequest.getSupplierId(), orderRequest.getConsumerId());
    }

    private void validateRequest(OrderCreateRequest req) {
        Objects.requireNonNull(req, "Order request must not be null");
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Order name must be provided");
        }
        if (req.getSupplierId() == null) {
            throw new IllegalArgumentException("SupplierId must be provided");
        }
        if (req.getConsumerId() == null) {
            throw new IllegalArgumentException("ConsumerId must be provided");
        }
        if (req.getPrice() == null || req.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
    }


    public List<Order> getClientOrders(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new EntityNotFoundException("Client not found with id: " + clientId);
        }
        return orderRepository.findByClientId(clientId);
    }
}
