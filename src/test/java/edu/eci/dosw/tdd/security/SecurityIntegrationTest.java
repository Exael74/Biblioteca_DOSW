package edu.eci.dosw.tdd.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.eci.dosw.tdd.controller.dto.LoginRequest;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    private String userToken;
    private String librarianToken;

    @BeforeEach
    void setup() throws Exception {
        String suffix = String.valueOf(System.nanoTime());

        User user = User.builder()
                .name("User " + suffix)
                .username("user" + suffix)
                .password("secret-user")
                .role(User.Role.USER)
                .build();
        userService.addUser(user);

        User librarian = User.builder()
                .name("Librarian " + suffix)
                .username("librarian" + suffix)
                .password("secret-lib")
                .role(User.Role.LIBRARIAN)
                .build();
        userService.addUser(librarian);

        userToken = loginAndGetToken("user" + suffix, "secret-user");
        librarianToken = loginAndGetToken("librarian" + suffix, "secret-lib");
    }

    @Test
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/books/available"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401WhenTokenInvalid() throws Exception {
        mockMvc.perform(get("/books/available")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenRoleIsInsufficient() throws Exception {
        String payload = """
                {
                  "title": "Clean Architecture",
                  "author": "Robert C. Martin",
                  "totalCopies": 3,
                  "availableCopies": 3
                }
                """;

        mockMvc.perform(post("/books")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowLibrarianAndUserOnAuthorizedOperations() throws Exception {
        Book created = bookService.addBook(Book.builder()
                .title("DDD")
                .author("Eric Evans")
                .totalCopies(2)
                .availableCopies(2)
                .build());

        String borrowPayload = """
                {
                  "bookId": "%s"
                }
                """.formatted(created.getId());

        mockMvc.perform(post("/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(borrowPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        mockMvc.perform(patch("/books/{id}/inventory", created.getId())
                        .header("Authorization", "Bearer " + librarianToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"totalCopies\":5,\"availableCopies\":4}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCopies").value(5));
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
}