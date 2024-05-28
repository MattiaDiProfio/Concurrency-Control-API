package com.mdp.next;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.hamcrest.Matchers.hasSize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
class NextApplicationTests {

	@Autowired
	private MockMvc mockMvc; // needed to mock request-response cycle

	@Test
	void contextLoads() {
		// sanity check to ensure the mockMvc bean was actually injected into the spring container
		assertNotNull(mockMvc);
	}

	@Test
	public void testGetUserSuccessfull() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/1");
		mockMvc.perform(request).
			andExpect(status().is2xxSuccessful()).
			andExpect(jsonPath("$.id").exists()).
			andExpect(jsonPath("$.name").exists()).
			andExpect(jsonPath("$.address").exists()).
			andExpect(jsonPath("$.email").exists());
	}

	@Test
	public void testGetUserUnsuccessfull() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/5g3Ji8s");
		mockMvc.perform(request).andExpect(status().is4xxClientError());
	}

	@Test
	public void testGetAllUsers() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.get("/user/all");
		mockMvc.perform(request)
		    .andExpect(status().is2xxSuccessful())
            .andExpect(jsonPath("$", hasSize(3))); // since 3 objects are written to the h2 database anytime the application starts
	}

	@Test
	public void testCreateUserSuccesfull() throws Exception {
		// NOTE for the future - using single quotes in the JSON payload will cause an error! Use double quotes "" instead
		String jsonRequestBody = """
			{ "id" : "1", "account" : "null", "name": "Mattia Di Profio", "email": "mattiadiprofio@email.com", "address": "123 Random Avenue" }	
		""";

		RequestBuilder request = MockMvcRequestBuilders.post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody);

		mockMvc.perform(request).
			andExpect(status().is2xxSuccessful()).
			andExpect(jsonPath("$.id").exists()).
			andExpect(jsonPath("$.name").exists()).
			andExpect(jsonPath("$.address").exists()).
			andExpect(jsonPath("$.email").exists());
	}

	@Test 
	public void testCreateUserUnsuccessful() throws Exception {
		String jsonRequestBody = """
		{ "name" : "   ", email": "    ", "address": "   " }
		""";

		RequestBuilder request = MockMvcRequestBuilders.post("/user")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody);

		mockMvc.perform(request).
			andExpect(status().is4xxClientError());		
	}

	@Test
	public void testUpdateUserSuccessful() throws Exception {
		String jsonRequestBody = """
			{ "name": "Mattia Di Profio", "email": "mattiadiprofio@email.com", "address": "123 Random Avenue" }
		""";

		RequestBuilder request = MockMvcRequestBuilders.put("/user/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody);

		mockMvc.perform(request).
			andExpect(status().is2xxSuccessful()).
			andExpect(jsonPath("$.id").exists()).
			andExpect(jsonPath("$.name").exists()).
			andExpect(jsonPath("$.address").exists()).
			andExpect(jsonPath("$.email").exists());
	}

	@Test
	public void testUpdateUserUnsuccessful() throws Exception {
		String jsonRequestBody = """
			{ "email": "mattiadiprofioemail..com", "address": "   " }
		""";

		RequestBuilder request = MockMvcRequestBuilders.put("/user/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody);

		mockMvc.perform(request).
			andExpect(status().is4xxClientError());
	}

	@Test
	public void testDeleteUserSuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/user/1");
		mockMvc.perform(request).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testDeleteUserUnsuccessful() throws Exception {
		RequestBuilder request = MockMvcRequestBuilders.delete("/user/5g3Ji8s");
		mockMvc.perform(request).andExpect(status().is4xxClientError());
	}

}
