# TangledTreeApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetTangledTreeLayers**](TangledTreeApi.md#GetTangledTreeLayers) | **GET** /tangledTreeLayers | Get tangled tree node layers to display for a given data model and figure type
[**GetTangledTreeText**](TangledTreeApi.md#GetTangledTreeText) | **GET** /tangledTreeText | Get tangled tree plain or higlighted text to display for a given data model, text formatting and figure type


# **GetTangledTreeLayers**
> character GetTangledTreeLayers(schema_url, figure_type = "component")

Get tangled tree node layers to display for a given data model and figure type

Get tangled tree node layers to display for a given data model and figure type

### Example
```R
library(openapi)

# Get tangled tree node layers to display for a given data model and figure type
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_figure_type <- "component" # character | Figure type to generate. (Optional)

api_instance <- TangledTreeApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetTangledTreeLayers(var_schema_url, figure_type = var_figure_typedata_file = "result.txt")
result <- api_instance$GetTangledTreeLayers(var_schema_url, figure_type = var_figure_type)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **figure_type** | Enum [component, dependency] | Figure type to generate. | [optional] [default to &quot;component&quot;]

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

# **GetTangledTreeText**
> object GetTangledTreeText(schema_url, figure_type = "component", text_format = "plain")

Get tangled tree plain or higlighted text to display for a given data model, text formatting and figure type

Get tangled tree plain or higlighted text to display for a given data model, text formatting and figure type

### Example
```R
library(openapi)

# Get tangled tree plain or higlighted text to display for a given data model, text formatting and figure type
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_figure_type <- "component" # character | Figure type to generate. (Optional)
var_text_format <- "plain" # character | Text formatting type. (Optional)

api_instance <- TangledTreeApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetTangledTreeText(var_schema_url, figure_type = var_figure_type, text_format = var_text_formatdata_file = "result.txt")
result <- api_instance$GetTangledTreeText(var_schema_url, figure_type = var_figure_type, text_format = var_text_format)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **figure_type** | Enum [component, dependency] | Figure type to generate. | [optional] [default to &quot;component&quot;]
 **text_format** | Enum [plain, highlighted] | Text formatting type. | [optional] [default to &quot;plain&quot;]

### Return type

**object**

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

