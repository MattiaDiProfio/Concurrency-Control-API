## Close Account

Close an account if the requesting user is the owner and the account targeted is currently open. This action will
delete the account, but not any transactions in which it was involved.

* URL `/user/:userId/accounts/:accountId`
* Method : `DELETE`
* Auth required : **YES**
* Request Structure
```json
{}
```

### Success Response

* Code : `204 No Content`
* Response Structure

```json
{}
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

* Cause : The accountId specified does not belong to any account managed by the requesting user
* Code : `404 Not Found`
* Response Message
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "We could not find any account with id '[:accountId]' under your management"
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