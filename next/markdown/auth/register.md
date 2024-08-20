## Register

Register a new user. Pay close attention to the payload sent, and ensure that no fields are missing or invalid.

* URL `/register`
* Method : `POST`
* Auth required : **No**
* Request Structure

```json
{
    "name" : "[full name]",
    "email" : "[valid email address]",
    "address" : "[user's postal address]",
    "username" : "[valid username]",
    "password" : "[plaintext password]",
    "role" : "[user role :'ADMIN' || 'CUSTOMER']"
}
```

### Success Response

* Code : `201 Created`
* Response Structure

```json
{}
```

### Error Response

* Cause : Generic - username might already exist
* Code : `400 Bad Request`
* Response Message : `Username does not exist`

```json
{
    "timestamp" : "[dd-MM-yyyy hh-mm-ss]",
    "message" : [
      "Data Integrity Violation: we cannot process your request."
    ]
}
```
