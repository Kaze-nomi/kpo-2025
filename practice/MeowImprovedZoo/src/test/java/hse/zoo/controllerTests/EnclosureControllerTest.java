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
class EnclosureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // region Helper Methods
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

    private int createTestAnimal(int enclosureId) throws Exception {
        CreateAnimalRequest request = new CreateAnimalRequest(
            enclosureId,
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

        if (result.getResponse().getStatus() != 201) {
            return -1;
        }
        
        return extractIdFromResponse(result);
    }

    private int extractIdFromResponse(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        return Integer.parseInt(content.replaceAll("\\D+", ""));
    }
    // endregion

    // region CRUD Tests
    @Test
    void createEnclosure_Success() throws Exception {
        CreateEnclosureRequest request = new CreateEnclosureRequest(
            "Lion Tiger",
            10, 5, 15,
            5
        );

        mockMvc.perform(post("/api/enclosures/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(containsString("Добавлен вольер с id")));
    }

    @Test
    void createEnclosure_InvalidData() throws Exception {
        CreateEnclosureRequest invalidRequest = new CreateEnclosureRequest(
            "L1on", // Invalid species with digit
            -10,    // Negative width
            0,      // Zero height
            null,   // Missing length
            -5      // Negative capacity
        );

        mockMvc.perform(post("/api/enclosures/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEnclosureById_Success() throws Exception {
        int enclosureId = createTestEnclosure("Lion", 5);

        mockMvc.perform(get("/api/enclosures/get/{id}", enclosureId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maxAnimalCount").value(5))
                .andExpect(jsonPath("$.type[0].name").value("Lion"));
    }

    @Test
    void getEnclosureById_NotFound() throws Exception {
        mockMvc.perform(get("/api/enclosures/get/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEnclosure_Success() throws Exception {
        int enclosureId = createTestEnclosure("Tiger", 2);

        mockMvc.perform(delete("/api/enclosures/delete/{id}", enclosureId))
                .andExpect(status().isOk())
                .andExpect(content().string("Вольер с id " + enclosureId + " удалён"));
    }

    @Test
    void deleteEnclosure_WithAnimals() throws Exception {
        int enclosureId = createTestEnclosure("Lion", 2);
        createTestAnimal(enclosureId);

        mockMvc.perform(delete("/api/enclosures/delete/{id}", enclosureId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEnclosure_NotFound() throws Exception {
        mockMvc.perform(delete("/api/enclosures/delete/{id}", 999))
                .andExpect(status().isNotFound());
    }
    // endregion

    // region Query Tests
    @Test
    void getAllEnclosures_Empty() throws Exception {
        mockMvc.perform(get("/api/enclosures/get/all"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));
    }

    @Test
    void getAllEnclosures_WithData() throws Exception {
        createTestEnclosure("Lion", 2);
        createTestEnclosure("Tiger", 3);

        mockMvc.perform(get("/api/enclosures/get/all"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Lion")))
                .andExpect(content().string(containsString("Tiger")));
    }

    @Test
    void getEmptyEnclosures_Success() throws Exception {
        createTestEnclosure("Bear", 1);
        int occupiedEnclosureId = createTestEnclosure("Wolf", 1);
        createTestAnimal(occupiedEnclosureId);

        mockMvc.perform(get("/api/enclosures/get/empty"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bear")))
                .andExpect(content().string(not(containsString("Simba"))));
    }

    @Test
    void getEmptyEnclosures_AllOccupied() throws Exception {
        int enclosureId = createTestEnclosure("Lion", 1);
        createTestAnimal(enclosureId);

        mockMvc.perform(get("/api/enclosures/get/empty"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("Lion"))));
    }
    // endregion

    // region Capacity Tests
    @Test
    void addAnimalToFullEnclosure_Failure() throws Exception {
        int enclosureId = createTestEnclosure("Lion", 1);
        createTestAnimal(enclosureId);

        CreateAnimalRequest request = new CreateAnimalRequest(
            enclosureId,
            "Lion",
            "Nala",
            "2020-01-01",
            true,
            "Meat",
            true
        );

        mockMvc.perform(post("/api/animals/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    // endregion

    // region Species Validation
    @Test
    void addWrongSpeciesToEnclosure_Failure() throws Exception {
        int enclosureId = createTestEnclosure("Lion", 2);
        
        CreateAnimalRequest request = new CreateAnimalRequest(
            enclosureId,
            "Tiger", // Wrong species
            "ShereKhan",
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
    // endregion

    // region Complex Scenarios
    @Test
    void transferBetweenEnclosures_Success() throws Exception {
        int sourceEnclosure = createTestEnclosure("Lion", 2);
        int targetEnclosure = createTestEnclosure("Lion", 2);
        int animalId = createTestAnimal(sourceEnclosure);

        mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", animalId, targetEnclosure))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/enclosures/get/{id}", sourceEnclosure))
                .andExpect(jsonPath("$.currentAnimalCount").value(0));

        mockMvc.perform(get("/api/enclosures/get/{id}", targetEnclosure))
                .andExpect(jsonPath("$.currentAnimalCount").value(1));
    }

    @Test
    void multipleOperationsStressTest() throws Exception {
        // Create 3 enclosures
        int[] enclosures = new int[3];
        for (int i = 0; i < 3; i++) {
            enclosures[i] = createTestEnclosure("Mixed", 10);
        }

        // Add 5 animals to each
        for (int encId : enclosures) {
            for (int j = 0; j < 5; j++) {
                CreateAnimalRequest request = new CreateAnimalRequest(
                    encId,
                    "Mixed",
                    "Test Animal " + encId + " " + j,
                    "2020-01-01",
                    true,
                    "Mixed",
                    true
                );

                mockMvc.perform(post("/api/animals/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andReturn();

            }
        }

        // Check counts
        mockMvc.perform(get("/api/enclosures/get/{id}", enclosures[0]))
                .andExpect(jsonPath("$.currentAnimalCount").value(5));

        // Transfer all animals from one enclosure to another
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/animals/transfer/{animalId}/{enclosureId}", i, enclosures[1]))
                    .andExpect(status().isOk());
        }

        // Check counts
        mockMvc.perform(get("/api/enclosures/get/{id}", enclosures[0]))
        .andExpect(jsonPath("$.currentAnimalCount").value(0));

        // Check counts
        mockMvc.perform(get("/api/enclosures/get/{id}", enclosures[1]))
        .andExpect(jsonPath("$.currentAnimalCount").value(10));

        // Delete one enclosure
        mockMvc.perform(delete("/api/enclosures/delete/{id}", enclosures[0]))
                .andExpect(status().isOk());

        // Verify remaining
        mockMvc.perform(get("/api/enclosures/get/all"))
                .andExpect(content().string(not(containsString("ID=" + enclosures[0]))));
    }
    // endregion

    // region Edge Cases
    @Test
    void createEnclosureWithZeroSize_Success() throws Exception {
        CreateEnclosureRequest request = new CreateEnclosureRequest(
            "Fish",
            0, 0, 0, // Zero size
            100
        );

        mockMvc.perform(post("/api/enclosures/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createEnclosureWithMaxCapacity_Success() throws Exception {
        CreateEnclosureRequest request = new CreateEnclosureRequest(
            "Bird",
            5, 5, 5,
            Integer.MAX_VALUE
        );

        mockMvc.perform(post("/api/enclosures/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
    // endregion
}