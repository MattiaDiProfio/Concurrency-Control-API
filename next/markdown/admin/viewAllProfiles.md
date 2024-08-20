## View all Profiles

Given that the requesting user has admin privileges, this endpoint will return a list of 
all the profiles currently registered on the API. Accounts and Transactions are omitted from the response
due to serialisation/deserialization measures in place.

* URL `/user/all`
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
    "name" : "[user's full name]",
    "email" : "[user's email address]",
    "address" : "[user's postal address]",
    "username" : "[username]",
    "role" : "[user role]",
    "id" : "[userId]"
  },
  {
    "..."
  }, 
  "..."
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