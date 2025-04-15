package hse.kpo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

public record CustomerRequest(
        @Schema(description = "Имя", example = "Глеб")
        String name,

        @Schema(description = "Сила ног", example = "6")
        @Min(value = 1, message = "Минимальная сила ног - 1")
        int legPower,

        @Schema(description = "Сила рук", example = "10")
        @Min(value = 1, message = "Минимальная сила рук - 1")
        int handPower,

        @Schema(description = "IQ", example = "60")
        @Min(value = 1, message = "Минимальный IQ - 1")
        int iq
) {}