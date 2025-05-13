package hse.zoo.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import hse.zoo.Presentation.request.CreateAnimalRequest;
import hse.zoo.Presentation.request.CreateEnclosureRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // region Helper Methods
    private int createTestAnimal(String species, Boolean isHealthy) throws Exception {
        CreateAnimalRequest request = new CreateAnimalRequest(
            null,
            species,
            "TestAnimal",
            "2020-01-01",
            false,
            "Food",
            isHealthy
        );
        
        MvcResult result = mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        
        return extractIdFromResponse(result);
    }

    private int createTestEnclosure(String species, int capacity) throws Exception {
        CreateEnclosureRequest request = new CreateEnclosureRequest(
            species,
            10, 10, 10,
            capacity
        );
        
        MvcResult result = mockMvc.perform(post("/api/enclosures/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn();
        
        return extractIdFromResponse(result);
    }

    private int extractIdFromResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return Integer.parseInt(content.replaceAll("\\D+", ""));
    }

    private void transferAnimal(int animalId, int enclosureId) throws Exception {
        mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", animalId, enclosureId))
                .andExpect(status().isOk());
    }
    // endregion

    // region CRUD Tests
    @Test
    void createAnimal_Success() throws Exception {
        CreateAnimalRequest request = new CreateAnimalRequest(
            null,
            "Lion",
            "Simba",
            "2020-01-01",
            false,
            "Meat",
            true
        );

        mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(containsString("Добавлено животное с id")));
    }

    @Test
    void createAnimal_InvalidData() throws Exception {
        // Test all possible invalid combinations
        CreateAnimalRequest invalidRequest = new CreateAnimalRequest(
            null,
            "L1on", // Invalid species with digit
            "", // Empty name
            "invalid-date", // Wrong date format
            null, // Missing sex
            "M3at!", // Invalid food
            null // Missing health status
        );

        mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAnimalById_Success() throws Exception {
        int animalId = createTestAnimal("Tiger", true);

        mockMvc.perform(get("/api/animals/get/{id}", animalId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("TestAnimal"))
                .andExpect(jsonPath("$.species.name").value("Tiger"));
    }

    @Test
    void getAnimalById_NotFound() throws Exception {
        mockMvc.perform(get("/api/animals/get/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAnimal_Success() throws Exception {
        int animalId = createTestAnimal("Bear", true);

        mockMvc.perform(delete("/api/animals/delete/{id}", animalId))
                .andExpect(status().isOk())
                .andExpect(content().string("Животное с id " + animalId + " удалено"));
    }

    @Test
    void deleteAnimal_NotFound() throws Exception {
        mockMvc.perform(delete("/api/animals/delete/{id}", 999))
                .andExpect(status().isNotFound());
    }
    // endregion

    // region Transfer Tests
    @Test
    void transferAnimal_Success() throws Exception {
        int enclosureId = createTestEnclosure("Tiger", 2);
        int animalId = createTestAnimal("Tiger", true);

        mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", animalId, enclosureId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("переведено")));

        // Verify transfer
        mockMvc.perform(get("/api/animals/get/enclosureFromAnimal/{id}", animalId))
                .andExpect(status().isFound())
                .andExpect(content().string(containsString(String.valueOf(enclosureId))));
    }

    @Test
    void transferAnimal_InvalidEnclosure() throws Exception {
        int animalId = createTestAnimal("Tiger", true);
        int invalidEnclosureId = 999;

        mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", animalId, invalidEnclosureId))
                .andExpect(status().isNotFound());
    }

    @Test
    void transferAnimal_WrongSpecies() throws Exception {
        int enclosureId = createTestEnclosure("Lion", 1);
        int animalId = createTestAnimal("Tiger", true);

        mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", animalId, enclosureId))
                .andExpect(status().isNotFound());
    }

    @Test
    void transferAnimal_FullEnclosure() throws Exception {
        int enclosureId = createTestEnclosure("Tiger", 1);
        int animal1 = createTestAnimal("Tiger", true);
        int animal2 = createTestAnimal("Tiger", true);

        // Transfer first animal
        mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", animal1, enclosureId))
                .andExpect(status().isOk());

        // Try transfer second
        mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", animal2, enclosureId))
                .andExpect(status().isNotFound());
    }
    // endregion

    // region Query Tests
    @Test
    void getAllAnimals_Empty() throws Exception {
        mockMvc.perform(get("/api/animals/get/all"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Все животные: \n")));
    }

    @Test
    void getAllAnimals_WithData() throws Exception {
        createTestAnimal("Lion", true);
        createTestAnimal("Tiger", true);

        mockMvc.perform(get("/api/animals/get/all"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TestAnimal")))
                .andExpect(content().string(containsString("Lion")))
                .andExpect(content().string(containsString("Tiger")));
    }

    @Test
    void getAnimalsInEnclosure_Success() throws Exception {
        int enclosureId = createTestEnclosure("Tiger", 2);
        int animalId = createTestAnimal("Tiger", true);
        transferAnimal(animalId, enclosureId);

        mockMvc.perform(get("/api/animals/get/enclosure/{enclosureId}", enclosureId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tiger")))
                .andExpect(content().string(containsString("ID=" + animalId)));
    }

    @Test
    void getAnimalsInEnclosure_Empty() throws Exception {
        int enclosureId = createTestEnclosure("Tiger", 1);

        mockMvc.perform(get("/api/animals/get/enclosure/{enclosureId}", enclosureId))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Животные в вольере с id " + enclosureId + ": \n")));
    }

    @Test
    void getIllAnimals_Success() throws Exception {
        createTestAnimal("Tiger", false);
        createTestAnimal("Lion", true);

        mockMvc.perform(get("/api/animals/get/ill"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tiger")))
                .andExpect(content().string(not(containsString("Lion"))));
    }

    @Test
    void getFreeAnimals_Success() throws Exception {
        createTestAnimal("Tiger", true);
        int enclosedAnimalId = createTestAnimal("Lion", true);
        int enclosureId = createTestEnclosure("Lion", 1);
        transferAnimal(enclosedAnimalId, enclosureId);

        mockMvc.perform(get("/api/animals/get/freeAnimals"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tiger")))
                .andExpect(content().string(not(containsString("Lion"))));
    }
    // endregion

    // region Health Management Tests
    @Test
    void healAnimal_Success() throws Exception {
        int sickAnimalId = createTestAnimal("Tiger", false);

        mockMvc.perform(put("/api/animals/heal/{animalId}", sickAnimalId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/animals/get/{id}", sickAnimalId))
                .andExpect(jsonPath("$.isHealthy").value(true));
    }

    @Test
    void healAnimal_NotFound() throws Exception {
        mockMvc.perform(put("/api/animals/heal/{animalId}", 999))
                .andExpect(status().isNotFound());
    }
    // endregion

    // region Enclosure Check Tests
    @Test
    void getEnclosureByAnimalId_Success() throws Exception {
        int enclosureId = createTestEnclosure("Tiger", 1);
        int animalId = createTestAnimal("Tiger", true);
        transferAnimal(animalId, enclosureId);

        mockMvc.perform(get("/api/animals/get/enclosureFromAnimal/{id}", animalId))
                .andExpect(status().isFound())
                .andExpect(content().string(containsString(String.valueOf(enclosureId))));
    }

    @Test
    void getEnclosureByAnimalId_NoEnclosure() throws Exception {
        int animalId = createTestAnimal("Tiger", true);

        mockMvc.perform(get("/api/animals/get/enclosureFromAnimal/{id}", animalId))
                .andExpect(status().isNotFound());
    }
    // endregion

    // region Edge Cases
    @Test
    void createAnimal_WithEnclosure() throws Exception {
        int enclosureId = createTestEnclosure("Tiger", 1);

        CreateAnimalRequest request = new CreateAnimalRequest(
            enclosureId,
            "Tiger",
            "Simba",
            "2020-01-01",
            false,
            "Meat",
            true
        );

        MvcResult result = mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        int animalId = extractIdFromResponse(result);

        mockMvc.perform(get("/api/animals/get/enclosureFromAnimal/{id}", animalId))
                .andExpect(status().isFound())
                .andExpect(content().string(containsString(String.valueOf(enclosureId))));
    }

    @Test
    void createAnimal_WithInvalidEnclosure() throws Exception {
        CreateAnimalRequest request = new CreateAnimalRequest(
            999, // Non-existing enclosure
            "Tiger",
            "Simba",
            "2020-01-01",
            false,
            "Meat",
            true
        );

        mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void massOperationsTest() throws Exception {
        // Test capacity limits
        int enclosureId = createTestEnclosure("Tiger", 2);
        
        // Create and transfer 3 animals
        for (int i = 1; i <= 3; i++) {
            int animalId = createTestAnimal("Tiger", true);
            if (i <= 2) {
                transferAnimal(animalId, enclosureId);
            } else {
                mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", animalId, enclosureId))
                        .andExpect(status().isNotFound());
            }
        }
    }
    // endregion
}