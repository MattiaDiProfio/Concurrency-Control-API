package com.mdp.next.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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

    @Test
    @Order(8)
    // we try and simulate OCC dirty-read and lost-update and check that the OCC algorithm handles them properly
    public void testPlaceTransactionOk() throws Exception {

        // starting state
        assertEquals(300.00, getTotal());

        List<RequestBuilder> requests = List.of(
            // b -> c -> d : dirty-read
            MockMvcRequestBuilders.post("/user/2/accounts/1/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"3\" }"),
            MockMvcRequestBuilders.post("/user/3/accounts/2/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"4\" }"),
            // b -> c <- d : lost-update
            MockMvcRequestBuilders.post("/user/2/accounts/1/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"3\" }"),
            MockMvcRequestBuilders.post("/user/4/accounts/3/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Bjwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"3\" }")
        );

        // simultaneously place all transactions
        CountDownLatch latch = new CountDownLatch(1);
        for (RequestBuilder request : requests) {
            Thread thread = new Thread(() -> {
                try {
                    latch.await(); // Wait for the latch to reach 0
                    mockMvc.perform(request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        latch.countDown();

        // after concurrent transactions
        assertEquals(300.00, getTotal());

        mockMvc.perform(MockMvcRequestBuilders.post("/user/4/accounts/3/place").contentType(MediaType.APPLICATION_JSON).header("Authorization", this.Djwt).content("{ \"amount\": \"10.0\",  \"receiverAccountId\": \"2\" }"));
        // after 1 independent transaction

        assertEquals(300.00, getTotal());

    }


    public double getTotal() throws Exception {
        double total = 0;

        MvcResult result;

        result = mockMvc.perform(MockMvcRequestBuilders.get("/user/2/accounts/1").header("Authorization", this.Ajwt)).andExpect(status().isOk()).andReturn();
        total += objectMapper.readTree(result.getResponse().getContentAsString()).get("balance").asDouble();

        result = mockMvc.perform(MockMvcRequestBuilders.get("/user/3/accounts/2").header("Authorization", this.Ajwt)).andExpect(status().isOk()).andReturn();;
        total += objectMapper.readTree(result.getResponse().getContentAsString()).get("balance").asDouble();

        result = mockMvc.perform(MockMvcRequestBuilders.get("/user/4/accounts/3").header("Authorization", this.Ajwt)).andExpect(status().isOk()).andReturn();;
        total += objectMapper.readTree(result.getResponse().getContentAsString()).get("balance").asDouble();

        return total;
    }


}
