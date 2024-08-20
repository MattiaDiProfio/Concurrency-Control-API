## View All Accounts owned by a User

Given that the requesting user is trying to access their profile, or they are an admin, this endpoint will
return a list (possibly empty) of all open accounts associated with the specified userId

* URL `/user/:userId/accounts`
* Method : `GET`
* Auth required : **YES**
* Request Structure
```json
{}
```

### Success Response

* Code : `200 OK`
* Response Structure

```json
{
    [
      {
        "accountId" : 2,
        "balanace" : 153.01
        "sentTransactions" : [
          {
            "transactionId" : 1,
            "amount" : 3.99,
            "senderAccountId" : 2,
            "receiverAccountId" : 5
          },
          "..."
        ]
      ,
        "receivedTransactions" : [ "..." ],
        "accountOwnerId" : 32,
      },
      {
        "..."
      },
      "..."
    ]
}
```

### Error Response

* Cause : The user with id specified does not belong to a registered profile
* Code : `404 Not Found`
* Response Message
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "The user with id '[:userId]' does not exist in our records"
    ]
}
```

* Cause : The userId of the user trying to access this resource is not a recognised admin user or, it does not match with the userId of the profile specified in the request URL
* Code : `403 Forbidden`
* Response Message
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "Unauthorized access! You must be the resource owner or an admin to interact with this resource"
    ]
}
```