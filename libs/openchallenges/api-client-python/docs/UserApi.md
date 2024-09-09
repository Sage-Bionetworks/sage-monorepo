# openchallenges_client.UserApi

All URIs are relative to _http://localhost/v1_

| Method                                    | HTTP request               | Description   |
| ----------------------------------------- | -------------------------- | ------------- |
| [**create_user**](UserApi.md#create_user) | **POST** /users/register   | Create a user |
| [**delete_user**](UserApi.md#delete_user) | **DELETE** /users/{userId} | Delete a user |
| [**get_user**](UserApi.md#get_user)       | **GET** /users/{userId}    | Get a user    |
| [**list_users**](UserApi.md#list_users)   | **GET** /users             | Get all users |

# **create_user**

> UserCreateResponse create_user(user_create_request)

Create a user

Create a user with the specified account name

### Example

```python
import time
import os
import openchallenges_client
from openchallenges_client.models.user_create_request import UserCreateRequest
from openchallenges_client.models.user_create_response import UserCreateResponse
from openchallenges_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_client.UserApi(api_client)
    user_create_request = openchallenges_client.UserCreateRequest() # UserCreateRequest |

    try:
        # Create a user
        api_response = api_instance.create_user(user_create_request)
        print("The response of UserApi->create_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UserApi->create_user: %s\n" % e)
```

### Parameters

| Name                    | Type                                          | Description | Notes |
| ----------------------- | --------------------------------------------- | ----------- | ----- |
| **user_create_request** | [**UserCreateRequest**](UserCreateRequest.md) |             |

### Return type

[**UserCreateResponse**](UserCreateResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **201**     | Account created                                                   | -                |
| **400**     | Invalid request                                                   | -                |
| **409**     | The request conflicts with current state of the target resource   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_user**

> object delete_user(user_id)

Delete a user

Deletes the user specified

### Example

```python
import time
import os
import openchallenges_client
from openchallenges_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_client.UserApi(api_client)
    user_id = 56 # int | The unique identifier of the user, either the user account ID or login

    try:
        # Delete a user
        api_response = api_instance.delete_user(user_id)
        print("The response of UserApi->delete_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UserApi->delete_user: %s\n" % e)
```

### Parameters

| Name        | Type    | Description                                                            | Notes |
| ----------- | ------- | ---------------------------------------------------------------------- | ----- |
| **user_id** | **int** | The unique identifier of the user, either the user account ID or login |

### Return type

**object**

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Deleted                                                           | -                |
| **400**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_user**

> User get_user(user_id)

Get a user

Returns the user specified

### Example

```python
import time
import os
import openchallenges_client
from openchallenges_client.models.user import User
from openchallenges_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_client.UserApi(api_client)
    user_id = 56 # int | The unique identifier of the user, either the user account ID or login

    try:
        # Get a user
        api_response = api_instance.get_user(user_id)
        print("The response of UserApi->get_user:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UserApi->get_user: %s\n" % e)
```

### Parameters

| Name        | Type    | Description                                                            | Notes |
| ----------- | ------- | ---------------------------------------------------------------------- | ----- |
| **user_id** | **int** | The unique identifier of the user, either the user account ID or login |

### Return type

[**User**](User.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | A user                                                            | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_users**

> UsersPage list_users(page_number=page_number, page_size=page_size)

Get all users

Returns the users

### Example

```python
import time
import os
import openchallenges_client
from openchallenges_client.models.users_page import UsersPage
from openchallenges_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_client.UserApi(api_client)
    page_number = 0 # int | The page number. (optional) (default to 0)
    page_size = 100 # int | The number of items in a single page. (optional) (default to 100)

    try:
        # Get all users
        api_response = api_instance.list_users(page_number=page_number, page_size=page_size)
        print("The response of UserApi->list_users:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UserApi->list_users: %s\n" % e)
```

### Parameters

| Name            | Type    | Description                           | Notes                       |
| --------------- | ------- | ------------------------------------- | --------------------------- |
| **page_number** | **int** | The page number.                      | [optional] [default to 0]   |
| **page_size**   | **int** | The number of items in a single page. | [optional] [default to 100] |

### Return type

[**UsersPage**](UsersPage.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **400**     | Invalid request                                                   | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
