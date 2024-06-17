/**
this class is responsible for taking in a request payload and
sending the payload to the specified endpoint concurrently

the main purpose of this class is to allow the testing of the 
transaction concurrency management logic found in the transaction
service layer 
 */

package com.mdp.next.concurrency;

import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.MockMvc;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RequestDispatcher implements Runnable {

    private MockMvc mockMvc;
    private RequestBuilder request;

    // perform the request and print its result to screen
    @Override  
    public void run() {
        try { 

            System.out.println("REQUEST WITH ID OF " + Thread.currentThread().getId());
            mockMvc.perform(request);
            
        }
        catch (Exception e) { e.printStackTrace(); }
    }

}