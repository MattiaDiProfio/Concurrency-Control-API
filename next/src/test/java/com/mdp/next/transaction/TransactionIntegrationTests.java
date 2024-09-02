package com.mdp.next.transaction;

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
public class TransactionIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String Ajwt;
    private String Bjwt;
    private String Cjwt;
    private String Djwt;

    @BeforeAll
    void setup() throws Exception {

        // register an admin and 3 customers
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON).content("{ \"name\": \"A\", \"email\": \"A@email.com\", \"address\": \"customerStreet\", \"username\": \"A\", \"password\": \"password123\", \"role\": \"ADMIN\" }"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON).content("{ \"name\": \"B\", \"email\": \"B@email.com\", \"address\": \"customerStreet\", \"username\": \"B\", \"password\": \"password123\", \"role\": \"CUSTOMER\" }"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON).content("{ \"name\": \"C\", \"email\": \"C@email.com\", \"address\": \"adminStreet\", \"username\": \"C\", \"password\": \"password123\", \"role\": \"CUSTOMER\" }"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/register").contentType(MediaType.APPLICATION_JSON).content("{ \"name\": \"D\", \"email\": \"D@email.com\", \"address\": \"adminStreet\", \"username\": \"D\", \"password\": \"password123\", \"role\": \"CUSTOMER\" }"));

        // login the admin, and the 3 customers
        MvcResult mvcResult;
        String customerActionJwt;
        ResultActions loginAction;

        loginAction = this.mockMvc.perform(MockMvcRequestBuilders.post("/authenticate").contentType(MediaType.APPLICATION_JSON).content("{ \"username\": \"A\", \"password\": \"password123\" }"));
        mvcResult = loginAction.andReturn();
        customerActionJwt = mvcResult.getResponse().getContentAsString();
        this.Ajwt = "Bearer " + this.objectMapper.readTree(customerActionJwt).get("token").asText();

        loginAction = this.mockMvc.perform(MockMvcRequestBuilders.post("/authenticate").contentType(MediaType.APPLICATION_JSON).content("{ \"username\": \"B\", \"password\": \"password123\" }"));
        mvcResult = loginAction.andReturn();
        customerActionJwt = mvcResult.getResponse().getContentAsString();
        this.Bjwt = "Bearer " + this.objectMapper.readTree(customerActionJwt).get("token").asText();

        loginAction = this.mockMvc.perform(MockMvcRequestBuilders.post("/authenticate").contentType(MediaType.APPLICATION_JSON).content("{ \"username\": \"C\", \"password\": \"password123\" }"));
        mvcResult = loginAction.andReturn();
        customerActionJwt = mvcResult.getResponse().getContentAsString();
        this.Cjwt = "Bearer " + this.objectMapper.readTree(customerActionJwt).get("token").asText();

        loginAction = this.mockMvc.perform(MockMvcRequestBuilders.post("/authenticate").contentType(MediaType.APPLICATION_JSON).content("{ \"username\": \"D\", \"password\": \"password123\" }"));
        mvcResult = loginAction.andReturn();
        customerActionJwt = mvcResult.getResponse().getContentAsString();
        this.Djwt = "Bearer " + this.objectMapper.readTree(customerActionJwt).get("token").asText();


        // open an account per customer profile (3 in total)
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/2/accounts").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"type\": \"SAVINGS\" }"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/3/accounts").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Cjwt).content("{ \"type\": \"SAVINGS\" }"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/4/accounts").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Djwt).content("{ \"type\": \"SAVINGS\" }"));

        // populate the DB with some test transactions
        // b -> c (10)
        // b -> d (10)
        // d -> c (10)
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/2/accounts/1/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"2\" }"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/2/accounts/1/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"3\" }"));
        this.mockMvc.perform(MockMvcRequestBuilders.post("/user/4/accounts/3/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Djwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"2\" }"));

    }

    @Test
    @Order(1)
    public void adminViewsAllTransactionsOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/transaction/all").header("Authorization", this.Ajwt);
        mockMvc.perform(request).andExpect(status().is2xxSuccessful()).andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    @Order(2)
    public void AFailsToViewBsSentTransactions() throws Exception {
        // a customer cannot view anything related to other customers, including their accounts
        RequestBuilder request = MockMvcRequestBuilders.get("/user/3/accounts/2/sent").header("Authorization", this.Bjwt);
        mockMvc.perform(request).andExpect(status().isForbidden());
    }

    @Test
    @Order(3)
    public void BViewsItsReceivedTransactionsOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/2/accounts/1/received").header("Authorization", this.Bjwt);
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    @Order(4)
    public void AdminViewsAllCsSentTransactionsOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/3/accounts/2/sent").header("Authorization", this.Ajwt);
        mockMvc.perform(request).andExpect(status().isOk());
    }

    @Test
    @Order(5)
    public void placeTransactionFailDueToInsufficientFunds() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/user/2/accounts/1/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"150.0\",  \"receiverAccountId\": \"2\" }");
        mockMvc.perform(request).andExpect(status().isBadRequest()).andExpect(result -> result.getResolvedException().getMessage().equals("Insufficient balance to place transaction"));
    }

    @Test
    @Order(6)
    public void placeTransactionFailDueToSameAccounts() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/user/2/accounts/1/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"1\" }");
        mockMvc.perform(request).andExpect(status().isBadRequest()).andExpect(result -> result.getResolvedException().getMessage().equals("Sender and Receiver accounts must be different"));;
    }

    @Test
    @Order(7)
    public void placeTransactionFailDueToNegativeAmount() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/user/2/accounts/1/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"-0.50\",  \"receiverAccountId\": \"2\" }");
        mockMvc.perform(request).andExpect(status().isBadRequest()).andExpect(result -> result.getResolvedException().getMessage().equals("Amount cannot be less than 0.01"));;
    }


    /*
    * setup
    * - 3 users, 1 admin and 3 customers
    * - open an account per customer, and place a transaction of 5 from a -> b and one of 10 from b -> a, and 20 from c -> a
    *
    * scenarios to test
    * === authorization tests ===
    * 1. the admin tries to access all the transactions - success
    * 2. user a tries to access all of b's sent transactions - fails
    * 3. user b tries to access all of b's received transactions - success
    * 4. the admin tries to access all of c's sent transactions - success
    *
    * === place transaction tests (mainly validation) ===
    * - transaction amount too large for sender
    * - sender and receiver are equal
    * - transaction amount is <= 0
    *
    * === occ algorithm tests === we assert that the total amount of money in the DB is the same before n after
    * 1. scenario 1 (no failure) - a sends 1.0 => b, a sends 1.0 => c concurrently. after these two, c sends 10.0 => b
    * 2. scenario 2 (failure due to dirty read) - a sends 1.0 => b, b sends 2.0 => c
    * 3. scenario 3 (failure due to lost update) - a sends 1.0 => b and c sends 1.0 => b
    * */

}
