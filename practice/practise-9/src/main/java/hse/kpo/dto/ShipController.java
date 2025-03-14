package hse.kpo.dto;

import java.util.List;
import java.util.Optional;

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

import hse.kpo.domains.ships.Ship;
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
import hse.kpo.interfaces.engineInterfaces.IEngine;
import hse.kpo.params.EngineTypes;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/ships")
@RequiredArgsConstructor
@Tag(name = "Корабли", description = "Управление транспортными средствами")
public class ShipController {
    private final ShipService ShipService;
    private final HseService hseService;
    private final HSE hse;

    // GET by ID
    @GetMapping("/{vin}")
    @Operation(summary = "Получить корабль по VIN")
    public ResponseEntity<Ship> getShipByVin(@PathVariable int vin) {
        return ShipService.getShips().stream()
                .filter(ship -> ship.getID() == vin)
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать корабль",
            description = "Для PEDAL требуется pedalSize (1-15)")
    public ResponseEntity<Ship> createShip(
            @Valid @RequestBody ShipRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        Optional<EngineTypes> engineType = EngineTypes.find(request.engineType());
        if (!engineType.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No this type");
        }

        Ship ship;
        switch (engineType.get()) {
            case PEDAL -> ship = hse.addPedalShip(request.pedalSize());
            case HAND -> ship = hse.addHandShip();
            case LEVITATION -> ship = hse.addLevitationShip();
            default -> throw new RuntimeException();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ship);
    }

    @PostMapping("/sell")
    @Operation(summary = "Продать все доступные корабли")
    public ResponseEntity<Void> sellAllShips() {
        hse.sellShips();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sell/{vin}")
    @Operation(summary = "Продать корабль по VIN")
    public ResponseEntity<Void> sellShip(@PathVariable int vin) {
        var shipOptional = ShipService.getShips().stream()
                .filter(c -> c.getID() == vin)
                .findFirst();

        if (shipOptional.isPresent()) {
            var ship = shipOptional.get();
            ShipService.getShips().remove(ship);
            hseService.sellShip(vin);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{vin}")
    @Operation(summary = "Обновить корабль")
    public ResponseEntity<Ship> updateShip(
            @PathVariable int vin,
            @Valid @RequestBody ShipRequest request) {

        return ShipService.getShips().stream()
                .filter(ship -> ship.getID() == vin)
                .findFirst()
                .map(existingShip -> {
                    var updatedShip = createShipFromRequest(request, vin);
                    ShipService.getShips().remove(existingShip);
                    ShipService.addExistingShip(updatedShip);
                    return ResponseEntity.ok(updatedShip);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{vin}")
    @Operation(summary = "Удалить корабль")
    public ResponseEntity<Void> deleteShip(@PathVariable int vin) {
        boolean removed = ShipService.getShips().removeIf(ship -> ship.getID() == vin);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping
    @Operation(summary = "Получить все корабли с фильтрацией",
            parameters = {
                    @Parameter(name = "engineType", description = "Фильтр по типу двигателя"),
                    @Parameter(name = "minVin", description = "Минимальный VIN")
            })
    public List<Ship> getAllShips(
            @RequestParam(required = false) String engineType,
            @RequestParam(required = false) Integer minVin) {

        return ShipService.getShips().stream()
                .filter(ship -> engineType == null || ship.getEngineType().equals(engineType))
                .filter(ship -> minVin == null || ship.getID() >= minVin)
                .toList();
    }

    private Ship createShipFromRequest(ShipRequest request, int vin) {
        IEngine engine = switch (EngineTypes.valueOf(request.engineType())) {
            case PEDAL -> new PedalEngine(request.pedalSize());
            case HAND -> new HandEngine();
            case LEVITATION -> new LevitationEngine();
        };
        return new Ship(vin, engine);
    }
}