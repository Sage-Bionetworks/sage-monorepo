# openchallenges_api_client_python.EdamConceptApi

All URIs are relative to *http://localhost/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**list_edam_concepts**](EdamConceptApi.md#list_edam_concepts) | **GET** /edamConcepts | List EDAM concepts


# **list_edam_concepts**
> EdamConceptsPage list_edam_concepts(edam_concept_search_query=edam_concept_search_query)

List EDAM concepts

List EDAM concepts

### Example


```python
import openchallenges_api_client_python
from openchallenges_api_client_python.models.edam_concept_search_query import EdamConceptSearchQuery
from openchallenges_api_client_python.models.edam_concepts_page import EdamConceptsPage
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.EdamConceptApi(api_client)
    edam_concept_search_query = openchallenges_api_client_python.EdamConceptSearchQuery() # EdamConceptSearchQuery | The search query used to find EDAM concepts. (optional)

    try:
        # List EDAM concepts
        api_response = api_instance.list_edam_concepts(edam_concept_search_query=edam_concept_search_query)
        print("The response of EdamConceptApi->list_edam_concepts:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling EdamConceptApi->list_edam_concepts: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **edam_concept_search_query** | [**EdamConceptSearchQuery**](.md)| The search query used to find EDAM concepts. | [optional] 

### Return type

[**EdamConceptsPage**](EdamConceptsPage.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Success |  -  |
**400** | Invalid request |  -  |
**500** | The request cannot be fulfilled due to an unexpected server error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

