package com.example.dataox.integration;

import com.example.dataox.dto.OrderCreateRequest;
import com.example.dataox.entities.Client;
import com.example.dataox.entities.Order;
import com.example.dataox.repo.ClientRepository;
import com.example.dataox.repo.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ConcurrentDecreasingPriceOrdersIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Client supplier;
    private Client consumer;

    @BeforeEach
    void setUp() {
        supplier = Client.builder()
                .name("Supplier")
                .email("supplier@example.com")
                .profit(BigDecimal.ZERO)
                .active(true)
                .build();
        supplier = clientRepository.save(supplier);

        consumer = Client.builder()
                .name("Consumer")
                .email("consumer@example.com")
                .profit(BigDecimal.ZERO)
                .active(true)
                .build();
        consumer = clientRepository.save(consumer);

        OrderCreateRequest initialRequest = new OrderCreateRequest();
        initialRequest.setName("Initial Order");
        initialRequest.setSupplierId(supplier.getId());
        initialRequest.setConsumerId(consumer.getId());
        initialRequest.setPrice(new BigDecimal("970.00"));

        restTemplate.postForEntity("/api/orders", initialRequest, Order.class);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    void testMultipleDecreasingPriceOrders_ShouldCreateOnlyOne() throws InterruptedException {
        int numberOfRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfRequests);
        CountDownLatch latch = new CountDownLatch(numberOfRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        for (int i = 0; i < numberOfRequests; i++) {
            int price = 100 - i * 10;
            OrderCreateRequest request = new OrderCreateRequest();
            request.setName("decreasing-price-order");
            request.setSupplierId(supplier.getId());
            request.setConsumerId(consumer.getId());
            request.setPrice(new BigDecimal(price));

            executor.submit(() -> {
                try {
                    ResponseEntity<Order> response = restTemplate.postForEntity(
                            "/api/orders", request, Order.class);

                    if (response.getStatusCode().is2xxSuccessful()) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        assertEquals(1, successCount.get(), "Only one order should be created");
        assertEquals(9, errorCount.get(), "Nine requests should fail");

        List<Order> orders = orderRepository.findAll();
        assertEquals(2, orders.size(), "Should have initial order and one new order");

        Order newOrder = orders.stream()
                .filter(order -> !"Initial Order".equals(order.getName()))
                .findFirst()
                .orElseThrow();

        BigDecimal consumerProfit = calculateConsumerProfit(consumer.getId());
        assertTrue(consumerProfit.compareTo(new BigDecimal("-1000")) >= 0,
                "Consumer profit should be >= -1000, but was: " + consumerProfit);
    }

    private BigDecimal calculateConsumerProfit(Long consumerId) {
        Client updatedConsumer = clientRepository.findById(consumerId)
                .orElseThrow(() -> new IllegalStateException("Consumer not found in DB"));
        return updatedConsumer.getProfit() != null ? updatedConsumer.getProfit() : BigDecimal.ZERO;
    }
}