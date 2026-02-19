package ro.unibuc.hello.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import ro.unibuc.hello.IntegrationTestBase;
import ro.unibuc.hello.data.TodoRepository;
import ro.unibuc.hello.data.UserRepository;
import ro.unibuc.hello.dto.CreateTodoRequest;
import ro.unibuc.hello.dto.CreateUserRequest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("TodoController Integration Tests")
class TodoControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void cleanUp() {
        todoRepository.deleteAll();
        userRepository.deleteAll();
    }

    private void createUser(String name, String email) throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName(name);
        request.setEmail(email);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    private String createTodo(String description, String assigneeEmail) throws Exception {
        CreateTodoRequest request = new CreateTodoRequest(description, assigneeEmail);

        String response = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(description))
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.assigneeEmail").value(assigneeEmail))
                .andExpect(jsonPath("$.id").exists())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asText();
    }

    @Test
    public void testCreateAndGetTodo_validTodoCreation_retrievesTodoSuccessfully() throws Exception {
        createUser("Alice", "alice@example.com");
        String todoId = createTodo("Buy milk", "alice@example.com");

        mockMvc.perform(get("/api/todos/" + todoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Buy milk"))
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.assigneeName").value("Alice"))
                .andExpect(jsonPath("$.assigneeEmail").value("alice@example.com"));
    }

    @Test
    public void testGetTodosByUser_multipleUsersWithDifferentTodos_filtersCorrectly() throws Exception {
        createUser("Alice", "alice@example.com");
        createUser("Bob", "bob@example.com");
        createTodo("Buy milk", "alice@example.com");
        createTodo("Walk the dog", "alice@example.com");
        createTodo("Clean house", "bob@example.com");

        mockMvc.perform(get("/api/todos").param("assigneeEmail", "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/todos").param("assigneeEmail", "bob@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    public void testSetDone_toggleDoneStatus_updatesStatusCorrectly() throws Exception {
        createUser("Alice", "alice@example.com");
        String todoId = createTodo("Buy milk", "alice@example.com");

        mockMvc.perform(patch("/api/todos/" + todoId + "/done")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(true));

        mockMvc.perform(patch("/api/todos/" + todoId + "/done")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.done").value(false));
    }

    @Test
    public void testAssign_reassignToDifferentUser_updateAssigneeSuccessfully() throws Exception {
        createUser("Alice", "alice@example.com");
        createUser("Bob", "bob@example.com");
        String todoId = createTodo("Buy milk", "alice@example.com");

        mockMvc.perform(patch("/api/todos/" + todoId + "/assignee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newAssigneeEmail\":\"bob@example.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assigneeName").value("Bob"))
                .andExpect(jsonPath("$.assigneeEmail").value("bob@example.com"));
    }

    @Test
    public void testEditDescription_validNewDescription_updatesDescriptionSuccessfully() throws Exception {
        createUser("Alice", "alice@example.com");
        String todoId = createTodo("Buy milk", "alice@example.com");

        mockMvc.perform(patch("/api/todos/" + todoId + "/description")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"Buy oat milk\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Buy oat milk"));
    }

    @Test
    public void testDeleteTodo_existingTodo_deletesSuccessfully() throws Exception {
        createUser("Alice", "alice@example.com");
        String todoId = createTodo("Buy milk", "alice@example.com");

        mockMvc.perform(delete("/api/todos/" + todoId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/todos").param("assigneeEmail", "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
