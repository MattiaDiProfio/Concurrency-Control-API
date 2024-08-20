## Update Profile

Update a user profile's non-critical information such as email and postal addresses.

* URL `/user/:userId`
* Method : `PUT`
* Auth required : **YES**
* Request Structure
```json
{
  "email" : "[valid email address]",
  "address" : "[new postal address]"
}
```

### Success Response

* Code : `201 Created`
* Response Structure

```json
{}
```

### Error Response
* Cause : The payload contains missing information or invalid information
* Code : `400 Bad Request`
* Response Message
```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "Address cannot be blank"
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