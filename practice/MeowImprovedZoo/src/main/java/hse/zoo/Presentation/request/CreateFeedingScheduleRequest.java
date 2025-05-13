package hse.zoo.Presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateFeedingScheduleRequest(
    @Schema(description = "ID животного", example = "1")
    Integer animalId,

    @Schema(description = "Время приема пищи", example = "12:00")
    String time,

    @Schema(description = "Тип пищи (0 - для травоядных, 1 - для хищников)", example = "1")
    Boolean foodType
    
) {}