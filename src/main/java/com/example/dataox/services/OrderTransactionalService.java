package com.example.dataox.services;

import com.example.dataox.dto.OrderCreateRequest;
import com.example.dataox.entities.Client;
import com.example.dataox.entities.Order;
import com.example.dataox.repo.ClientRepository;
import com.example.dataox.repo.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderTransactionalService {
    private final ClientRepository clientRepository;
    private final OrderRepository orderRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Order saveOrderTransactional(LocalDateTime startTime, OrderCreateRequest orderRequest, Long supplierId, Long consumerId) {
        Client supplier = clientRepository.findByIdForUpdate(supplierId)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + supplierId));
        Client consumer = clientRepository.findByIdForUpdate(consumerId)
                .orElseThrow(() -> new EntityNotFoundException("Consumer not found with id: " + consumerId));

        if (!supplier.isActive()) {
            throw new IllegalStateException("Supplier is inactive");
        }
        if (!consumer.isActive()) {
            throw new IllegalStateException("Consumer is inactive");
        }

        BigDecimal price = orderRequest.getPrice();
        BigDecimal threshold = BigDecimal.valueOf(-1000L);

        BigDecimal supplierCurrent = supplier.getProfit() == null ? BigDecimal.ZERO : supplier.getProfit();
        BigDecimal supplierAfter = supplierCurrent.add(price);
        if (supplierAfter.compareTo(threshold) < 0) {
            throw new IllegalStateException("Creating this order would make supplier's total profit less than -1000");
        }

        BigDecimal consumerCurrent = consumer.getProfit() == null ? BigDecimal.ZERO : consumer.getProfit();
        BigDecimal consumerAfter = consumerCurrent.subtract(price);
        if (consumerAfter.compareTo(threshold) < 0) {
            throw new IllegalStateException("Creating this order would make consumer's total profit less than -1000");
        }

        Order order = Order.builder()
                .name(orderRequest.getName())
                .supplier(supplier)
                .consumer(consumer)
                .price(orderRequest.getPrice())
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .build();

        try {
            Order saved = orderRepository.save(order);

            supplier.setProfit(supplierAfter);
            consumer.setProfit(consumerAfter);

            clientRepository.save(supplier);
            clientRepository.save(consumer);

            return saved;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException("Order cannot be created: constraint violation (possible duplicate business key)", ex);
        }
    }
}
