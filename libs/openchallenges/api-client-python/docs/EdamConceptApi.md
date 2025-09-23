# openchallenges_api_client.EdamConceptApi

All URIs are relative to *https://openchallenges.io/api/v1*

| Method                                                         | HTTP request           | Description        |
| -------------------------------------------------------------- | ---------------------- | ------------------ |
| [**list_edam_concepts**](EdamConceptApi.md#list_edam_concepts) | **GET** /edam-concepts | List EDAM concepts |

# **list_edam_concepts**

> EdamConceptsPage list_edam_concepts(edam_concept_search_query=edam_concept_search_query)

List EDAM concepts

List EDAM concepts

### Example

- Api Key Authentication (apiKey):
- Bearer (JWT) Authentication (jwtBearer):

```python
import openchallenges_api_client
from openchallenges_api_client.models.edam_concept_search_query import EdamConceptSearchQuery
from openchallenges_api_client.models.edam_concepts_page import EdamConceptsPage
from openchallenges_api_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure API key authorization: apiKey
configuration.api_key['apiKey'] = os.environ["API_KEY"]

# Uncomment below to setup prefix (e.g. Bearer) for API key, if needed
# configuration.api_key_prefix['apiKey'] = 'Bearer'

# Configure Bearer authorization (JWT): jwtBearer
configuration = openchallenges_api_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openchallenges_api_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client.EdamConceptApi(api_client)
    edam_concept_search_query = openchallenges_api_client.EdamConceptSearchQuery() # EdamConceptSearchQuery | The search query used to find EDAM concepts. (optional)

    try:
        # List EDAM concepts
        api_response = api_instance.list_edam_concepts(edam_concept_search_query=edam_concept_search_query)
        print("The response of EdamConceptApi->list_edam_concepts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling EdamConceptApi->list_edam_concepts: %s\n" % e)
```

### Parameters

| Name                          | Type                              | Description                                  | Notes      |
| ----------------------------- | --------------------------------- | -------------------------------------------- | ---------- |
| **edam_concept_search_query** | [**EdamConceptSearchQuery**](.md) | The search query used to find EDAM concepts. | [optional] |

### Return type

[**EdamConceptsPage**](EdamConceptsPage.md)

### Authorization

[apiKey](../README.md#apiKey), [jwtBearer](../README.md#jwtBearer)

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
