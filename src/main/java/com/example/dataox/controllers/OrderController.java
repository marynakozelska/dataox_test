package com.example.dataox.controllers;

import com.example.dataox.dto.OrderCreateRequest;
import com.example.dataox.entities.Order;
import com.example.dataox.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operations for creating and listing orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get all orders")
    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Operation(summary = "Get order by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @Operation(summary = "Create new order (processing delay 1-10s). Business rules: unique business key, positive price, active clients, profit thresholds)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Business key conflict", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violation (profit threshold or inactive client)", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public Order createOrder(@Valid @RequestBody OrderCreateRequest req) {
        return orderService.createOrder(req);
    }

    @Operation(summary = "Get all orders where client is supplier or consumer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of orders for the client"),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/clients/{clientId}/orders")
    public List<Order> getClientOrders(@PathVariable Long clientId) {
        return orderService.getClientOrders(clientId);
    }
}
