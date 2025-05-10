package hse.zoo.Presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateEnclosureRequest(
        @Schema(description = "Тип вольера", example = "хищники рыбы птицы")
        String species,

        @Schema(description = "Ширина вольера в метрах", example = "100")
        Integer width,

        @Schema(description = "Высота вольера в метрах", example = "100")
        Integer height,

        @Schema(description = "Длина вольера в метрах", example = "100")
        Integer length,

        @Schema(description = "Максимальное количество животных в вольере", example = "100")
        Integer maxAnimalCount
) {}