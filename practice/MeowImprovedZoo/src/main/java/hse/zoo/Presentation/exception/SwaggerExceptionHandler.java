package hse.zoo.Presentation.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "hse.zoo")
public class SwaggerExceptionHandler {
    @ExceptionHandler(SwaggerException.class)
    public ResponseEntity<SwaggerException> handleKpoException(SwaggerException ex) {
        return ResponseEntity.status(HttpStatus.valueOf(ex.getCode()))
                .body(ex);
    }

    @ExceptionHandler(Error.class)
    public ResponseEntity<SwaggerException> handleError(Error error) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SwaggerException(HttpStatus.INTERNAL_SERVER_ERROR.value(), error.getMessage()));
    }
}