package hse.payments.grpc;

import hse.payments.proto.grpc.*;
import hse.payments.services.PaymentsService;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class PaymentsGrpcServer extends PaymentsServiceGrpc.PaymentsServiceImplBase {

    private final PaymentsService paymentsService;

    @Override
    public void createAccount(CreateAccountRequest request, 
                             StreamObserver<CreateAccountResponse> responseObserver) {
        try {
            int userId = parseUserId(request.getUserId());
            paymentsService.createAccount(userId);
            
            CreateAccountResponse response = CreateAccountResponse.newBuilder()
                    .setSuccess(true)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver,
                    "Ошибка создания счета: " + e.getMessage());
        }
    }

    @Override
    public void deposit(DepositRequest request, 
                        StreamObserver<DepositResponse> responseObserver) {
        try {
            int userId = parseUserId(request.getUserId());
            double amount = request.getAmount();
            
            paymentsService.deposit(userId, amount);
            double newBalance = paymentsService.getBalance(userId);
            
            DepositResponse response = DepositResponse.newBuilder()
                    .setSuccess(true)
                    .setNewBalance(newBalance)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            handleError(responseObserver,
                    "Ошибка пополнения счета: " + e.getMessage());
        }
    }

    @Override
    public void getBalance(GetBalanceRequest request, 
                           StreamObserver<GetBalanceResponse> responseObserver) {
        try {
            int userId = parseUserId(request.getUserId());
            double balance = paymentsService.getBalance(userId);
            
            GetBalanceResponse response = GetBalanceResponse.newBuilder()
                    .setBalance(balance)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            handleError(responseObserver,
                    "Ошибка получения баланса: " + e.getMessage());
        }
    }

    private int parseUserId(String userIdStr) {
        try {
            Integer id = Integer.parseInt(userIdStr);
            if (id == null || id < 1) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException e) {
            throw new NumberFormatException("ID должен быть положительным натуральным числом");
        }
    }

    private void handleError(StreamObserver<?> responseObserver, String description) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(description)
                    .asRuntimeException());    
    }

}