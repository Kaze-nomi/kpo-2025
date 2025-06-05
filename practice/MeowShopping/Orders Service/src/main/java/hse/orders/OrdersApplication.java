package hse.orders;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@EnableRetry
@SpringBootApplication
public class OrdersApplication {
	public static void main(String[] args) {
		
		SpringApplication.run(OrdersApplication.class, args);
		
	}
}