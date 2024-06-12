package com.mdp.next;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
class UserLayerIntegrationTests {

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
	public void testGetAllUsers() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/all").header("Authorization", this.jwtToken);
		mockMvc
			.perform(request)
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$").value(instanceOf(List.class)));
	}


	@Test
	@Order(2)
	public void testGetUserOk() throws Exception {
		// since the ID field on the User object is automatically incremented upon registration,
		// we expect the user with ID of 1 to be found in the database
		RequestBuilder request = MockMvcRequestBuilders.get("/user/1").header("Authorization", this.jwtToken);
		mockMvc
			.perform(request)
			.andExpect(status().is2xxSuccessful())
			.andExpect(jsonPath("$.name").value("mattia di profio"))
			.andExpect(jsonPath("$.email").value("mattia@email.com"))
			.andExpect(jsonPath("$.address").value("123 Any Place"))
			.andExpect(jsonPath("$.username").value("mattia"));
	}


	@Test
	@Order(3)
	public void testGetUserFailure() throws Exception {
		// the action fails because the user with the specified ID is not found in the database
		RequestBuilder request = MockMvcRequestBuilders.get("/user/123").header("Authorization", this.jwtToken);
		mockMvc
			.perform(request)
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message").value("The user with id '123' does not exist in our records"));
	}


	@Test
	@Order(4)
	public void testRegisterUserOk() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.post("/user/register")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.jwtToken)
			.content("{ \"name\": \"john doe\", \"email\": \"johndoe@email.com\", \"address\": \"32 Random Avenue\", \"username\": \"john123\", \"password\": \"password123\", \"role\": \"ADMIN\" }");
		
		mockMvc
			.perform(request)
			.andExpect(status().is2xxSuccessful());
	}


	@Test
	@Order(5)
	public void testRegisterUserFailureDueToRepetition() throws Exception {
		// request payload is valid but request is rejected due to user with given
		// credentials already existing in the database
		RequestBuilder request = MockMvcRequestBuilders
			.post("/user/register")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.jwtToken)
			.content("{ \"name\": \"mattia di profio\", \"email\": \"mattia@email.com\", \"address\": \"123 Any Place\", \"username\": \"mattia\", \"password\": \"password123\", \"role\": \"ADMIN\" }");
		
		mockMvc
			.perform(request)
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message").value("Data Integrity Violation: we cannot process your request."));
	}


	@Test
	@Order(6)
	public void testRegisterUserFailureDueToInvalidPayload() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.post("/user/register")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.jwtToken)
			.content("{ \"name\": \"john doe\", \"email\": \"johndoe@email.com\", \"address\": \"32 Random Avenue\", \"username\": \" \", \"password\": \" \", \"role\": \"ADMIN\" }");

		// the username and password are blank in this payload, since the BindingResult passed to the controller is negative, the exception messages
		// below will be expected
		mockMvc
			.perform(request)
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message", hasSize(2)))
			.andExpect(jsonPath("$.message", hasItem("username cannot be blank")))
			.andExpect(jsonPath("$.message", hasItem("password cannot be blank")));
	}


	@Test
	@Order(7)
	public void testEditUserOk() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.put("/user/1")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.jwtToken)
			.content("{ \"email\": \"mdp@gmail.com\", \"address\": \"120 Garden Road GK8 2HS\" }");

		mockMvc.perform(request).andExpect(status().is2xxSuccessful());
		mockMvc
			.perform(MockMvcRequestBuilders.get("/user/1").header("Authorization", this.jwtToken))
			.andExpect(jsonPath("$.email").value("mdp@gmail.com"))
			.andExpect(jsonPath("$.address").value("120 Garden Road GK8 2HS"));
	}


	@Test
	@Order(8)
	public void testEditUserFailureDueToInvalidPayload() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders
			.put("/user/1")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.jwtToken)
			.content("{ \"email\": \" \", \"address\": \" \" }");

		mockMvc
			.perform(request)
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message", hasItem("address cannot be blank")))
			.andExpect(jsonPath("$.message", hasItem("email must follow a valid email format")));
    }


	@Test
	@Order(9)
	public void testEditUserFailureDueToInexistentUser() throws Exception {
		// request is rejected due to invalid user ID passed in the request parameter
		RequestBuilder request = MockMvcRequestBuilders
			.put("/user/123")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.jwtToken)
			.content("{ \"email\": \"mdp@gmail.com\", \"address\": \"120 Garden Road GK8 2HS\" }");

		mockMvc
			.perform(request)
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message").value("The user with id '123' does not exist in our records"));
	}


	@Test
	@Order(10)
	public void testDeleteUserOk() throws Exception {
		// create and then delete an additional user
		RequestBuilder request = MockMvcRequestBuilders
			.post("/user/register")
			.contentType(MediaType.APPLICATION_JSON)
			.header("Authorization", this.jwtToken)
			.content("{ \"name\": \"john doe\", \"email\": \"johndoe@email.com\", \"address\": \"32 Random Avenue\", \"username\": \"john123\", \"password\": \"password123\", \"role\": \"ADMIN\" }");
		mockMvc.perform(request);

		request = MockMvcRequestBuilders.delete("/user/2").header("Authorization", this.jwtToken);
		mockMvc
			.perform(request)
			.andExpect(status().is2xxSuccessful());

		mockMvc
			.perform(MockMvcRequestBuilders.get("/user/2").header("Authorization", this.jwtToken))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message").value("The user with id '2' does not exist in our records"));
	}


	@Test
	@Order(11)
	public void testDeleteUserFailureDueToInexistentUser() throws Exception {
		// request fails because there is no user with the given ID in the database
		RequestBuilder request = MockMvcRequestBuilders.delete("/user/123").header("Authorization", this.jwtToken);
		mockMvc
			.perform(request)
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.message").value("The user with id '123' does not exist in our records"));
	}


}