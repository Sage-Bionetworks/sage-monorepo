# bixarena_api_client.AdminApi

All URIs are relative to *https://bixarena.ai/api/v1*

| Method                                                     | HTTP request                                   | Description         |
| ---------------------------------------------------------- | ---------------------------------------------- | ------------------- |
| [**admin_stats**](AdminApi.md#admin_stats)                 | **GET** /admin/stats                           | Admin statistics    |
| [**create_quest**](AdminApi.md#create_quest)               | **POST** /quests                               | Create a quest      |
| [**create_quest_post**](AdminApi.md#create_quest_post)     | **POST** /quests/{questId}/posts               | Create a quest post |
| [**delete_quest**](AdminApi.md#delete_quest)               | **DELETE** /quests/{questId}                   | Delete a quest      |
| [**delete_quest_post**](AdminApi.md#delete_quest_post)     | **DELETE** /quests/{questId}/posts/{postIndex} | Delete a quest post |
| [**reorder_quest_posts**](AdminApi.md#reorder_quest_posts) | **PUT** /quests/{questId}/posts/reorder        | Reorder quest posts |
| [**update_quest**](AdminApi.md#update_quest)               | **PUT** /quests/{questId}                      | Update a quest      |
| [**update_quest_post**](AdminApi.md#update_quest_post)     | **PUT** /quests/{questId}/posts/{postIndex}    | Update a quest post |

# **admin_stats**

> AdminStats200Response admin_stats()

Admin statistics

Administrative operations requiring admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.admin_stats200_response import AdminStats200Response
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AdminApi(api_client)

    try:
        # Admin statistics
        api_response = api_instance.admin_stats()
        print("The response of AdminApi->admin_stats:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminApi->admin_stats: %s\n" % e)
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**AdminStats200Response**](AdminStats200Response.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Success                                                                                           | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_quest**

> Quest create_quest(quest_create_or_update)

Create a quest

Create a new community quest. Requires admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.quest import Quest
from bixarena_api_client.models.quest_create_or_update import QuestCreateOrUpdate
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AdminApi(api_client)
    quest_create_or_update = bixarena_api_client.QuestCreateOrUpdate() # QuestCreateOrUpdate |

    try:
        # Create a quest
        api_response = api_instance.create_quest(quest_create_or_update)
        print("The response of AdminApi->create_quest:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminApi->create_quest: %s\n" % e)
```

### Parameters

| Name                       | Type                                              | Description | Notes |
| -------------------------- | ------------------------------------------------- | ----------- | ----- |
| **quest_create_or_update** | [**QuestCreateOrUpdate**](QuestCreateOrUpdate.md) |             |

### Return type

[**Quest**](Quest.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **201**     | Quest created successfully                                                                        | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **409**     | The request conflicts with current state of the target resource                                   | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_quest_post**

> QuestPost create_quest_post(quest_id, quest_post_create_or_update)

Create a quest post

Add a new post to a quest. The post is appended at the next available
post index. Requires admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.quest_post import QuestPost
from bixarena_api_client.models.quest_post_create_or_update import QuestPostCreateOrUpdate
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AdminApi(api_client)
    quest_id = 'build-bioarena-together' # str | Unique identifier for a quest
    quest_post_create_or_update = bixarena_api_client.QuestPostCreateOrUpdate() # QuestPostCreateOrUpdate |

    try:
        # Create a quest post
        api_response = api_instance.create_quest_post(quest_id, quest_post_create_or_update)
        print("The response of AdminApi->create_quest_post:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminApi->create_quest_post: %s\n" % e)
```

### Parameters

| Name                            | Type                                                      | Description                   | Notes |
| ------------------------------- | --------------------------------------------------------- | ----------------------------- | ----- |
| **quest_id**                    | **str**                                                   | Unique identifier for a quest |
| **quest_post_create_or_update** | [**QuestPostCreateOrUpdate**](QuestPostCreateOrUpdate.md) |                               |

### Return type

[**QuestPost**](QuestPost.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **201**     | Post created successfully                                                                         | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_quest**

> delete_quest(quest_id)

Delete a quest

Delete a quest and all its posts. Requires admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AdminApi(api_client)
    quest_id = 'build-bioarena-together' # str | Unique identifier for a quest

    try:
        # Delete a quest
        api_instance.delete_quest(quest_id)
    except Exception as e:
        print("Exception when calling AdminApi->delete_quest: %s\n" % e)
```

### Parameters

| Name         | Type    | Description                   | Notes |
| ------------ | ------- | ----------------------------- | ----- |
| **quest_id** | **str** | Unique identifier for a quest |

### Return type

void (empty response body)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/problem+json, application/json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **204**     | Quest deleted successfully                                                                        | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_quest_post**

> delete_quest_post(quest_id, post_index)

Delete a quest post

Delete a quest post. Remaining posts are not automatically reindexed. Requires admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AdminApi(api_client)
    quest_id = 'build-bioarena-together' # str | Unique identifier for a quest
    post_index = 0 # int | Display ordering index of a quest post (0-based)

    try:
        # Delete a quest post
        api_instance.delete_quest_post(quest_id, post_index)
    except Exception as e:
        print("Exception when calling AdminApi->delete_quest_post: %s\n" % e)
```

### Parameters

| Name           | Type    | Description                                      | Notes |
| -------------- | ------- | ------------------------------------------------ | ----- |
| **quest_id**   | **str** | Unique identifier for a quest                    |
| **post_index** | **int** | Display ordering index of a quest post (0-based) |

### Return type

void (empty response body)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/problem+json, application/json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **204**     | Post deleted successfully                                                                         | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **reorder_quest_posts**

> Quest reorder_quest_posts(quest_id, quest_post_reorder)

Reorder quest posts

Reorder all posts in a quest. The request body must contain the complete
list of existing post indexes in the desired new order. The backend
validates that the array contains exactly all existing post indexes
(no duplicates, no missing), then reassigns post_index values 0, 1, 2, ...
based on array order. Requires admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.quest import Quest
from bixarena_api_client.models.quest_post_reorder import QuestPostReorder
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AdminApi(api_client)
    quest_id = 'build-bioarena-together' # str | Unique identifier for a quest
    quest_post_reorder = bixarena_api_client.QuestPostReorder() # QuestPostReorder |

    try:
        # Reorder quest posts
        api_response = api_instance.reorder_quest_posts(quest_id, quest_post_reorder)
        print("The response of AdminApi->reorder_quest_posts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminApi->reorder_quest_posts: %s\n" % e)
```

### Parameters

| Name                   | Type                                        | Description                   | Notes |
| ---------------------- | ------------------------------------------- | ----------------------------- | ----- |
| **quest_id**           | **str**                                     | Unique identifier for a quest |
| **quest_post_reorder** | [**QuestPostReorder**](QuestPostReorder.md) |                               |

### Return type

[**Quest**](Quest.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Posts reordered successfully                                                                      | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_quest**

> Quest update_quest(quest_id, quest_create_or_update)

Update a quest

Update quest metadata. Requires admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.quest import Quest
from bixarena_api_client.models.quest_create_or_update import QuestCreateOrUpdate
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AdminApi(api_client)
    quest_id = 'build-bioarena-together' # str | Unique identifier for a quest
    quest_create_or_update = bixarena_api_client.QuestCreateOrUpdate() # QuestCreateOrUpdate |

    try:
        # Update a quest
        api_response = api_instance.update_quest(quest_id, quest_create_or_update)
        print("The response of AdminApi->update_quest:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminApi->update_quest: %s\n" % e)
```

### Parameters

| Name                       | Type                                              | Description                   | Notes |
| -------------------------- | ------------------------------------------------- | ----------------------------- | ----- |
| **quest_id**               | **str**                                           | Unique identifier for a quest |
| **quest_create_or_update** | [**QuestCreateOrUpdate**](QuestCreateOrUpdate.md) |                               |

### Return type

[**Quest**](Quest.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Quest updated successfully                                                                        | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_quest_post**

> QuestPost update_quest_post(quest_id, post_index, quest_post_create_or_update)

Update a quest post

Update an existing quest post. Requires admin role.

### Example

- Bearer (JWT) Authentication (jwtBearer):

```python
import bixarena_api_client
from bixarena_api_client.models.quest_post import QuestPost
from bixarena_api_client.models.quest_post_create_or_update import QuestPostCreateOrUpdate
from bixarena_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://bixarena.ai/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = bixarena_api_client.Configuration(
    host = "https://bixarena.ai/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): jwtBearer
configuration = bixarena_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with bixarena_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = bixarena_api_client.AdminApi(api_client)
    quest_id = 'build-bioarena-together' # str | Unique identifier for a quest
    post_index = 0 # int | Display ordering index of a quest post (0-based)
    quest_post_create_or_update = bixarena_api_client.QuestPostCreateOrUpdate() # QuestPostCreateOrUpdate |

    try:
        # Update a quest post
        api_response = api_instance.update_quest_post(quest_id, post_index, quest_post_create_or_update)
        print("The response of AdminApi->update_quest_post:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminApi->update_quest_post: %s\n" % e)
```

### Parameters

| Name                            | Type                                                      | Description                                      | Notes |
| ------------------------------- | --------------------------------------------------------- | ------------------------------------------------ | ----- |
| **quest_id**                    | **str**                                                   | Unique identifier for a quest                    |
| **post_index**                  | **int**                                                   | Display ordering index of a quest post (0-based) |
| **quest_post_create_or_update** | [**QuestPostCreateOrUpdate**](QuestPostCreateOrUpdate.md) |                                                  |

### Return type

[**QuestPost**](QuestPost.md)

### Authorization

[jwtBearer](../README.md#jwtBearer)

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                                                       | Response headers                                                                                                                                                                                                                                                      |
| ----------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **200**     | Post updated successfully                                                                         | -                                                                                                                                                                                                                                                                     |
| **400**     | Invalid request                                                                                   | -                                                                                                                                                                                                                                                                     |
| **401**     | Unauthorized                                                                                      | -                                                                                                                                                                                                                                                                     |
| **403**     | The user does not have the permission to perform this action                                      | -                                                                                                                                                                                                                                                                     |
| **404**     | The specified resource was not found                                                              | -                                                                                                                                                                                                                                                                     |
| **429**     | Too many requests. Rate limit exceeded. The client should wait before making additional requests. | _ Retry-After - Seconds to wait before making a new request <br> _ X-RateLimit-Limit - Maximum requests allowed per minute <br> _ X-RateLimit-Remaining - Remaining requests in current window <br> _ X-RateLimit-Reset - Seconds until rate limit window resets <br> |
| **500**     | The request cannot be fulfilled due to an unexpected server error                                 | -                                                                                                                                                                                                                                                                     |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
