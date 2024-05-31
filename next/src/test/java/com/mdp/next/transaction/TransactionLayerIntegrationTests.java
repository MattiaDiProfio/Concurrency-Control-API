package com.mdp.next.transaction;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;
import static org.hamcrest.Matchers.hasSize;
import com.mdp.next.service.TransactionServiceImpl;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionLayerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    TransactionServiceImpl transactionService;

    @BeforeAll
    public void setUpTestSuite() throws Exception {
        // create two accounts 
        mockMvc.perform(MockMvcRequestBuilders.post("/user/1/account").contentType(MediaType.APPLICATION_JSON).content("{ \"balance\" : \"50.00\" }"));
        mockMvc.perform(MockMvcRequestBuilders.post("/user/2/account").contentType(MediaType.APPLICATION_JSON).content("{ \"balance\" : \"50.00\" }"));

        // place two transactions between the two accounts
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{ \"amount\": \"5.00\", \"senderID\": \"1\", \"receiverID\": \"2\" }"));
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{ \"amount\": \"8.00\", \"senderID\": \"1\", \"receiverID\": \"2\" }"));
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{ \"amount\": \"7.00\", \"senderID\": \"2\", \"receiverID\": \"1\" }"));
    }

    @Test
    public void contextLoads() {
        assertNotNull(mockMvc);
    }

    @Test
    public void testGetAllTransactions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/transaction/all");
		mockMvc.perform(request)
		    .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(3)));
    }

    @Test
    public void testGetAccountSentTransactions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/1/account/transaction/sent");
		mockMvc.perform(request)
		    .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test 
    public void testGetAccountReceivedTransactions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/2/account/transaction/received");
		mockMvc.perform(request)
		    .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testPlaceTransactionSuccessfull() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{ \"amount\": \"8.00\", \"senderID\": \"1\", \"receiverID\": \"2\" }");
        mockMvc.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.amount").value(is(8.00)))
            .andExpect(jsonPath("$.senderID").value(is(1)))
            .andExpect(jsonPath("$.receiverID").value(is(2)));
    }

    @Test 
    public void testPlaceTransactionFailDueToEqualAccounts() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{ \"amount\": \"8.00\", \"senderID\": \"2\", \"receiverID\": \"2\" }");
        mockMvc.perform(request).andExpect(status().is4xxClientError());
    }

    @Test
    public void testPlaceTransactionFailDueToInsufficientAssets() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{ \"amount\": \"500.00\", \"senderID\": \"2\", \"receiverID\": \"2\" }");
        mockMvc.perform(request).andExpect(status().is4xxClientError());
    }

    @Test
    public void testPlaceTransactionFailDueToNegativeAmount() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{ \"amount\": \"-10.00\", \"senderID\": \"2\", \"receiverID\": \"2\" }");
        mockMvc.perform(request).andExpect(status().is4xxClientError());
    }

    // @Test
    // public void testDeleteTransactionSuccessfull() throws Exception {
    //     RequestBuilder request = MockMvcRequestBuilders.delete("/transaction/1");
    //     mockMvc.perform(request).andExpect(status().is2xxSuccessful());
    // }

    // @Test
    // public void testDeleteTransactionUnsuccessfull() throws Exception {
    //     RequestBuilder request = MockMvcRequestBuilders.delete("/transaction/100");
    //     mockMvc.perform(request).andExpect(status().is4xxClientError());
    // }

    @Test
    public void testAbortTransactionSuccessfull() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.delete("/transaction/1/abort");
        mockMvc.perform(request).andExpect(status().is2xxSuccessful());
    }

    @Test
    public void testAbortTransactionFailDueToClosedAccount() throws Exception {
        // close account 1
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/1/account"));

        RequestBuilder request = MockMvcRequestBuilders.delete("/transaction/1/abort");
        mockMvc.perform(request).andExpect(status().is4xxClientError());
    }

    @Test 
    public void testAbortTransactionFailDueToInsufficientAssets() throws Exception {
        // empty account 2's funds by performing a transaction from account 1 to 2
        mockMvc.perform(MockMvcRequestBuilders.post("/transaction").contentType(MediaType.APPLICATION_JSON).content("{ \"amount\": \"600.00\", \"senderID\": \"1\", \"receiverID\": \"2\" }"));
        
        // now the receiver of transaction to be aborted cannot "refund" the sender account
        RequestBuilder request = MockMvcRequestBuilders.delete("/transaction/1/abort");
        mockMvc.perform(request).andExpect(status().is4xxClientError());
    }

}
