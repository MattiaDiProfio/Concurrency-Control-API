package com.mdp.next.security.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class UserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminJwt;
    private String customerJwt;

    @BeforeAll
    void registerAndLogin() throws Exception {
        // register a Customer User and an Admin user
        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"customer\", \"email\": \"customer@email.com\", \"address\": \"customer\", \"username\": \"customer\", \"password\": \"customer123\", \"role\": \"CUSTOMER\" }"));

        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"admin\", \"email\": \"admin@email.com\", \"address\": \"admin\", \"username\": \"admin\", \"password\": \"admin123\", \"role\": \"ADMIN\" }"));

        // login both users
        ResultActions loginCustomerAction = this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"customer\", \"password\": \"customer123\" }"));
        MvcResult mvcResult = loginCustomerAction.andReturn();
        String customerActionJwt = mvcResult.getResponse().getContentAsString();

        ResultActions loginAdminAction = this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"admin\", \"password\": \"admin123\" }"));
        mvcResult = loginAdminAction.andReturn();
        String adminActionJwt = mvcResult.getResponse().getContentAsString();

        // extract the token from the response object and store it to be passed into subsequent tests
        this.customerJwt = "Bearer " + this.objectMapper.readTree(customerActionJwt).get("token").asText();
        this.adminJwt = "Bearer " + this.objectMapper.readTree(adminActionJwt).get("token").asText();
    }

    @Test
    public void getProfileUnauthorized() throws Exception {
        // GET /user/2
        // customer is trying to access another user's (the admin) profile, which is not allowed
    }

    @Test
    public void getCustomerProfileSuccess1() throws Exception {
        // GET /user/1
        // admin tries to access a customer's profile and they succeed
    }

    @Test
    public void getCustomerProfileSuccess2() throws Exception {
        // GET /user/1
       // customer tries to access their profile and they succeed
    }

    @Test
    public void getAllUsersUnauthorized() throws Exception {
        // GET /user/all
        // test fails because logged in customer is trying to access admin-only resources
    }

    @Test
    public void getAllUsersSuccessful() throws Exception {
        // admin is able to view all users since this is an admin-only endpoint
    }

    @Test
    public void updateCustomerUnauthorized() throws Exception {
        // admin tries to update the email of the customer - fails
    }

    @Test
    public void updateAdminUnauthorized() throws Exception {
        // customer tries to update the email of the admin - fails
    }

    @Test
    public void updateCustomerSuccess() throws Exception {
        // customer tries to update their email - ok
    }

    @Test
    public void deleteCustomerUnauthorized() throws Exception {
        // admin tries to delete a customer account - fails
    }

    @Test
    public void deleteAdminUnauthorized() throws Exception {
        // customer tries to delete admin account - fails
    }

    @Test
    public void deleteAdminSuccess() throws Exception {
        // admin tries to delete their own account - ok
    }

}
