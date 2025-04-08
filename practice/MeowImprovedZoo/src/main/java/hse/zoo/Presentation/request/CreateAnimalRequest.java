package hse.zoo.Presentation.request;

import java.util.Date;

import org.springframework.lang.Nullable;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateAnimalRequest(
        @Schema(description = "ID вольера (если нет вольера - null)", example = "null")
        @Nullable
        Integer enclosureId,

        @Schema(description = "Вид животного", example = "Тигр")
        String species,

        @Schema(description = "Имя животного", example = "Мурзик")
        String name,

        @Schema(description = "Дата рождения животного в формате гггг-мм-дд", example = "2022-01-01")
        Date birthDate,

        @Schema(description = "Пол животного (0 - мужской, 1 - женский)", example = "0")
        Boolean sex,

        @Schema(description = "Любимая еда животного", example = "Курятина")
        String favouriteFood,

        @Schema(description = "Состояние здоровья животного (1 - здоровый, 0 - больной)", example = "1")
        Boolean isHealthy
) {}