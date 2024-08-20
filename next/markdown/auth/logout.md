<div style="display: flex; justify-content: space-between; align-items: center;">
  <div>
    <h2>Logout</h2>
  </div>
  <div style="margin-top: 20px;">
    <a href="javascript:history.back()" style="text-decoration: none; background-color: #4CAF50; color: white; padding: 10px 20px; border-radius: 5px;"><b>Back</b></a>
  </div>
</div>

Logout from the API. Requires user to specify the Bearer Token (JWT) in the request headers. Sending this request 
will invalidate the current token used by the user to access API resources.
* URL `/logout`
* Method : `POST`
* Auth required : **YES**
* Request Structure **(Headers)** : 

```json
{
    "Authorization" : "[valid token]"
}
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