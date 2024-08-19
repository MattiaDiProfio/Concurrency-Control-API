package com.mdp.next.security.user;

public class UserIntegrationTests {

    // TODO : simulate all requests in the Controller layer, including authorization and access to resources

    /*
     * setup method
     * - register and login 2 different users, 1 admin and 1 customer
     *
     * endpoints to test are :
     *
     * 1. GET /user/all
     * - attempt to send request with customer -> fails due to missing ADMIN role
     * - attempt to send request with admin -> assert list returned is of length 2, and that a customer and admin are present
     *
     * 2. GET /user/userID
     * - attempt to get /user/1 and 2 with ADMIN -> both succeed
     * - attempt to get /user/1 with customer -> fails
     * - attempt to get /user/2 with customer -> success since they own the profile
     *
     * 3. PUT /user/userID
     * - attempt to edit user 1 with customer -> fails
     * - attempt to edit user 2 with admin -> fails
     * - attempt to update email on user 2 with customer -> success since the customer owns this profile
     *
     * 4. DELETE /user/userID
     * - attempt to delete user 1 with customer -> fails
     * - attempt to delete user 2 with admin -> fails
     * - attempt to delete user 1 with admin -> success since it's the admin's profile
     *
     * teardown method
     * - logout the remaining logged-in users, 1 admin
     *
     * */
}
