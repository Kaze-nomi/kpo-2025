package hse.orders.grpc;

import hse.orders.domains.Order;
import hse.orders.domains.Progress;
import hse.orders.proto.grpc.*;
import hse.orders.services.OrdersService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class OrdersGrpcServer extends OrdersServiceGrpc.OrdersServiceImplBase {

    private final OrdersService ordersService;

    @Override
    public void getOrders(GetOrdersRequest request, 
                         StreamObserver<GetOrdersResponse> responseObserver) {
        try {
            int userId = parseId(request.getUserId());
            List<Order> orders = ordersService.getOrders(userId);
            
            GetOrdersResponse response = GetOrdersResponse.newBuilder()
                    .addAllOrders(convertToProtoOrders(orders))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver,
                    "Ошибка получения заказов: " + e.getMessage());
        }
    }

    @Override
    public void getOrderStatus(GetOrderStatusRequest request, 
                               StreamObserver<GetOrderStatusResponse> responseObserver) {
        try {
            int orderId = parseId(request.getOrderId());
            Order order = ordersService.getOrderStatus(orderId);
            
            if (order == null) {
                handleError(responseObserver,
                        "Заказ не найден: " + request.getOrderId());
                return;
            }
            
            GetOrderStatusResponse response = GetOrderStatusResponse.newBuilder()
                    .setStatus(convertToProtoStatus(order.getStatus()))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver,
                    "Ошибка получения статуса заказа: " + e.getMessage());
        }
    }

    @Override
    public void createOrder(CreateOrderRequest request, 
                           StreamObserver<CreateOrderResponse> responseObserver) {
        try {
            int userId = parseId(request.getUserId());
            double amount = request.getAmount();
            String description = request.getDescription();

            if (amount <= 0) {
                handleError(responseObserver,
                        "Сумма заказа должна быть больше 0");
                return;
            }
            
            Order order = ordersService.createOrder(userId, amount, description);
            
            CreateOrderResponse response = CreateOrderResponse.newBuilder()
                    .setOrderId(String.valueOf(order.getId()))
                    .setStatus(convertToProtoStatus(order.getStatus()))
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver,
                    "Ошибка создания заказа: " + e.getMessage());
        }
    }

    private int parseId(String IdStr) {
        try {
            Integer id = Integer.parseInt(IdStr);
            if (id == null || id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("ID должен быть положительным натуральным числом");
        }
    }

    private List<hse.orders.proto.grpc.Order> convertToProtoOrders(List<Order> orders) {
        return orders.stream()
                .map(this::convertToProtoOrder)
                .collect(Collectors.toList());
    }

    private hse.orders.proto.grpc.Order convertToProtoOrder(Order order) {
        return hse.orders.proto.grpc.Order.newBuilder()
                .setOrderId(String.valueOf(order.getId()))
                .setUserId(String.valueOf(order.getUserId()))
                .setAmount(order.getAmount())
                .setStatus(convertToProtoStatus(order.getStatus()))
                .setDescription(order.getDescription())
                .build();
    }

    private OrderStatus convertToProtoStatus(Progress status) {
        switch (status) {
            case NEW:
                return OrderStatus.NEW;
            case FINISHED:
                return OrderStatus.FINISHED;
            case CANCELLED:
                return OrderStatus.CANCELED;
            default:
                throw new IllegalArgumentException("Неизвестный статус: " + status);
        }
    }

    private void handleError(StreamObserver<?> responseObserver, String description) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(description)
                    .asRuntimeException());    
    }
    
}