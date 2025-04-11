package hse.zoo.Presentation.controllers;

import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import hse.zoo.Domain.entities.FeedingSchedule;
import hse.zoo.Presentation.request.CreateFeedingScheduleRequest;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Tag(name = "Расписания кормления", description = "Управление расписаниями кормления")
public class FeedingScheduleController {
    private final ZooFacade zooFacade;

    @GetMapping("/get/{id}")
    @Operation(summary = "Получить расписание по id")
    public ResponseEntity<FeedingSchedule> getScheduleById(@PathVariable("id") int id) {
        try {
            FeedingSchedule tmp = zooFacade.getSchedule(id);
            return ResponseEntity.ok(tmp);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }
    
    @PostMapping("/add")
    @Operation(summary = "Добавить расписание")
    public ResponseEntity<String> addSchedule(
            @Valid @RequestBody CreateFeedingScheduleRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        try {
            Integer animalId = request.animalId();
            String feedingTime = request.time();
            Boolean foodType = request.foodType();
            LocalTime localTime = LocalTime.parse(feedingTime, DateTimeFormatter.ofPattern("HH:mm"));
            Time time = Time.valueOf(localTime);
            Integer scheduleId = zooFacade.addFeedingSchedule(animalId, time, foodType);
            return ResponseEntity.status(HttpStatus.CREATED).body("Добавлено расписание с id " + scheduleId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            e.getMessage());    
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Удалить расписание")
    public ResponseEntity<String> DeleteSchedule(@PathVariable("id") int id) {
        try {
            zooFacade.deleteFeedingSchedule(id);
            return ResponseEntity.ok("Вольер с id " + id + " удалён");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @PutMapping("/change/{id}")
    @Operation(summary = "Изменить расписание")
    public ResponseEntity<String> changeSchedule(@PathVariable("id") int id,
            @Valid @RequestBody CreateFeedingScheduleRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    bindingResult.getAllErrors().get(0).getDefaultMessage());
        }

        try {
            Integer animalId = request.animalId();
            String feedingTime = request.time();
            Boolean foodType = request.foodType();
            LocalTime localTime = LocalTime.parse(feedingTime, DateTimeFormatter.ofPattern("HH:mm"));
            Time time = Time.valueOf(localTime);            
            zooFacade.changeSchedule(id, animalId, time, foodType);
            return ResponseEntity.ok("Расписание с id " + id + " изменено");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Получить все расписания")
    public ResponseEntity<String> getAllSchedules() {
        try {
            List<FeedingSchedule> schedules = zooFacade.getSchedules();
            String result = "";
            for (FeedingSchedule schedule : schedules) {
                result += schedule.toString() + ", ID=" + zooFacade.getFeedingScheduleId(schedule) + ", AnimalID=" + zooFacade.getAnimalId(schedule.getAnimal()) + "\n";
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    e.getMessage());        
        }
    }

    @GetMapping("/check/{id}")
    @Operation(summary = "Проверить, поело ли сегодня животное")
    public ResponseEntity<String> checkIfFed(@PathVariable("id") int id) {
        try {
            boolean isFed = zooFacade.checkIfFed(id);
            return ResponseEntity.ok(isFed ? "Животное уже поело" : "Животное еще не поело");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}