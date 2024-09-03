# OCC API 
This project consists of a RESTful API built using the Spring framework. Through the endpoints listed below, users can manage their profiles and accounts, as well as send and receive money through transactions. The main focus of this project has been implementing a robust JWT-based authentication & authorization mechansim and ensuring high concurrency by checking for validity of transactions with the OCC algorithm provided.

## How to Run the API
Once you have cloned the repo, travel to the /next folder in your terminal of choice and run the command `mvn clean spring-boot:run`. Please wait between 10-30 seconds for the API to start-up. 
You can interact with the API via Postman. I have provided the json file for all User, Account, and Transaction requests, so that you can import them in your workspace without having to set them up from scratch. First of all, you'll need to register and then login as a user, then you will be able to open/close accounts and place transactions. If you encounter any bug, or would like to suggest an improvement, feel free to create a PR :)

## API Architecture & Components Overview

#### Database Schema
![](next/markdown/media/db.png)

#### Model-View-Controller Architecture
The architecture chosen for this project is a standard 3-tier MVC, with an implict cross-cutting security layer. Within this setup, the user-facing layer (View) is responsible for receiving requests and validates the authority of the requesting user by invoking the Authorization Filter. Once authorized, the request is delegated to the Service layer, where the Controller interacts with the Model to perform the requested actions on the database. 

![](next/markdown/media/mvc.png)

#### Security Architecture
The Securirty Layer has been designed around JSON-Web-Token (JWT) authentication, and role-based authrorization. Every request apart for the registration & login are protected, and once logged in a user must possess the required level of authority to access certain resources. For example, admins can GET all resources but cannot edit or delete a resource which they do not own.

![](next/markdown/media/security.png)

#### Transaction Entity

To allow for the Optimistic-Concurrency-Control algorithm to operate properly, Transactions must provide a mix of user-facing data, such as amount, sender & receiver, as well as a set of attributes used during the validation phase, such as timestamps of when the transaction entered/exited each phase, working sets, and the validationID required to compute the Relevant Set during the Validation Phase.
The Transaction object is divided into three phases : 
- Working/Read Phase : the transaction instantiates local copies of objects it writes to, and defines its Read & Write sets. Additionally, the transaction performs the desired action on its local copies.
- Validation Phase : the transaction is validated against any other transaction which may cause concurrency issues, such as *lost-updates* or *dirty-reads*. Refer to the OCC Algorithm diagram below.
- Commit/Abort Phase : depending on the outcome of the previous phase, the transaction either makes its changes persistent or rolls-back to the previous state

![](next/markdown/media/transaction.png)

#### Optimistic Concurrency Control Algorithm
![](next/markdown/media/algorithm.png)

## API Endpoints Overview

Open endpoints require no Authentication.

* [Register](next/markdown/auth/register.md) : `POST /register`
* [Login](next/markdown/auth/login.md) : `POST /authenticate`

Protected endpoints require a valid token to be included in the request header under the 'Authorization' key.
This token can be obtained by logging-in on the `/authenticate` route.

* [Logout](next/markdown/auth/logout.md) : `POST /logout`

#### Base User (Customer) Endpoints

Each endpoint allows the user associated with the provided token to
manipulate and interact with their resources, including their user profile,
accounts, and transactions. 

* [View Profile](next/markdown/user/viewProfile.md) : `GET /user/:userId`
* [Create Profile](next/markdown/auth/register.md) : `POST /register`
* [Edit Profile](next/markdown/user/updateProfile.md) : `PUT /user/:userId`
* [Delete Profile](next/markdown/user/deleteProfile.md) : `DELETE /user/:userId`


* [View Account](next/markdown/account/viewAccount.md) : `GET /user/:userId/accounts/:accountId`
* [View User's Accounts](next/markdown/account/viewAllUserAccounts.md) : `GET /user/:userId/accounts`
* [Open Account](next/markdown/account/openAccount.md) : `POST /user/:userId/accounts`
* [Close Account](next/markdown/account/closeAccount.md) : `DELETE /user/:userId/accounts/:accountId`


* [View Account's Sent Transactions](next/markdown/account/viewAccountSentTransactions.md) : `GET /user/:userId/accounts/:accountId/sent`
* [View Account's Received Transactions](next/markdown/account/viewAccountReceivedTransactions.md) : `GET /user/:userId/accounts/:accountId/received`
* [Place Transaction](next/markdown/transaction/placeTransaction.md) : `POST /user/:userId/accounts/:accountId/place`

#### Super User (Admin) Endpoints

Admin can view all resources provided by the API and manage their own user profile, but are restricted in having their
own accounts and sending/receiving transactions.

* [View all Profiles](next/markdown/admin/viewAllProfiles.md) : `GET /user/all`
* [View all Accounts](next/markdown/admin/viewAllAccounts.md) : `GET /account/all`
* [View all Transactions](next/markdown/admin/viewAllTransactions.md) : `GET /transaction/all`
