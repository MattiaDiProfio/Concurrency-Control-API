# Concurrency API Developer Notes

### API Entities Overview

### API Architecture Overview

### API Endpoints Overview

Open endpoints require no Authentication.

* [Register](markdown/auth/register.md) : `POST /register`
* [Login](markdown/auth/login.md) : `POST /authenticate`

Protected endpoints require a valid token to be included in the request header under the 'Authorization' key.
This token can be obtained by logging-in on the `/authenticate` route.

* [Logout](markdown/auth/logout.md) : `POST /logout`

#### Base User (Customer) Endpoints

Each endpoint allows the user associated with the provided token to
manipulate and interact with their resources, including their user profile,
accounts, and transactions. 

* [View Profile](markdown/user/viewProfile.md) : `GET /user/:userId`
* [Create Profile](markdown/auth/register.md) : `POST /register`
* [Edit Profile](markdown/user/updateProfile.md) : `PUT /user/:userId`
* [Delete Profile](markdown/user/deleteProfile.md) : `DELETE /user/:userId`


* [View Account](markdown/account/viewAccount.md) : `GET /user/:userId/accounts/:accountId`
* [View User's Accounts](markdown/account/viewAllUserAccounts.md) : `GET /user/:userId/accounts`
* [Open Account](markdown/account/openAccount.md) : `POST /user/:userId/accounts`
* [Close Account](markdown/account/closeAccount.md) : `DELETE /user/:userId/accounts/:accountId`


* [View Account's Sent Transactions](markdown/account/viewAccountSentTransactions.md) : `GET /user/:userId/accounts/:accountId/sent`
* [View Account's Received Transactions](markdown/account/viewAccountReceivedTransactions.md) : `GET /user/:userId/accounts/:accountId/received`
* [Place Transaction](markdown/transaction/placeTransaction.md) : `POST /transaction`

#### Super User (Admin) Endpoints

Admin can view all resources provided by the API and manage their own user profile, but are restricted in having their
own accounts and sending/receiving transactions.

* [View all Profiles](markdown/admin/viewAllProfiles.md) : `GET /user/all`
* [View all Accounts](markdown/admin/viewAllAccounts.md) : `GET /account/all`
* [View all Transactions](markdown/admin/viewAllTransactions.md) : `GET /transaction/all`
