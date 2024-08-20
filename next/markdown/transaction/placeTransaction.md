## Place Transaction

Given that the logged-in user owns the sending account, this endpoint allows
them to transfer money to any other open account, as long as they have enough funds to cover
the transactions, alongside other restrictions.

* URL `/transaction
* Method : `POST`
* Auth required : **YES**
* Request Structure
```json
{
  "amount" : 3.99,
  "senderAccountId" : 2,
  "receiverAccountId" : 5
}
```

### Success Response

* Code : `201 Created`
* Response Structure

```json
{}
```

### Error Responses
* Cause : The payload contains missing information or invalid information
* Code : `400 Bad Request`
* Response Message (Depending upon request payload submitted)
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "Amount cannot be less than 0.01",
      "Insufficient balance to place transaction",
      "Sender and Receiver accounts must be different",
      "Invalid sender account! No account found with the given senderAccountId",
      "Invalid receiver account! No account found with the given receiverAccountId",
    ]
}
```
* Cause : The transaction could not be placed, most likely to occur if the transaction's validation phase fails. Refer to **OCC Algorithm** section in README.md for details.
* Code : `500 Internal Server Error`
* Response Message (Depending upon request payload submitted)
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "Your transaction could not be placed! No funds have left your account."
    ]
}
```