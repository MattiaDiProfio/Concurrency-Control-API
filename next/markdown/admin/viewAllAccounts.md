## View all Accounts

Given that the requesting user has admin privileges, this endpoint will return a list of
all the accounts currently registered on the API. User objects are omitted and replaced by an accountOwnerId field,
while Transactions are included in the response object.

* URL `/account/all`
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

* Cause : The userId of the user trying to access this resource is not a recognised admin user
* Code : `403 Forbidden`
* Response Message
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "Unauthorized access! You must be an admin to interact with this resource"
    ]
}
```