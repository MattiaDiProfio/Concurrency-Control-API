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
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
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
            .content("{ \"name\": \"customer\", \"email\": \"customer@email.com\", \"address\": \"customerStreet\", \"username\": \"customer\", \"password\": \"customer123\", \"role\": \"CUSTOMER\" }"));

        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"admin\", \"email\": \"admin@email.com\", \"address\": \"adminStreet\", \"username\": \"admin\", \"password\": \"admin123\", \"role\": \"ADMIN\" }"));

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
    @Order(1)
    public void getProfileUnauthorized() throws Exception {
        // customer is trying to access another user's (the admin) profile, which is not allowed
        RequestBuilder request = MockMvcRequestBuilders.get("/user/2").header("Authorization", this.customerJwt);
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    public void getCustomerProfileSuccess1() throws Exception {
        // admin tries to access a customer's profile and they succeed
        RequestBuilder request = MockMvcRequestBuilders.get("/user/1").header("Authorization", this.adminJwt);
        mockMvc
            .perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.name").value("customer"))
            .andExpect(jsonPath("$.email").value("customer@email.com"));
    }

    @Test
    @Order(3)
    public void testGetUserFailure() throws Exception {
        // the action fails because the user with the specified ID is not found in the database
        RequestBuilder request = MockMvcRequestBuilders.get("/user/123").header("Authorization", this.adminJwt);
        mockMvc
            .perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message").value("The user with id '123' does not exist in our records"));
    }

    @Test
    @Order(4)
    public void getCustomerProfileSuccess2() throws Exception {
       // customer tries to access their profile and they succeed
        RequestBuilder request = MockMvcRequestBuilders.get("/user/1").header("Authorization", this.customerJwt);
        mockMvc
            .perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.name").value("customer"))
            .andExpect(jsonPath("$.email").value("customer@email.com"));
    }

    @Test
    @Order(5)
    public void getAllUsersUnauthorized() throws Exception {
        // test fails because logged in customer is trying to access admin-only resources
        RequestBuilder request = MockMvcRequestBuilders.get("/user/all").header("Authorization", this.customerJwt);
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(6)
    public void getAllUsersSuccessful() throws Exception {
        // admin is able to view all users since this is an admin-only endpoint
        RequestBuilder request = MockMvcRequestBuilders.get("/user/all").header("Authorization", this.adminJwt);
        mockMvc.perform(request).andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @Order(7)
    public void updateCustomerUnauthorized() throws Exception {
        // admin tries to update the email of the customer - fails
        RequestBuilder request = MockMvcRequestBuilders
                .put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.adminJwt)
                .content("{ \"email\": \"newCustomerEmail@gmail.com\", \"address\": \"123 Java Street\" }");

        mockMvc.perform(request).andExpect(status().isForbidden());

        // assert that the customer profile was not changed by the admin's request
        mockMvc
            .perform(MockMvcRequestBuilders.get("/user/1").header("Authorization", this.adminJwt))
            .andExpect(jsonPath("$.email").value("customer@email.com"))
            .andExpect(jsonPath("$.address").value("customerStreet"));
    }

    @Test
    @Order(8)
    public void updateUserFailureDueToInvalidPayload() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
            .put("/user/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", this.customerJwt)
            .content("{ \"email\": \" \", \"address\": \" \" }");
        mockMvc
            .perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", hasItem("address cannot be blank")))
            .andExpect(jsonPath("$.message", hasItem("email must follow a valid email format")));
    }

    @Test
    @Order(9)
    public void updateAdminUnauthorized() throws Exception {
        // customer tries to update the email of the admin - fails
        RequestBuilder request = MockMvcRequestBuilders
                .put("/user/2")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.customerJwt)
                .content("{ \"email\": \"newAdminEmail@gmail.com\", \"address\": \"123 Java Street\" }");

        mockMvc.perform(request).andExpect(status().isForbidden());

        // assert that the admin profile was not changed by the customer's request
        mockMvc
            .perform(MockMvcRequestBuilders.get("/user/2").header("Authorization", this.adminJwt))
            .andExpect(jsonPath("$.email").value("admin@email.com"))
            .andExpect(jsonPath("$.address").value("adminStreet"));
    }

    @Test
    @Order(10)
    public void updateCustomerSuccess() throws Exception {
        // customer tries to update their email - ok
        RequestBuilder request = MockMvcRequestBuilders
                .put("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.customerJwt)
                .content("{ \"email\": \"newCustomerEmail@gmail.com\", \"address\": \"123 Java Street\" }");

        mockMvc.perform(request).andExpect(status().is2xxSuccessful());

        mockMvc
                .perform(MockMvcRequestBuilders.get("/user/1").header("Authorization", this.customerJwt))
                .andExpect(jsonPath("$.email").value("newCustomerEmail@gmail.com"))
                .andExpect(jsonPath("$.address").value("123 Java Street"));
    }

    @Test
    @Order(11)
    public void deleteCustomerUnauthorized() throws Exception {
        // admin tries to delete a customer account - fails
        RequestBuilder request = MockMvcRequestBuilders.delete("/user/1").header("Authorization", this.adminJwt);
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(12)
    public void deleteAdminUnauthorized() throws Exception {
        // customer tries to delete admin account - fails
        RequestBuilder request = MockMvcRequestBuilders.delete("/user/2").header("Authorization", this.customerJwt);
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(13)
    public void deleteAdminSuccess() throws Exception {
        // customer tries to delete their own account - ok
        RequestBuilder request = MockMvcRequestBuilders.delete("/user/1").header("Authorization", this.customerJwt);
        mockMvc.perform(request).andExpect(status().is2xxSuccessful());
        // assert the only user profile in DB is the admin one now
        request = MockMvcRequestBuilders.get("/user/all").header("Authorization", this.adminJwt);
        mockMvc.perform(request).andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(1)));
    }

}
