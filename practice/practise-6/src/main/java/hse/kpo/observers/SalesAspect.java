package hse.kpo.observers;

import hse.kpo.interfaces.ISalesObserver;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@Aspect
@RequiredArgsConstructor
public class SalesAspect {
private final ISalesObserver salesObserver;

    @Around("@annotation(sales)")
    public Object sales(ProceedingJoinPoint pjp, Sales sales) throws Throwable {

        salesObserver.checkCustomers();

        //String operationName = sales.value().isEmpty() ? pjp.getSignature().toLongString() : sales.value();
        try {
            Object result = pjp.proceed();
            salesObserver.checkCustomers();
            return result;
        } catch (Throwable e) {
            throw e;
        }
    }
}