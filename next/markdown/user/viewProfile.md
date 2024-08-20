## View Profile

View a user's profile information, excluding accounts and transactions. Only the profile's owner or an admin 
user can view this resource, as indicated in the Error Responses section below

* URL `/user/:userId`
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
    "name": "[user's full name]",
    "email": "[user's email address]",
    "address": "[user's postal address]",
    "username": "[username]",
    "role": "[user role]",
    "id": "[userId]"
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