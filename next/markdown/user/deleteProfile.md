## Delete Profile

Delete a user's profile, therefore deleting the User and closing any of its associated accounts. 

* URL `/user/:userId`
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