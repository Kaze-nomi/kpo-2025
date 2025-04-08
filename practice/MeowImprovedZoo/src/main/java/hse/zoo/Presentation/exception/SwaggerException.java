package hse.zoo.Presentation.exception;

import lombok.Getter;

@Getter
public class SwaggerException extends RuntimeException {
    private final int code;

    public SwaggerException(int code, String message) {
        super(message);
        this.code = code;
    }
}