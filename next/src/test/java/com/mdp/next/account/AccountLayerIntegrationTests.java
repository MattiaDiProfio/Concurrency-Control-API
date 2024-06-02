// package com.mdp.next.account;

// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.RequestBuilder;
// import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
// import static org.hamcrest.Matchers.is;
// import com.mdp.next.service.AccountServiceImpl;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
// import org.springframework.http.MediaType;

// @SpringBootTest
// @AutoConfigureMockMvc
// public class AccountLayerIntegrationTests {
    
//   @Autowired
//   private MockMvc mockMvc; // needed to mock request-response cycle

//   @Autowired      
//   AccountServiceImpl accountService;

//   @Test
//   void contextLoads() {
//     // sanity check to ensure the mockMvc bean was actually injected into the spring container
//     assertNotNull(mockMvc);
//   }

//     @Test
//     public void testGetAccountSuccessfull() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.post("/user/1/account").contentType(MediaType.APPLICATION_JSON).content("{ \"balance\" : \"15.50\" }"));
//     RequestBuilder request = MockMvcRequestBuilders.get("/user/1/account");
//     mockMvc.perform(request).
//       andExpect(status().is2xxSuccessful()).
//             andExpect(jsonPath("$.balance").value(is(15.50)));
//     }

//     @Test       
//     public void testGetAllAccounts() throws Exception {
//     RequestBuilder request = MockMvcRequestBuilders.get("/account/all");
//     mockMvc.perform(request).andExpect(status().is2xxSuccessful()).
//             andExpect(jsonPath("$").isArray());
//     }

//     @Test 
//     public void testGetAccountUnsuccessfull() throws Exception {
//     RequestBuilder request = MockMvcRequestBuilders.get("/user/3/account");
//     mockMvc.perform(request).andExpect(status().is4xxClientError());
//     }

//     @Test
//     public void testOpenAccountSuccessfull() throws Exception {
//     String jsonRequestBody = """
//     { "balance" : "100.00" }
//     """;
//     RequestBuilder request = MockMvcRequestBuilders.post("/user/2/account")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(jsonRequestBody);
//     mockMvc.perform(request).andExpect(status().is2xxSuccessful()).	
//         andExpect(status().isCreated()).
//             andExpect(jsonPath("$.balance").exists()).
//             andExpect(jsonPath("$.balance").value(is(100.00))).
//             andExpect(jsonPath("$.userID").value(is(2)));
//     }

//     @Test 
//     public void testOpenAccountUnsuccessfullDueToUniquenessBreach() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.post("/user/1/account").contentType(MediaType.APPLICATION_JSON).content("{ \"balance\" : \"15.50\" }"));

//     mockMvc.
//             perform(
//                 MockMvcRequestBuilders.post("/user/1/account").
//                 contentType(MediaType.APPLICATION_JSON).
//                 content("{ \"balance\" : \"30.00\" }")
//             ).andExpect(status().is4xxClientError());		
//     }

//     @Test 
//     public void testOpenAccountUnsuccessfullDueToInvalidPayload() throws Exception {
//     String jsonRequestBody = """
//     { "balance" : null }
//     """;
//     RequestBuilder request = MockMvcRequestBuilders.post("/user/2/account")
//             .contentType(MediaType.APPLICATION_JSON)
//             .content(jsonRequestBody);
//     mockMvc.perform(request).andExpect(status().is4xxClientError());		
//     }

//     @Test
//     public void testCloseAccountSuccessfull() throws Exception {
//         mockMvc.perform(MockMvcRequestBuilders.post("/user/1/account").contentType(MediaType.APPLICATION_JSON).content("{ \"balance\" : \"15.50\" }"));
//     RequestBuilder request = MockMvcRequestBuilders.delete("/user/1/account");
//     mockMvc.perform(request).andExpect(status().isNoContent());
//     }

//     @Test 
//     public void testCloseAccountUnsuccessfull() throws Exception {
//     RequestBuilder request = MockMvcRequestBuilders.delete("/user/3/account");
//     mockMvc.perform(request).andExpect(status().is4xxClientError());
//     }

// }
