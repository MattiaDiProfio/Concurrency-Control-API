package com.mdp.next.concurrency;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
public class ConcurrencyManagementTests {
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
    public void testPlaceTransactionOk() throws Exception {

        RequestBuilder request = MockMvcRequestBuilders
			.post("/transaction")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.receiverJWT)
			.content("{ \"amount\": \"10.00\", \"senderID\": \"1\", \"receiverID\": \"2\" }");

		System.out.println("========= 3 TRANSACTIONS AT THE SAME TIME =========");

		// simultaneously instantiate 3 transactions
        for (int i=0; i < 3; i++) {
            Thread thread = new Thread(new RequestDispatcher(mockMvc, request));
            thread.start();
        }

		// imposes a wait so that the logic below does not run before the 3 concurrent transactions above
		TimeUnit.SECONDS.sleep(1);

		System.out.println("======== ACCOUNTS AFTER CONCURRENT TRANSACTIONS =========");

		request = MockMvcRequestBuilders
			.get("/account/all")
			.header("Authorization", this.receiverJWT);

		mockMvc.perform(request).andDo(MockMvcResultHandlers.print());

		System.out.println("========= 1 TRANSACTION =========");

        request = MockMvcRequestBuilders
			.post("/transaction")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.receiverJWT)
			.content("{ \"amount\": \"10.00\", \"senderID\": \"1\", \"receiverID\": \"2\" }");
		mockMvc.perform(request);

		System.out.println("========= FINAL ACCOUNTS =========");

		request = MockMvcRequestBuilders
			.get("/account/all")
			.header("Authorization", this.receiverJWT);

		mockMvc.perform(request).andDo(MockMvcResultHandlers.print());


    }

}
