package com.mdp.next.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class AuthenticationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeAll
    void registerAndLogin() throws Exception {
        // register a new user
        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"mattia di profio\", \"email\": \"mattia@email.com\", \"address\": \"123 Any Place\", \"username\": \"mattia\", \"password\": \"password123\", \"role\": \"ADMIN\" }"));

        // login the newly registered user
        ResultActions resultActions = this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"mattia\", \"password\": \"password123\" }"));

        MvcResult mvcResult = resultActions.andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();

        // extract the token from the response object and store it to be passed into subsequent tests
        this.jwtToken = "Bearer " + this.objectMapper.readTree(contentAsString).get("token").asText();
    }

    @AfterAll
    void logout() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/logout"));
    }

    @Test
    public void testCannotRegisterSameUserTwice() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
            .post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", this.jwtToken)
            .content("{ \"name\": \"mattia di profio\", \"email\": \"mattia@email.com\", \"address\": \"123 Any Place\", \"username\": \"mattia\", \"password\": \"password123\", \"role\": \"ADMIN\" }");

        mockMvc.perform(request).andExpect(status().is4xxClientError());
    }

    @Test
    public void testCannotLoginNonExistentUser() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
            .post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"testUser1\", \"password\": \"dummyPassword123\" }");

        mockMvc.perform(request).andExpect(status().is4xxClientError());
    }

    @Test
    public void cannotLoginWithIncorrectPassword() throws Exception {
        RequestBuilder registerNewUser = MockMvcRequestBuilders
                .post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"test user 2\", \"email\": \"testUser2@email.com\", \"address\": \"123 Any Place\", \"username\": \"testUser2\", \"password\": \"dummyPassword123\", \"role\": \"CUSTOMER\" }");

        mockMvc.perform(registerNewUser).andExpect(status().is2xxSuccessful());

        RequestBuilder loginNewUser = MockMvcRequestBuilders
            .post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"testUser2\", \"password\": \"wrongPassword123\" }");

        mockMvc.perform(loginNewUser).andExpect(status().is4xxClientError());

    }

    @Test
    public void cannotLogoutBeforeLogin() throws Exception {
        RequestBuilder registerNewUser = MockMvcRequestBuilders
                .post("/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"name\": \"test user 3\", \"email\": \"testUser3@email.com\", \"address\": \"123 Any Place\", \"username\": \"testUser3\", \"password\": \"dummyPassword123\", \"role\": \"CUSTOMER\" }");

        mockMvc.perform(registerNewUser).andExpect(status().is2xxSuccessful());

        this.mockMvc.perform(MockMvcRequestBuilders.get("/logout")).andExpect(status().is4xxClientError());

    }

}
