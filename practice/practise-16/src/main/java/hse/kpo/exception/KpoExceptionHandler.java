package hse.kpo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "hse.kpo")
public class KpoExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(KpoExceptionHandler.class);

    @ExceptionHandler(KpoException.class)
    public ResponseEntity<KpoException> handleKpoException(KpoException ex) {
        logger.error("KpoException occurred: ", ex);
        return ResponseEntity.status(HttpStatus.valueOf(ex.getCode()))
                .body(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<KpoException> handleException(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new KpoException(HttpStatus.INTERNAL_SERVER_ERROR.value(), 
                    "Internal server error: " + ex.getMessage()));
    }
}