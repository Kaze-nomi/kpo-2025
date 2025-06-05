package hse.api.ControllerTests;

import hse.api.controllers.ShoppingController;
import hse.api.proto.grpc.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShoppingControllerTest {

    @Mock
    private PaymentsServiceGrpc.PaymentsServiceBlockingStub paymentsServiceClient;

    @Mock
    private OrdersServiceGrpc.OrdersServiceBlockingStub ordersServiceClient;

    @InjectMocks
    private ShoppingController controller;

    // ========== Тесты для платежных операций ==========
    
    @Test
    void createAccount_Success() {
        when(paymentsServiceClient.createAccount(any()))
                .thenReturn(CreateAccountResponse.newBuilder().setSuccess(true).build());

        ResponseEntity<String> response = controller.createAccount("123");
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("Счет успешно создан"));
    }

    @Test
    void createAccount_ServiceUnavailable() {
        when(paymentsServiceClient.createAccount(any()))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        ResponseEntity<String> response = controller.createAccount("123");
        
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().contains("Сервис payments сейчас спит"));
    }

    @Test
    void accountExists_Exists() {
        when(paymentsServiceClient.getBalance(any()))
                .thenReturn(GetBalanceResponse.newBuilder().setBalance(100).build());

        ResponseEntity<String> response = controller.accountExists("123");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Exists", response.getBody());
    }

    @Test
    void accountExists_NotExists() {
        when(paymentsServiceClient.getBalance(any()))
                .thenThrow(new StatusRuntimeException(Status.INTERNAL
                        .withDescription("Аккаунт не найден")));

        ResponseEntity<String> response = controller.accountExists("123");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Not exists", response.getBody());
    }

    @Test
    void deposit_Success() {
        when(paymentsServiceClient.deposit(any()))
                .thenReturn(DepositResponse.newBuilder().setNewBalance(150).build());

        ResponseEntity<String> response = controller.deposit("123", 50);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Новый баланс: 150"));
    }

    @Test
    void deposit_InvalidAmount() {
        when(paymentsServiceClient.deposit(any()))
                .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

        ResponseEntity<String> response = controller.deposit("123", -10);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Ошибка"));
    }

    // ========== Тесты для операций с заказами ==========
    
    @Test
    void createOrder_Success() {
        when(ordersServiceClient.createOrder(any()))
                .thenReturn(CreateOrderResponse.newBuilder()
                        .setOrderId("999")
                        .setStatus(OrderStatus.NEW)
                        .build());

        ResponseEntity<String> response = controller.createOrder("123", 100, "Тестовый заказ");
        
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().contains("ID: 999"));
        assertTrue(response.getBody().contains("Статус: NEW"));
    }

    @Test
    void createOrder_InvalidAmount() {
        when(ordersServiceClient.createOrder(any()))
                .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT
                        .withDescription("Сумма должна быть положительной")));

        ResponseEntity<String> response = controller.createOrder("123", 0, "Невалидный заказ");
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Сумма должна быть положительной"));
    }

    @Test
    void getOrders_Success() {
        Order testOrder = Order.newBuilder()
                .setOrderId("777")
                .setAmount(200)
                .setStatus(OrderStatus.FINISHED)
                .setDescription("Завершенный заказ")
                .build();

        when(ordersServiceClient.getOrders(any()))
                .thenReturn(GetOrdersResponse.newBuilder().addOrders(testOrder).build());

        ResponseEntity<String> response = controller.getOrders("123");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("ID: 777"));
        assertTrue(response.getBody().contains("Статус: FINISHED"));
    }

    @Test
    void getOrderStatus_Success() {
        when(ordersServiceClient.getOrderStatus(any()))
                .thenReturn(GetOrderStatusResponse.newBuilder()
                        .setStatus(OrderStatus.CANCELED)
                        .build());

        ResponseEntity<String> response = controller.getOrderStatus("888");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("Статус заказа 888: CANCELED"));
    }

    @Test
    void getOrderStatus_NotFound() {
        when(ordersServiceClient.getOrderStatus(any()))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        ResponseEntity<String> response = controller.getOrderStatus("000");
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Ошибка"));
    }

    // ========== Тесты обработки ошибок ==========
    
    @Test
    void handleGrpcException_Unavailable() {
        StatusRuntimeException exception = new StatusRuntimeException(Status.UNAVAILABLE);
        ResponseEntity<String> response = controller.handleGrpcException(exception, "payments");
        
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().contains("Сервис payments сейчас спит"));
    }

    @Test
    void handleGrpcException_InternalError() {
        StatusRuntimeException exception = new StatusRuntimeException(Status.INTERNAL
                .withDescription("База данных недоступна"));
        
        ResponseEntity<String> response = controller.handleGrpcException(exception, "orders");
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("База данных недоступна"));
    }
    
}