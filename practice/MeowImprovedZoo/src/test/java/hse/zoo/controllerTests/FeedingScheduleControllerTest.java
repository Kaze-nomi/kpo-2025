package hse.zoo.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.zoo.Presentation.request.CreateAnimalRequest;
import hse.zoo.Presentation.request.CreateFeedingScheduleRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FeedingScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // region Helper Methods
    private int createTestAnimal() throws Exception {
        CreateAnimalRequest request = new CreateAnimalRequest(
            null,
            "Lion",
            "Simba",
            "2020-01-01",
            false,
            "Meat",
            true
        );
        
        MvcResult result = mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        
        return extractIdFromResponse(result);
    }

    private int createTestSchedule(int animalId, String time, boolean foodType) throws Exception {
        CreateFeedingScheduleRequest request = new CreateFeedingScheduleRequest(
            animalId,
            time,
            foodType
        );
        
        MvcResult result = mockMvc.perform(post("/api/schedules/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        
        return extractIdFromResponse(result);
    }

    private int extractIdFromResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return Integer.parseInt(content.replaceAll("\\D+", ""));
    }
    // endregion

    // region CRUD Tests
    @Test
    void createFeedingSchedule_Success() throws Exception {
        int animalId = createTestAnimal();
        
        CreateFeedingScheduleRequest request = new CreateFeedingScheduleRequest(
            animalId,
            "12:30",
            true
        );

        mockMvc.perform(post("/api/schedules/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(containsString("Добавлено расписание с id")));
    }

    @Test
    void createFeedingSchedule_InvalidData() throws Exception {
        // Test invalid time format
        CreateFeedingScheduleRequest invalidRequest = new CreateFeedingScheduleRequest(
            1,
            "25:70", // Invalid time
            true
        );

        mockMvc.perform(post("/api/schedules/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getScheduleById_Success() throws Exception {
        int animalId = createTestAnimal();
        int scheduleId = createTestSchedule(animalId, "09:00", true);

        mockMvc.perform(get("/api/schedules/get/{id}", scheduleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foodType").value(true))
                .andExpect(jsonPath("$.feedingTime").value("09:00:00"));
    }

    @Test
    void getScheduleById_NotFound() throws Exception {
        mockMvc.perform(get("/api/schedules/get/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteSchedule_Success() throws Exception {
        int animalId = createTestAnimal();
        int scheduleId = createTestSchedule(animalId, "15:00", false);

        mockMvc.perform(delete("/api/schedules/delete/{id}", scheduleId))
                .andExpect(status().isOk())
                .andExpect(content().string("Вольер с id " + scheduleId + " удалён"));
    }

    @Test
    void deleteSchedule_NotFound() throws Exception {
        mockMvc.perform(delete("/api/schedules/delete/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSchedule_Success() throws Exception {
        int animalId = createTestAnimal();
        int scheduleId = createTestSchedule(animalId, "10:00", true);
        
        CreateFeedingScheduleRequest updateRequest = new CreateFeedingScheduleRequest(
            animalId,
            "11:00",
            false
        );

        mockMvc.perform(put("/api/schedules/change/{id}", scheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Расписание с id " + scheduleId + " изменено"));

        // Verify update
        mockMvc.perform(get("/api/schedules/get/{id}", scheduleId))
                .andExpect(jsonPath("$.feedingTime").value("11:00:00"))
                .andExpect(jsonPath("$.foodType").value(false));
    }

    @Test
    void updateSchedule_NotFound() throws Exception {
        CreateFeedingScheduleRequest request = new CreateFeedingScheduleRequest(
            1,
            "12:00",
            true
        );

        mockMvc.perform(put("/api/schedules/change/{id}", 999)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    // endregion

    // region Business Logic Tests
    @Test
    void getAllSchedules_Success() throws Exception {
        int animalId = createTestAnimal();
        createTestSchedule(animalId, "08:00", true);
        createTestSchedule(animalId, "20:00", false);

        mockMvc.perform(get("/api/schedules/all"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("08:00")))
                .andExpect(content().string(containsString("20:00")));
    }

    @Test
    void checkFeedingStatus_Fed() throws Exception {
        int animalId = createTestAnimal();
        int scheduleId = createTestSchedule(animalId, "00:00", true); // Time in past
        
        // Wait to ensure time has passed
        TimeUnit.SECONDS.sleep(1);

        mockMvc.perform(get("/api/schedules/check/{id}", scheduleId))
                .andExpect(status().isOk())
                .andExpect(content().string("Животное уже поело"));
    }

    @Test
    void checkFeedingStatus_NotFed() throws Exception {
        int animalId = createTestAnimal();
        int scheduleId = createTestSchedule(animalId, "23:59", true); // Future time
        
        mockMvc.perform(get("/api/schedules/check/{id}", scheduleId))
                .andExpect(status().isOk())
                .andExpect(content().string("Животное еще не поело"));
    }

    @Test
    void checkFeedingStatus_InvalidSchedule() throws Exception {
        mockMvc.perform(get("/api/schedules/check/{id}", 999))
                .andExpect(status().isNotFound());
    }
    // endregion

    // region Edge Cases
    @Test
    void createSchedule_NonExistingAnimal() throws Exception {
        CreateFeedingScheduleRequest request = new CreateFeedingScheduleRequest(
            999, // Non-existing animal
            "12:00",
            true
        );

        mockMvc.perform(post("/api/schedules/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateSchedule_ChangeAnimal() throws Exception {
        CreateAnimalRequest request = new CreateAnimalRequest(
            null,
            "Lion",
            "Simba",
            "2020-01-01",
            false,
            "Meat",
            true
        );
        
        MvcResult result = mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();

        int animal1 = extractIdFromResponse(result);

        CreateAnimalRequest request_2 = new CreateAnimalRequest(
            null,
            "Lion",
            "Tigra",
            "2020-01-01",
            false,
            "Meat",
            true
        );
        
        MvcResult result_2 = mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request_2)))
                .andReturn();

        int animal2 = extractIdFromResponse(result_2);

        int scheduleId = createTestSchedule(animal1, "09:00", true);

        CreateFeedingScheduleRequest updateRequest = new CreateFeedingScheduleRequest(
            animal2,
            "09:00",
            true
        );

        mockMvc.perform(put("/api/schedules/change/{id}", scheduleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/schedules/get/{id}", scheduleId))
                .andExpect(jsonPath("$.animal.name").value("Tigra"));
    }
    // endregion
}