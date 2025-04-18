package hse.zoo.Domain.valueobjects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EnclosureSize {

    @Getter
    private Integer length;

    @Getter
    private Integer width;

    @Getter
    private Integer height;

    public EnclosureSize(Integer length, Integer width, Integer height) {
        if (length < 0 || width < 0 || height < 0) {
            throw new IllegalArgumentException("The size of the enclosure cannot be less than 0");
        }
        this.length = length;
        this.width = width;
        this.height = height;
    }

}