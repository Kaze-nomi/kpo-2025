package hse.kpo.dto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hse.kpo.domains.cars.Car;
import hse.kpo.domains.engines.HandEngine;
import hse.kpo.domains.engines.LevitationEngine;
import hse.kpo.domains.engines.PedalEngine;
import hse.kpo.services.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import hse.kpo.facade.HSE;
import hse.kpo.interfaces.engineInterfaces.AbstractEngine;
import hse.kpo.params.EngineTypes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
@Tag(name = "Автомобили", description = "Управление транспортными средствами")
public class CarController {
    private final CarService carService;
    private final HseService hseService;
    private final HSE hse;

    // GET by ID
    @GetMapping("/{vin}")
    @Operation(summary = "Получить автомобиль по VIN")
    public ResponseEntity<Car> getCarByVin(@PathVariable int vin) {
        return carService.getCars().stream()
                .filter(car -> car.getID() == vin)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Создать автомобиль",
            description = "Для PEDAL требуется pedalSize (1-15)")
    public ResponseEntity<Car> createCar(
            @Valid @RequestBody CarRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        var engineType = EngineTypes.find(request.engineType());
        if (engineType.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No this type");
        }

        var car = switch (engineType.get()) {
            case EngineTypes.PEDAL -> hse.addPedalCar(request.pedalSize());
            case EngineTypes.HAND -> hse.addHandCar();
            case EngineTypes.LEVITATION -> hse.addLevitationCar();
            default -> throw new RuntimeException();
        };

        return ResponseEntity.status(HttpStatus.CREATED).body(car);
    }

    @PostMapping("/sell")
    @Operation(summary = "Продать все доступные автомобили")
    public ResponseEntity<Void> sellAllCars() {
        hse.sellCars();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sell/{vin}")
    @Operation(summary = "Продать автомобиль по VIN")
    public ResponseEntity<Void> sellCar(@PathVariable int vin) {
        var carOptional = carService.getCars().stream()
                .filter(c -> c.getID() == vin)
                .findFirst();

        if (carOptional.isPresent()) {
            var car = carOptional.get();
            carService.getCars().remove(car);
            hseService.sellCar(vin);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{vin}")
    @Operation(summary = "Обновить автомобиль")
    public ResponseEntity<Car> updateCar(
            @PathVariable int vin,
            @Valid @RequestBody CarRequest request) {

        return carService.getCars().stream()
                .filter(car -> car.getID() == vin)
                .findFirst()
                .map(existingCar -> {
                    var updatedCar = createCarFromRequest(request, vin);
                    carService.getCars().remove(existingCar);
                    carService.addExistingCar(updatedCar);
                    return ResponseEntity.ok(updatedCar);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{vin}")
    @Operation(summary = "Удалить автомобиль")
    public ResponseEntity<Void> deleteCar(@PathVariable int vin) {
        boolean removed = carService.getCars().removeIf(car -> car.getID() == vin);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Получить все автомобили с фильтрацией",
            parameters = {
                    @Parameter(name = "engineType", description = "Фильтр по типу двигателя"),
                    @Parameter(name = "minVin", description = "Минимальный VIN")
            })
    public List<Car> getAllCars(
            @RequestParam(required = false) String engineType,
            @RequestParam(required = false) Integer minVin) {

        return carService.getCars().stream()
                .filter(car -> engineType == null || car.getEngineType().equals(engineType))
                .filter(car -> minVin == null || car.getID() >= minVin)
                .toList();
    }

    private Car createCarFromRequest(CarRequest request, int vin) {
        AbstractEngine engine = switch (EngineTypes.valueOf(request.engineType())) {
            case PEDAL -> new PedalEngine(request.pedalSize());
            case HAND -> new HandEngine();
            case LEVITATION -> new LevitationEngine();
        };
        return new Car(vin, engine);
    }
}