package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.dto.CreateUserRequest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Tag("IntegrationTest")
public class UserControllerIntegrationTest {

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.20")
            .withExposedPorts(27017)
            .withSharding();

    @BeforeAll
    public static void setUp() {
        mongoDBContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        final String MONGO_URL = "mongodb://localhost:";
        final String PORT = String.valueOf(mongoDBContainer.getMappedPort(27017));
        registry.add("mongodb.connection.url", () -> MONGO_URL + PORT);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleanUp() {
        userRepository.deleteAll();
    }

    private String createUser(String name, String email) throws Exception {
        CreateUserRequest request = new CreateUserRequest(name, email);

        String response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.email").value(email))
            .andExpect(jsonPath("$.id").exists())
            .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    public void testCreateAndGetUser() throws Exception {
        String userId = createUser("Alice", "alice@example.com");

        mockMvc.perform(get("/users/" + userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    public void testGetAllUsers() throws Exception {
        createUser("Alice", "alice@example.com");
        createUser("Bob", "bob@example.com");

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testChangeName() throws Exception {
        String userId = createUser("Alice", "alice@example.com");

        mockMvc.perform(patch("/users/" + userId + "/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Alicia\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alicia"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        String userId = createUser("Alice", "alice@example.com");

        mockMvc.perform(delete("/users/" + userId))
            .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void testGetUserByEmail() throws Exception {
        createUser("Alice", "alice@example.com");

        mockMvc.perform(get("/users/by-email").param("email", "alice@example.com"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"));
    }
}
