package hse.api.controllers;

import hse.api.proto.grpc.*;
import io.grpc.StatusRuntimeException;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shopping")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:1337")
@Tag(name = "Интернет магазин ВШЭ")
public class ShoppingController {

    @GrpcClient("payments-service")
    private PaymentsServiceGrpc.PaymentsServiceBlockingStub paymentsServiceClient;

    @GrpcClient("orders-service")
    private OrdersServiceGrpc.OrdersServiceBlockingStub ordersServiceClient;

    // ========== Платежные операции ==========
    
    @PostMapping("/payments/account")
    @Operation(summary = "Создать счет пользователя")
    public ResponseEntity<String> createAccount(
            @RequestHeader("user-id") String userId) {
        try {
            paymentsServiceClient.createAccount(
                    CreateAccountRequest.newBuilder()
                            .setUserId(userId)
                            .build());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Счет успешно создан. ID: " + userId);
            
        } catch (StatusRuntimeException e) {
            return handleGrpcException(e, "payments");
        }
    }

    @PostMapping("/payments/deposit")
    @Operation(summary = "Пополнить счет")
    public ResponseEntity<String> deposit(
            @RequestHeader("user-id") String userId,
            @RequestParam double amount) {
        try {
            DepositResponse response = paymentsServiceClient.deposit(
                    DepositRequest.newBuilder()
                            .setUserId(userId)
                            .setAmount(amount)
                            .build());
            
            return ResponseEntity.ok("Баланс успешно пополнен. Новый баланс: " + response.getNewBalance());
            
        } catch (StatusRuntimeException e) {
            return handleGrpcException(e, "payments");
        }
    }

    @GetMapping("/payments/balance")
    @Operation(summary = "Получить баланс счета")
    public ResponseEntity<String> getBalance(
            @RequestHeader("user-id") String userId) {
        try {
            GetBalanceResponse response = paymentsServiceClient.getBalance(
                    GetBalanceRequest.newBuilder()
                            .setUserId(userId)
                            .build());
            
            return ResponseEntity.ok("Текущий баланс: " + response.getBalance());
            
        } catch (StatusRuntimeException e) {
            return handleGrpcException(e, "payments");
        }
    }

    // ========== Операции с заказами ==========
    
    @PostMapping("/orders")
    @Operation(summary = "Создать новый заказ")
    public ResponseEntity<String> createOrder(
            @RequestHeader("user-id") String userId,
            @RequestParam double amount,
            @RequestParam String description) {
        try {
            CreateOrderResponse response = ordersServiceClient.createOrder(
                    CreateOrderRequest.newBuilder()
                            .setUserId(userId)
                            .setAmount(amount)
                            .setDescription(description)
                            .build());
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Заказ успешно создан. ID: " + response.getOrderId() 
                            + "\nСтатус: " + response.getStatus().name());
            
        } catch (StatusRuntimeException e) {
            return handleGrpcException(e, "orders");
        }
    }

    @GetMapping("/orders")
    @Operation(summary = "Получить список заказов")
    public ResponseEntity<String> getOrders(
            @RequestHeader("user-id") String userId) {
        try {
            GetOrdersResponse response = ordersServiceClient.getOrders(
                    GetOrdersRequest.newBuilder()
                            .setUserId(userId)
                            .build());
            
            StringBuilder sb = new StringBuilder("Ваши заказы:\n");
            for (Order order : response.getOrdersList()) {
                sb.append(String.format("- ID: %s, Сумма: %.2f, Статус: %s, Описание: %s%n", 
                        order.getOrderId(), order.getAmount(), order.getStatus(), order.getDescription()));
            }
            
            return ResponseEntity.ok(sb.toString());
            
        } catch (StatusRuntimeException e) {
            return handleGrpcException(e, "orders");
        }
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Получить статус заказа")
    public ResponseEntity<String> getOrderStatus(
            @PathVariable String orderId) {
        try {
            GetOrderStatusResponse response = ordersServiceClient.getOrderStatus(
                    GetOrderStatusRequest.newBuilder()
                            .setOrderId(orderId)
                            .build());
            
            return ResponseEntity.ok("Статус заказа " + orderId + ": " + response.getStatus().name());
            
        } catch (StatusRuntimeException e) {
            return handleGrpcException(e, "orders");
        }
    }

    // ========== Система переподключения ==========
    
    @Scheduled(fixedRate = 5000)
    private void checkConnections() {
        tryReconnect((ManagedChannel) paymentsServiceClient.getChannel());
        tryReconnect((ManagedChannel) ordersServiceClient.getChannel());
    }

    private void tryReconnect(ManagedChannel channel) {
        if (channel.getState(true) != ConnectivityState.READY) {
            channel.resetConnectBackoff();
            channel.enterIdle();
            
            if (channel == paymentsServiceClient.getChannel()) {
                paymentsServiceClient = PaymentsServiceGrpc.newBlockingStub(channel);
            }
            
            if (channel == ordersServiceClient.getChannel()) {
                ordersServiceClient = OrdersServiceGrpc.newBlockingStub(channel);
            }
        }
    }
    
    // ========== Обработка ошибок ==========
    
    private ResponseEntity<String> handleGrpcException(StatusRuntimeException e, String serviceName) {
        if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
            String message = String.format("Сервис %s сейчас спит, попробуйте позже! 💤", serviceName);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(message);
        }
        
        String errorMessage = String.format("Ошибка в сервисе %s: %s", serviceName, e.getStatus().getDescription());
        // e.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorMessage);
    }
}