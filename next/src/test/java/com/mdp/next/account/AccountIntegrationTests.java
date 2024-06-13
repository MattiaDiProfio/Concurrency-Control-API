package com.mdp.next.account;

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
public class AccountIntegrationTests {
    
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

		// extract the token from the response object and store it to be passed into subsequent unit tests
		this.jwtToken = "Bearer " + this.objectMapper.readTree(contentAsString).get("token").asText();
	}

    @AfterAll
	void logout() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/logout"));
	}


	@Test
	@Order(1)
	public void testGetAllAccounts() throws Exception {

	}


    @Test
    @Order(2)
    public void testGetAccountByUserSuccess() throws Exception {

    }


    @Test
    @Order(3) 
    public void testGetAccountByUserFailureDueToNoAccount() throws Exception {
        // test fails because user is present in the database, but there is no
        // account available to be fetched from them
    }

    @Test
    @Order(4)
    public void testOpenAccountSuccess() throws Exception {

    }


    @Test
    @Order(5)
    public void testOpenAccountFailureDueToRepetition() throws Exception {

    }

    @Test
    @Order(6)
    public void testOpenAccountFailureDueToInvalidUser() throws Exception {
        // test fails because user specified does not exist, so an account cannot be associated to it 
    }


    @Test
    @Order(7) 
    public void testCloseAccountSuccess() throws Exception {

    }


    @Test
    @Order(8) 
    public void testCloseAccountFailureDueToNoAccount() throws Exception {
        // test fails because user does not have an account to be closed
    }


    @Test
    @Order(9) 
    public void testCloseAccountFailureDueToInvalidUser() throws Exception {
        // test fails because there is no user with the given ID in the database
    }


}
