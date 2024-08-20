## View all Transactions

Given that the requesting user has admin privileges, this endpoint will return a list of
all the transactions ever sent/received through the API. The sender and receiver accounts' accountIds 
are shown by two supplementary fields in the response objects.

* URL `/transaction/all`
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
[
  {
    "transactionId" : 1,
    "amount" : 3.99,
    "senderAccountId" : 2, 
    "receiverAccountId" : 5
  },
  {
    ...
  }, 
  ...
]
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