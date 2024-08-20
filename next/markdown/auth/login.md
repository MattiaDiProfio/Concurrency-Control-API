<div style="display: flex; justify-content: space-between; align-items: center;">
  <div>
    <h2>Login</h2>
  </div>
  <div style="margin-top: 20px;">
    <a href="javascript:history.back()" style="text-decoration: none; background-color: #4CAF50; color: white; padding: 10px 20px; border-radius: 5px;"><b>Back</b></a>
  </div>
</div>

Login with a registered profile in order to access further endpoints. The JWT token 
required to authenticate your requests will be included in a successful response's payload.

**Note** : Repeated requests to `/authenticate` will disable all previously valid tokens and leave the most recent one valid. In future versions of the API, users will not 
be able to attempt a log in if they have already done so. A mechanism to "ban" the user after 3 failed attempts will also be implemented.

* URL `/authenticate`
* Method : `POST`
* Auth required : **No**
* Request Structure

```json
{
    "username" : "[valid username]",
    "password" : "[plaintext password]"
}
```

### Success Response

* Code : `200 OK`

* Response Structure

```json
{
    "username" : "[valid username]",
    "token" : "[json web token]"
}
```

### Error Responses

* Cause : Provided username does not belong to a registered profile
* Code : `401 Unauthorized`
* Response Message 

```json
"Username does not exist"
```

* Cause : Username is valid, however the provided password is not
* Code : `401 Unauthorized`
* Response Message : 
```json
"You provided an incorrect password"
```