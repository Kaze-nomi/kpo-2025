package hse.payments.GrpcServerTests;

import hse.payments.services.PaymentsService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import hse.payments.grpc.PaymentsGrpcServer;
import hse.payments.proto.grpc.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentsGrpcServerTest {

    @Mock
    private PaymentsService paymentsService;

    @Mock
    private StreamObserver<CreateAccountResponse> createAccountObserver;

    @Mock
    private StreamObserver<DepositResponse> depositObserver;

    @Mock
    private StreamObserver<GetBalanceResponse> getBalanceObserver;

    @Captor
    private ArgumentCaptor<CreateAccountResponse> createAccountCaptor;

    @Captor
    private ArgumentCaptor<DepositResponse> depositCaptor;

    @Captor
    private ArgumentCaptor<GetBalanceResponse> getBalanceCaptor;

    @InjectMocks
    private PaymentsGrpcServer grpcServer;

    @Test
    void createAccount_Success() {
        grpcServer.createAccount(
                CreateAccountRequest.newBuilder().setUserId("123").build(),
                createAccountObserver);

        verify(paymentsService).createAccount(123);
        verify(createAccountObserver).onNext(createAccountCaptor.capture());
        verify(createAccountObserver).onCompleted();

        assertTrue(createAccountCaptor.getValue().getSuccess());
    }

    @Test
    void createAccount_InvalidUserId() {
        grpcServer.createAccount(
                CreateAccountRequest.newBuilder().setUserId("abc").build(),
                createAccountObserver);

        verify(createAccountObserver).onError(any());
        verifyNoInteractions(paymentsService);
    }

    @Test
    void deposit_Success() {
        when(paymentsService.getBalance(123)).thenReturn(100.0);

        grpcServer.deposit(
                DepositRequest.newBuilder()
                        .setUserId("123")
                        .setAmount(50.0)
                        .build(),
                depositObserver);

        verify(paymentsService).deposit(123, 50.0);
        verify(depositObserver).onNext(depositCaptor.capture());
        verify(depositObserver).onCompleted();

        DepositResponse response = depositCaptor.getValue();
        assertTrue(response.getSuccess());
        assertEquals(100.0, response.getNewBalance());
    }

    @Test
    void deposit_ConcurrencyFailure() {
        doThrow(new ObjectOptimisticLockingFailureException("", new Throwable()))
                .when(paymentsService).deposit(eq(123), anyDouble());

        grpcServer.deposit(
                DepositRequest.newBuilder()
                        .setUserId("123")
                        .setAmount(50.0)
                        .build(),
                depositObserver);

        verify(depositObserver).onError(any());
    }

    @Test
    void getBalance_Success() {
        when(paymentsService.getBalance(123)).thenReturn(75.5);

        grpcServer.getBalance(
                GetBalanceRequest.newBuilder().setUserId("123").build(),
                getBalanceObserver);

        verify(getBalanceObserver).onNext(getBalanceCaptor.capture());
        verify(getBalanceObserver).onCompleted();

        assertEquals(75.5, getBalanceCaptor.getValue().getBalance());
    }
    
}