package com.mdp.next.transaction;

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
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class TransactionIntegrationTests {
    
    @Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private String senderJWT;
    private String receiverJWT;

    @BeforeAll
	void setupAccounts() throws Exception {

        // register and login the 1st user - mattia 
		this.mockMvc
			.perform(MockMvcRequestBuilders
				.post("/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"mattia di profio\", \"email\": \"mattia@email.com\", \"address\": \"123 Any Place\", \"username\": \"mattia\", \"password\": \"password123\", \"role\": \"ADMIN\" }"));

		ResultActions resultActions = this.mockMvc
			.perform(MockMvcRequestBuilders
				.post("/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"username\": \"mattia\", \"password\": \"password123\" }"));

		MvcResult mvcResult = resultActions.andReturn();
		String contentAsString = mvcResult.getResponse().getContentAsString();
		this.senderJWT = "Bearer " + this.objectMapper.readTree(contentAsString).get("token").asText();

        // open an account with £100.00 for Mattia
        this.mockMvc
			.perform(MockMvcRequestBuilders
				.post("/user/1/account")
				.contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.senderJWT)
				.content("{ \"balance\": \"100.00\" }"));

        // register and login the 2nd user - nicolo
        this.mockMvc
			.perform(MockMvcRequestBuilders
				.post("/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"nicolo di profio\", \"email\": \"nicolo@email.com\", \"address\": \"123 Any Place\", \"username\": \"nicolo\", \"password\": \"password123\", \"role\": \"ADMIN\" }"));

		resultActions = this.mockMvc
			.perform(MockMvcRequestBuilders
				.post("/authenticate")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"username\": \"nicolo\", \"password\": \"password123\" }"));

		mvcResult = resultActions.andReturn();
		contentAsString = mvcResult.getResponse().getContentAsString();
		this.receiverJWT = "Bearer " + this.objectMapper.readTree(contentAsString).get("token").asText();

        // open and account with £0.00 for Nicolo
        this.mockMvc
			.perform(MockMvcRequestBuilders
				.post("/user/2/account")
				.contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", this.receiverJWT)
				.content("{ \"balance\": \"0.00\" }"));
	}

    @AfterAll
	void logout() throws Exception {
		this.senderJWT = null;
        this.receiverJWT = null;
	}


    @Test
    @Order(1) 
    public void testPlaceTransactionOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
			.post("/transaction")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.receiverJWT)
			.content("{ \"amount\": \"10.00\", \"senderID\": \"1\", \"receiverID\": \"2\" }");
		
		mockMvc
			.perform(request)
            .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$.amount").value(10.00))
            .andExpect(jsonPath("$.sender.balance").value(90.00))
            .andExpect(jsonPath("$.receiver.balance").value(10.00))
            .andExpect(jsonPath("$.receiverID").value(2L))
            .andExpect(jsonPath("$.senderID").value(1L));
    }

    @Test
    @Order(2)
    public void testPlaceTransactionFailureDueToInsufficientFunds() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
			.post("/transaction")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.senderJWT)
			.content("{ \"amount\": \"12345.00\", \"senderID\": \"1\", \"receiverID\": \"2\" }");
		
		mockMvc
			.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", hasItem("The account with id '1' does not have enough assets to cover the transaction")));
    }

    @Test
    @Order(3)
    public void testPlaceTransactionFailureDueToInvalidPayload() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
			.post("/transaction")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.senderJWT)
			.content("{ \"amount\": \"0.00\", \"senderID\": \"1\", \"receiverID\": \"10\" }");
		
		mockMvc
			.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", hasItem("The transaction must have an amount greater than £0.00.")));
    }

    @Test
    @Order(4)
    public void testPlaceTransactionFailureDueToEqualAccounts() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
			.post("/transaction")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.senderJWT)
			.content("{ \"amount\": \"10.00\", \"senderID\": \"1\", \"receiverID\": \"1\" }");
		
		mockMvc
			.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", hasItem("Sender account and Receiver account cannot be the same")));
    }


    @Test
    @Order(5)
    public void testGetAllTransactions() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/transaction/all").header("Authorization", this.senderJWT);
		mockMvc.perform(request)
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$").value(instanceOf(List.class)))
			.andExpect(jsonPath("$[0].amount").value(10.00))
			.andExpect(jsonPath("$[0].receiverID").value(2L))
			.andExpect(jsonPath("$[0].senderID").value(1L));
    }

    @Test
    @Order(6)
    public void testGetAccountSentTransactionsOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/1/account/transaction/sent").header("Authorization", this.senderJWT);

		mockMvc.perform(request)
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$").value(instanceOf(List.class)))
			.andExpect(jsonPath("$[0].amount").value(10.00))
			.andExpect(jsonPath("$[0].receiverID").value(2L))
			.andExpect(jsonPath("$[0].senderID").value(1L));
    }

    @Test
    @Order(7)
    public void testGetAccountSentTransactionsFailureDueToInvalidUser() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/123/account/transaction/sent").header("Authorization", this.senderJWT);
		mockMvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", hasItem("The user with id '123' does not exist in our records")));
    }

    @Test
    @Order(8)
    public void testGetAccountSentTransactionsFailureDueToInexistentAccount() throws Exception {

        // john is our 3rd user, which does not have an account yet
        this.mockMvc
			.perform(MockMvcRequestBuilders
				.post("/user/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ \"name\": \"john doe\", \"email\": \"john@email.com\", \"address\": \"123 Any Place\", \"username\": \"john\", \"password\": \"password123\", \"role\": \"BASE\" }"));

        RequestBuilder request = MockMvcRequestBuilders.get("/user/3/account/transaction/sent").header("Authorization", this.senderJWT);
		mockMvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", hasItem("The user with id '3' does not have an active account")));
    }

    @Test
    @Order(9)
    public void testGetAccountReceivedTransactionsOk() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/2/account/transaction/received").header("Authorization", this.senderJWT);
		mockMvc.perform(request)
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$").value(instanceOf(List.class)))
			.andExpect(jsonPath("$[0].amount").value(10.00))
			.andExpect(jsonPath("$[0].receiverID").value(2L))
			.andExpect(jsonPath("$[0].senderID").value(1L));
    }

    @Test
    @Order(10)
    public void testGetAccountReceivedTransactionsFailureDueToInvalidUser() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.get("/user/123/account/transaction/received").header("Authorization", this.senderJWT);
		mockMvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", hasItem("The user with id '123' does not exist in our records")));
    }

    @Test
    @Order(11)
    public void testGetAccountReceivedTransactionsFailureDueToInexistentAccount() throws Exception {
        // john is our 3rd user, which does not have an account yet, and it has been created in test 8
        RequestBuilder request = MockMvcRequestBuilders.get("/user/3/account/transaction/received").header("Authorization", this.senderJWT);
		mockMvc.perform(request)
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.message", hasItem("The user with id '3' does not have an active account")));
    }

}
