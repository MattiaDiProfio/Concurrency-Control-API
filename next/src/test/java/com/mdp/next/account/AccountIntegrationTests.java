package com.mdp.next.account;

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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class AccountIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminJwt;
    private String customer1Jwt;
    private String customer2Jwt;

    @BeforeAll
    void setup() throws Exception {
        // register 2x Customer User and an Admin user
        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"customer1\", \"email\": \"customer1@email.com\", \"address\": \"customerStreet\", \"username\": \"customer1\", \"password\": \"customer123\", \"role\": \"CUSTOMER\" }"));

        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"customer2\", \"email\": \"customer2@email.com\", \"address\": \"customerStreet\", \"username\": \"customer2\", \"password\": \"customer123\", \"role\": \"CUSTOMER\" }"));

        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"name\": \"admin\", \"email\": \"admin@email.com\", \"address\": \"adminStreet\", \"username\": \"admin\", \"password\": \"admin123\", \"role\": \"ADMIN\" }"));

        MvcResult mvcResult;
        String customerActionJwt;
        ResultActions loginAction;

        // login both users
        loginAction = this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"customer1\", \"password\": \"customer123\" }"));
        mvcResult = loginAction.andReturn();
        customerActionJwt = mvcResult.getResponse().getContentAsString();
        this.customer1Jwt = "Bearer " + this.objectMapper.readTree(customerActionJwt).get("token").asText();

        loginAction = this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"customer2\", \"password\": \"customer123\" }"));
        mvcResult = loginAction.andReturn();
        customerActionJwt = mvcResult.getResponse().getContentAsString();
        this.customer2Jwt = "Bearer " + this.objectMapper.readTree(customerActionJwt).get("token").asText();

        loginAction = this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/authenticate")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{ \"username\": \"admin\", \"password\": \"admin123\" }"));
        mvcResult = loginAction.andReturn();
        String adminActionJwt = mvcResult.getResponse().getContentAsString();
        this.adminJwt = "Bearer " + this.objectMapper.readTree(adminActionJwt).get("token").asText();

        // open a SAVINGS account for customer 1, an INVESTMENT account for customer 1 a CHECKING account for customer 2
        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", this.customer1Jwt)
            .content("{ \"type\": \"SAVINGS\" }"));
        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/1/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", this.customer1Jwt)
            .content("{ \"type\": \"INVESTMENT\" }"));
        this.mockMvc
            .perform(MockMvcRequestBuilders
            .post("/user/2/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", this.customer2Jwt)
            .content("{ \"type\": \"CHECKING\" }"));
    }

    @Test
    @Order(1)
    public void adminOpenAccountFailure() throws Exception {
        // admin tries to open their own account, but fail since admins cannot have an account
        RequestBuilder request = MockMvcRequestBuilders
                .post("/user/3/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.adminJwt)
                .content("{ \"type\": \"CHECKING\" }");
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    public void customerOpenAccountFailureDueToDuplicate() throws Exception {
        // customer1 already has an account of type SAVINGS, so this test is bound to fail
        RequestBuilder request = MockMvcRequestBuilders
                .post("/user/1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.customer1Jwt)
                .content("{ \"type\": \"SAVINGS\" }");
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    public void adminCloseAccountFailureDueToNoAuth() throws Exception {
        // admin cannot close a customer's account
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/user/1/accounts/1")
                .header("Authorization", this.adminJwt);
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(4)
    public void adminViewsAllAccountsOk() throws Exception {
        // an admin should be able to view all accounts in the 'accounts' DB table
        RequestBuilder request = MockMvcRequestBuilders.get("/account/all").header("Authorization", this.adminJwt);
        mockMvc.perform(request).andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @Order(5)
    public void adminViewsAllAccountsPerCustomerOk() throws Exception {
        // an admin should be able to view all users managed by any given customer
        RequestBuilder request = MockMvcRequestBuilders.get("/user/1/accounts").header("Authorization", this.adminJwt);
        mockMvc.perform(request).andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @Order(6)
    public void adminViewsInvalidAccountPerCustomerFailure() throws Exception {
        // an admin tries to view all accounts managed by a user which does not exist
        RequestBuilder request = MockMvcRequestBuilders.get("/user/100/accounts").header("Authorization", this.adminJwt);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

    @Test
    @Order(7)
    public void customerViewsTheirAccountOk() throws Exception {
        // a customer can view the details of a specific account they manage
        RequestBuilder request = MockMvcRequestBuilders.get("/user/1/accounts/1").header("Authorization", this.customer1Jwt);
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    @Order(8)
    public void customerViewsAllTheirAccountsOk() throws Exception {
        // a customer can view all of their accounts
        RequestBuilder request = MockMvcRequestBuilders.get("/user/1/accounts").header("Authorization", this.customer1Jwt);
        mockMvc.perform(request).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @Order(9)
    public void customerTriesToViewOtherAccountsFailure() throws Exception {
        // a customer cannot view anything related to other customers, including their accounts
        RequestBuilder request = MockMvcRequestBuilders.get("/user/2/accounts").header("Authorization", this.customer1Jwt);
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(10)
    public void customerCloseTheirAccountOk() throws Exception {
        // a customer should be able to close any of their accounts
        RequestBuilder request = MockMvcRequestBuilders.delete("/user/1/accounts/1").header("Authorization", this.customer1Jwt);
        mockMvc.perform(request).andExpect(status().isNoContent());
        // ensure the account has been closed!
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/user/1/accounts/1").header("Authorization", this.customer1Jwt)
        ).andExpect(status().isNotFound());
    }

    @Test
    @Order(11)
    public void customerCloseTheirAccountFailureDueToNoAccount() throws Exception {
        // a customer tries to close an account which they do not have under management hence it fails
        RequestBuilder request = MockMvcRequestBuilders.delete("/user/1/accounts/1").header("Authorization", this.customer1Jwt);
        mockMvc.perform(request).andExpect(status().isNotFound());
    }

}