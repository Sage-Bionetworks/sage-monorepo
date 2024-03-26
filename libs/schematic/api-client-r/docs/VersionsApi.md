# VersionsApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetSchematicVersion**](VersionsApi.md#GetSchematicVersion) | **GET** /schematicVersion | Gets the version of the schematic library currently used by the API


# **GetSchematicVersion**
> character GetSchematicVersion()

Gets the version of the schematic library currently used by the API

Gets the version of the schematic library currently used by the API

### Example
```R
library(openapi)

# Gets the version of the schematic library currently used by the API
#

api_instance <- VersionsApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetSchematicVersion(data_file = "result.txt")
result <- api_instance$GetSchematicVersion()
dput(result)
```

### Parameters
This endpoint does not need any parameter.

### Return type

**character**

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

