# schematic_client.StorageApi

All URIs are relative to _http://localhost/api/v1_

| Method                                                                           | HTTP request                                    | Description                                                                                    |
| -------------------------------------------------------------------------------- | ----------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| [**list_storage_project_datasets**](StorageApi.md#list_storage_project_datasets) | **GET** /storages/projects/{projectId}/datasets | Gets all datasets in folder under a given storage project that the current user has access to. |

# **list_storage_project_datasets**

> DatasetsPage list_storage_project_datasets(project_id)

Gets all datasets in folder under a given storage project that the current user has access to.

Gets all datasets in folder under a given storage project that the current user has access to.

### Example

```python
import time
import schematic_client
from schematic_client.api import storage_api
from schematic_client.model.basic_error import BasicError
from schematic_client.model.datasets_page import DatasetsPage
from pprint import pprint
# Defining the host is optional and defaults to http://localhost/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = schematic_client.Configuration(
    host = "http://localhost/api/v1"
)


# Enter a context with an instance of the API client
with schematic_client.ApiClient() as api_client:
    # Create an instance of the API class
    api_instance = storage_api.StorageApi(api_client)
    project_id = "syn26251192" # str | The Synapse ID of a storage project.

    # example passing only required values which don't have defaults set
    try:
        # Gets all datasets in folder under a given storage project that the current user has access to.
        api_response = api_instance.list_storage_project_datasets(project_id)
        pprint(api_response)
    except schematic_client.ApiException as e:
        print("Exception when calling StorageApi->list_storage_project_datasets: %s\n" % e)
```

### Parameters

| Name           | Type    | Description                          | Notes |
| -------------- | ------- | ------------------------------------ | ----- |
| **project_id** | **str** | The Synapse ID of a storage project. |

### Return type

[**DatasetsPage**](DatasetsPage.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | Success                                                           | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
