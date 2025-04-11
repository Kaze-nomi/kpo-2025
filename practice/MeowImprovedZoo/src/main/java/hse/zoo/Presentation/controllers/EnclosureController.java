package hse.zoo.Presentation.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import hse.zoo.Application.facade.ZooFacade;
import hse.zoo.Domain.entities.Enclosure;
import hse.zoo.Presentation.request.CreateEnclosureRequest;

@RestController
@RequestMapping("/api/enclosures")
@RequiredArgsConstructor
@Tag(name = "Вольеры", description = "Управление вольерами")
public class EnclosureController {
    private final ZooFacade zooFacade;

    @GetMapping("/get/{id}")
    @Operation(summary = "Получить вольер по id")
    public ResponseEntity<Enclosure> getEnclosureById(@PathVariable("id") int id) {
        try {
            Enclosure tmp = zooFacade.getEnclosure(id);
            return ResponseEntity.ok(tmp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }
    
    @PostMapping("/add")
    @Operation(summary = "Добавить вольер")
    public ResponseEntity<String> addEnclosure(
            @Valid @RequestBody CreateEnclosureRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        try {
            List<String> species = List.of(request.species().split(" "));
            Integer width = request.width();
            Integer height = request.height();
            Integer length = request.length();
            Integer maxAnimalCount = request.maxAnimalCount();
            Integer enclosureId = zooFacade.addEnclosure(species, width, height, length, maxAnimalCount);
            return ResponseEntity.status(HttpStatus.CREATED).body("Добавлен вольер с id " + enclosureId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            e.getMessage());    
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить вольер")
    public ResponseEntity<String> DeleteEnclosure(@PathVariable("id") int id) {
        try {
            zooFacade.deleteEnclosure(id);
            return ResponseEntity.ok("Вольер с id " + id + " удалён");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @GetMapping("/get/all")
    @Operation(summary = "Получить все вольеры")
    public ResponseEntity<String> getAllEnclosures() {
        try {
            List<Enclosure> enclosures = zooFacade.getEnclosures();
            String result = "";
            for (Enclosure enclosure : enclosures) {
                result += enclosure.toString() + ", ID=" + zooFacade.getEnclosureId(enclosure) + "\n";
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }
    
    @GetMapping("/get/empty")
    @Operation(summary = "Получить пустые вольеры")
    public ResponseEntity<String> getEmptyEnclosures() {
        try {
            List<Enclosure> enclosures = zooFacade.getEmptyEnclosures();
            String result = "";
            for (Enclosure enclosure : enclosures) {
                result += enclosure.toString() + ", ID=" + zooFacade.getEnclosureId(enclosure) + "\n";
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }
}