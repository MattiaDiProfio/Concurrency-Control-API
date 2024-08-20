<div style="display: flex; justify-content: space-between; align-items: center;">
  <div>
    <h2>Register</h2>
  </div>
  <div style="margin-top: 20px;">
    <a href="javascript:history.back()" style="text-decoration: none; background-color: #4CAF50; color: white; padding: 10px 20px; border-radius: 5px;"><b>Back</b></a>
  </div>
</div>

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
