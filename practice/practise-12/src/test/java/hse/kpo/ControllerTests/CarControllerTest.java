// package hse.kpo.ControllerTests;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import hse.kpo.dto.CarRequest;
// import hse.kpo.dto.CarResponse;

// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.annotation.DirtiesContext;
// import org.springframework.test.web.servlet.MockMvc;

// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.junit.jupiter.api.Assertions.assertAll;
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

// @SpringBootTest
// @AutoConfigureMockMvc
// @DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
// class CarControllerTest {
//     @Autowired
//     private MockMvc mockMvc;
//     @Autowired
//     private ObjectMapper objectMapper;

//    @Test
//     @DisplayName("Создание педального автомобиля с валидными параметрами")
//     void createPedalCar_ValidData_Returns2012() throws Exception {
//         CarRequest request = new CarRequest("PEDAL", 10);

//         String responseJson = mockMvc.perform(post("/api/cars")
//                         .contentType(MediaType.APPLICATION_JSON)
//                         .content(objectMapper.writeValueAsString(request)))
//                 .andExpect(status().isCreated())
//                 .andReturn().getResponse().getContentAsString();

//         CarResponse response = objectMapper.readValue(responseJson, CarResponse.class);
//         assertAll(
//                 () -> assertNotNull(response.vin(), "VIN должен быть присвоен"),
//                 () -> assertEquals("PedalEngine(size=10)", response.engineType(),
//                         "Тип двигателя должен быть PEDAL")
//         );
//     }
// }