package hse.zoo.Presentation.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import hse.zoo.Application.facade.ZooFacade;
import hse.zoo.Domain.entities.Animal;
import hse.zoo.Presentation.request.CreateAnimalRequest;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
@Tag(name = "Животные", description = "Управление животными")
public class AnimalContoller {
    private final ZooFacade zooFacade;

    @GetMapping("/get/{id}")
    @Operation(summary = "Получить животное по id")
    public ResponseEntity<Animal> getAnimalById(@PathVariable("id") int id) {
        try {
            Animal tmp = zooFacade.getAnimal(id);
            return ResponseEntity.ok(tmp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @GetMapping("/get/enclosureFromAnimal/{id}")
    @Operation(summary = "Проверить в каком вольере находится животное")
    public ResponseEntity<String> getEnclosureByAnimalId(@PathVariable("id") int id) {
        try {
            Integer enclosureId = zooFacade.getEnclosureByAnimalId(id);
            if (enclosureId == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Животное с id " + id + " не находится ни в одном вольере");
            }
            return ResponseEntity.status(HttpStatus.FOUND).body("Животное с id " + id + " находится в вольере с id " + enclosureId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    e.getMessage());        
        }
    }
     
    @PostMapping("/add")
    @Operation(summary = "Добавить животное")
    public ResponseEntity<String> addAnimal(
            @Valid @RequestBody CreateAnimalRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        try {
            Integer enclosureId = request.enclosureId();
            String species = request.species();
            String name = request.name();
            String date = request.birthDate();
            Boolean sex = request.sex();
            String favouriteFood = request.favouriteFood();
            Boolean isHealthy = request.isHealthy();
            Date birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            Integer animalId = zooFacade.addAnimal(enclosureId, name, new Date(birthDate.getTime()), sex, favouriteFood, isHealthy, species);
            return ResponseEntity.status(HttpStatus.CREATED).body("Добавлено животное с id " + animalId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            e.getMessage());    
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить животное")
    public ResponseEntity<String> DeleteAnimal(@PathVariable("id") int id) {
        try {
            zooFacade.deleteAnimal(id);
            return ResponseEntity.ok("Животное с id " + id + " удалено");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @PostMapping("/transfer/{animalId}/{enclosureId}")
    @Operation(summary = "Перевести животное в вольер")
    public ResponseEntity<String> TransferAnimal(@PathVariable("animalId") int animalId, @PathVariable("enclosureId") int enclosureId) {
        try {
            zooFacade.transferAnimalToEnclosure(animalId, enclosureId);
            return ResponseEntity.ok("Животное с id" + animalId + " переведено в вольер с id " + enclosureId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }
    
    @GetMapping("/get/all")
    @Operation(summary = "Получить всех животных")
    public ResponseEntity<String> getAllAnimals() {
        try {
            String result = "";
            List<Animal> animals = zooFacade.getAnimals();
            for (Animal animal : animals) {
                result += animal.toString() + ", ID=" + zooFacade.getAnimalId(animal) + ", EnclosureID=" + zooFacade.getEnclosureByAnimalId(zooFacade.getAnimalId(animal)) + "\n";
            }
            return ResponseEntity.ok("Все животные: \n" + result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @GetMapping("/get/enclosure/{enclosureId}")
    @Operation(summary = "Получить животных в вольере")
    public ResponseEntity<String> getAnimalsInEnclosure(@PathVariable("enclosureId") int enclosureId) {
        try {
            List<Animal> animals = zooFacade.getAnimalsInEnclosure(enclosureId);
            String result = "";
            for (Animal animal : animals) {
                result += animal.toString() + ", ID=" + zooFacade.getAnimalId(animal) + "\n";
            }
            return ResponseEntity.ok("Животные в вольере с id " + enclosureId + ": \n" + result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @GetMapping("/get/ill")
    @Operation(summary = "Получить заболевших животных")
    public ResponseEntity<String> getIllAnimals() {
        try {
            List<Animal> animals = zooFacade.getIllAnimals();
            String result = "";
            for (Animal animal : animals) {
                result += animal.toString() + ", ID=" + zooFacade.getAnimalId(animal) + ", EnclosureID=" + zooFacade.getEnclosureByAnimalId(zooFacade.getAnimalId(animal)) + "\n";
            }
            return ResponseEntity.ok("Больные животные: \n" + result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @PutMapping("/heal/{animalId}")
    @Operation(summary = "Лечить животное")
    public ResponseEntity<String> healAnimal(@PathVariable("animalId") int animalId) {
        try {
            zooFacade.healAnimal(animalId);
            return ResponseEntity.ok("Животное с id " + animalId + " вылечено");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @GetMapping("/get/freeAnimals")
    @Operation(summary = "Получить животных вне вольера")
    public ResponseEntity<String> getFreeAnimals() {
        try {
            List<Animal> animals = zooFacade.getAnimalsWithoutEnclosure();
            String result = "";
            for (Animal animal : animals) {
                result += animal.toString() + ", ID=" + zooFacade.getAnimalId(animal) + "\n";
            }
            return ResponseEntity.ok("Животные вне вольера: \n" + result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }
}