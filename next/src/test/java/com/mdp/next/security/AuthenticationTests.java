package com.mdp.next.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.List;

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

    }

    @Test
    public void testCannotLoginNonExistentUser() throws Exception {

    }

    @Test
    public void cannotLoginWithIncorrectPassword() throws Exception {

    }

    @Test
    public void cannotLogoutBeforeLogin() throws Exception {

    }

    @Test
    public void cannotLoginWithExpiredJWT() throws Exception {

    }

    @Test
    public void testLogoutInvalidatesAllJwts() throws Exception {

    }

}
