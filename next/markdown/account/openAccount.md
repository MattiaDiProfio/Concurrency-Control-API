## Open Account

POST /user/:userId/accounts

Open an account given that the user sending the request is the profile owner 
and that the type of account to be opened doesn't exist already. 

* URL `/user/:userId/accounts`
* Method : `POST`
* Auth required : **YES**
* Request Structure
```json
{
  "type" : "[valid account type - SAVINGS || CHECKING || INVESTMENT]",
}
```

### Success Response

* Code : `201 Created`
* Response Structure

```json
{}
```

### Error Responses
* Cause : The account type is invalid
* Code : `400 Bad Request`
* Response Message
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "Invalid account type. Must be 'CHECKING', 'INVESTMENT', or 'SAVINGS'",
    ]
}
```

* Cause : An account with specified type already exists under the user's management
* Code : `400 Bad Request`
* Response Message
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "An account of type [:type] already exists under your management.",
    ]
}
```

* Cause : The userId of the user trying to access this resource does not match with the userId of the profile specified in the request URL
* Code : `403 Forbidden`
* Response Message
```json
{
  "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
  "message" : [
    "Unauthorized access! You must be the resource owner to interact with this resource"
  ]
}
```
