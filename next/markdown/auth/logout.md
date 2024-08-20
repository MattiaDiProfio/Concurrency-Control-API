## Logout

Logout from the API. Requires user to specify the Bearer Token (JWT) in the request headers. Sending this request 
will invalidate the current token used by the user to access API resources.
* URL `/logout`
* Method : `POST`
* Auth required : **YES**
* Request Structure : 

```json
{}
```

### Success Response

* Code : `202 Accepted`
* Response Structure
```json
"Logout successful"
```

### Error Responses

* Cause : User provides an expired token in the request headers
* Code : `400 Bad Request`
* Response Message : 
```json
"Cannot logout using expired token"
```

* Cause : User attemps to logout without being logged in
* Code : `400 Bad Request`
* Response Message :
```json
"Cannot logout before login"
```