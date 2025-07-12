package hse.orders.GrpcTests;

import hse.orders.domains.Order;
import hse.orders.domains.Progress;
import hse.orders.grpc.OrdersGrpcServer;
import hse.orders.proto.grpc.*;
import hse.orders.services.OrdersService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdersGrpcServerTest {

    @Mock
    private OrdersService ordersService;
    
    @Mock
    private StreamObserver<GetOrdersResponse> ordersResponseObserver;
    
    @Mock
    private StreamObserver<GetOrderStatusResponse> statusResponseObserver;
    
    @Mock
    private StreamObserver<CreateOrderResponse> createOrderResponseObserver;
    
    @InjectMocks
    private OrdersGrpcServer grpcServer;
    
    @Captor
    private ArgumentCaptor<GetOrdersResponse> ordersResponseCaptor;
    
    @Captor
    private ArgumentCaptor<GetOrderStatusResponse> statusResponseCaptor;
    
    @Captor
    private ArgumentCaptor<CreateOrderResponse> createOrderResponseCaptor;
    
    @Captor
    private ArgumentCaptor<Throwable> errorCaptor;

    private final int validUserId = 1;
    private final int validOrderId = 100;
    private final String validUserIdStr = "1";
    private final String validOrderIdStr = "100";

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(validOrderId);
        testOrder.setUserId(validUserId);
        testOrder.setAmount(150.0);
        testOrder.setDescription("Test order");
        testOrder.setStatus(Progress.NEW);
    }

    // ------------------------- getOrders Tests -------------------------
    
    @Test
    void getOrders_ValidRequest_ReturnsOrders() {

        Order testOrder1 = new Order();
        testOrder1.setId(validOrderId + 1);
        testOrder1.setUserId(validUserId);
        testOrder1.setAmount(150.0);
        testOrder1.setDescription("Test order");
        testOrder1.setStatus(Progress.FINISHED);

        Order testOrder2 = new Order();
        testOrder2.setId(validOrderId + 2);
        testOrder2.setUserId(validUserId);
        testOrder2.setAmount(150.0);
        testOrder2.setDescription("Test order");
        testOrder2.setStatus(Progress.CANCELLED);

        when(ordersService.getOrders(validUserId))
            .thenReturn(Arrays.asList(testOrder, testOrder1, testOrder2));
        
        GetOrdersRequest request = GetOrdersRequest.newBuilder()
            .setUserId(validUserIdStr)
            .build();
        
        grpcServer.getOrders(request, ordersResponseObserver);
        
        verify(ordersResponseObserver, times(1)).onNext(ordersResponseCaptor.capture());
        verify(ordersResponseObserver, times(1)).onCompleted();
        
        GetOrdersResponse response = ordersResponseCaptor.getValue();
        assertEquals(3, response.getOrdersCount());
        assertEquals(validOrderIdStr, response.getOrders(0).getOrderId());
    }

    @Test
    void getOrders_InvalidUserId_ThrowsError() {
        GetOrdersRequest request = GetOrdersRequest.newBuilder()
            .setUserId("invalid_id")
            .build();
        
        grpcServer.getOrders(request, ordersResponseObserver);
        
        verify(ordersResponseObserver).onError(errorCaptor.capture());
        assertTrue(errorCaptor.getValue().getMessage().contains("ID должен быть положительным натуральным числом"));
    }

    // ------------------------- getOrderStatus Tests -------------------------
    
    @Test
    void getOrderStatus_ValidRequest_ReturnsStatus() {
        when(ordersService.getOrderStatus(validOrderId))
            .thenReturn(testOrder);
        
        GetOrderStatusRequest request = GetOrderStatusRequest.newBuilder()
            .setOrderId(validOrderIdStr)
            .build();
        
        grpcServer.getOrderStatus(request, statusResponseObserver);
        
        verify(statusResponseObserver).onNext(statusResponseCaptor.capture());
        verify(statusResponseObserver).onCompleted();
        
        GetOrderStatusResponse response = statusResponseCaptor.getValue();
        assertEquals(OrderStatus.NEW, response.getStatus());
    }

    @Test
    void getOrderStatus_OrderNotFound_ThrowsError() {
        when(ordersService.getOrderStatus(validOrderId))
            .thenReturn(null);
        
        GetOrderStatusRequest request = GetOrderStatusRequest.newBuilder()
            .setOrderId(validOrderIdStr)
            .build();
        
        grpcServer.getOrderStatus(request, statusResponseObserver);
        
        verify(statusResponseObserver).onError(errorCaptor.capture());
        assertTrue(errorCaptor.getValue().getMessage().contains("Заказ не найден"));
    }

    // ------------------------- createOrder Tests -------------------------
    
    @Test
    void createOrder_ValidRequest_CreatesSuccessfully() {
        when(ordersService.createOrder(anyInt(), anyDouble(), anyString()))
            .thenReturn(testOrder);
        
        CreateOrderRequest request = CreateOrderRequest.newBuilder()
            .setUserId(validUserIdStr)
            .setAmount(150.0)
            .setDescription("Test order")
            .build();
        
        grpcServer.createOrder(request, createOrderResponseObserver);
        
        verify(createOrderResponseObserver).onNext(createOrderResponseCaptor.capture());
        verify(createOrderResponseObserver).onCompleted();
        
        CreateOrderResponse response = createOrderResponseCaptor.getValue();
        assertEquals(validOrderIdStr, response.getOrderId());
        assertEquals(OrderStatus.NEW, response.getStatus());
    }

    @Test
    void createOrder_InvalidAmount_ThrowsError() {
        CreateOrderRequest request = CreateOrderRequest.newBuilder()
            .setUserId(validUserIdStr)
            .setAmount(-10.0)
            .setDescription("Invalid order")
            .build();
        
        grpcServer.createOrder(request, createOrderResponseObserver);
        
        verify(createOrderResponseObserver).onError(errorCaptor.capture());
        assertTrue(errorCaptor.getValue().getMessage().contains("Сумма заказа должна быть больше 0"));
    }

    @Test
    void createOrder_ServiceThrowsException_PropagatesError() {
        when(ordersService.createOrder(anyInt(), anyDouble(), anyString()))
            .thenThrow(new RuntimeException("DB Error"));
        
        CreateOrderRequest request = CreateOrderRequest.newBuilder()
            .setUserId(validUserIdStr)
            .setAmount(100.0)
            .setDescription("Test")
            .build();
        
        grpcServer.createOrder(request, createOrderResponseObserver);
        
        verify(createOrderResponseObserver).onError(errorCaptor.capture());
        assertTrue(errorCaptor.getValue().getMessage().contains("Ошибка создания заказа"));
    }
    
}