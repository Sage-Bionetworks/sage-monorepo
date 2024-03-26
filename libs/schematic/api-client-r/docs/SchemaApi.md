# SchemaApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetComponent**](SchemaApi.md#GetComponent) | **GET** /components/{componentLabel}/ | Get all the attributes associated with a specific data model component formatted as a dataframe (stored as a JSON String).
[**GetConnectedNodePairArray**](SchemaApi.md#GetConnectedNodePairArray) | **GET** /connectedNodePairArray | Gets an array of connected node pairs
[**GetConnectedNodePairPage**](SchemaApi.md#GetConnectedNodePairPage) | **GET** /connectedNodePairPage | Gets a page of connected node pairs
[**GetNodeDependencyArray**](SchemaApi.md#GetNodeDependencyArray) | **GET** /nodes/{nodeLabel}/dependencyArray | Gets the immediate dependencies that are related to the given source node
[**GetNodeDependencyPage**](SchemaApi.md#GetNodeDependencyPage) | **GET** /nodes/{nodeLabel}/dependencyPage | Gets the immediate dependencies that are related to the given source node
[**GetNodeIsRequired**](SchemaApi.md#GetNodeIsRequired) | **GET** /nodes/{nodeDisplay}/isRequired | Gets whether or not the node is required in the schema
[**GetNodeProperties**](SchemaApi.md#GetNodeProperties) | **GET** /nodes/{nodeLabel}/nodeProperties | Gets properties associated with a given node
[**GetNodeValidationRules**](SchemaApi.md#GetNodeValidationRules) | **GET** /nodes/{nodeDisplay}/validationRules | Gets the validation rules, along with the arguments for each given rule associated with a given node
[**GetPropertyLabel**](SchemaApi.md#GetPropertyLabel) | **GET** /nodes/{nodeDisplay}/propertyLabel | Gets the property label of the node
[**GetSchemaAttributes**](SchemaApi.md#GetSchemaAttributes) | **GET** /schemaAttributes | Get all the attributes associated with a data model formatted as a dataframe (stored as a JSON String).


# **GetComponent**
> character GetComponent(component_label, schema_url, include_index = FALSE)

Get all the attributes associated with a specific data model component formatted as a dataframe (stored as a JSON String).

Get all the attributes associated with a specific data model component formatted as a dataframe (stored as a JSON String).

### Example
```R
library(openapi)

# Get all the attributes associated with a specific data model component formatted as a dataframe (stored as a JSON String).
#
# prepare function argument(s)
var_component_label <- "component_label_example" # character | The label of a component in a schema
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_include_index <- FALSE # character | Whether to include the indexes of the dataframe in the returned JSON string. (Optional)

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetComponent(var_component_label, var_schema_url, include_index = var_include_indexdata_file = "result.txt")
result <- api_instance$GetComponent(var_component_label, var_schema_url, include_index = var_include_index)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **component_label** | **character**| The label of a component in a schema | 
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **include_index** | **character**| Whether to include the indexes of the dataframe in the returned JSON string. | [optional] [default to FALSE]

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

# **GetConnectedNodePairArray**
> ConnectedNodePairArray GetConnectedNodePairArray(schema_url, relationship_type)

Gets an array of connected node pairs

Gets a array of connected node pairs

### Example
```R
library(openapi)

# Gets an array of connected node pairs
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_relationship_type <- "relationship_type_example" # character | Type of relationship in a schema, such as requiresDependency

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetConnectedNodePairArray(var_schema_url, var_relationship_typedata_file = "result.txt")
result <- api_instance$GetConnectedNodePairArray(var_schema_url, var_relationship_type)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **relationship_type** | **character**| Type of relationship in a schema, such as requiresDependency | 

### Return type

[**ConnectedNodePairArray**](ConnectedNodePairArray.md)

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

# **GetConnectedNodePairPage**
> ConnectedNodePairPage GetConnectedNodePairPage(schema_url, relationship_type, page_number = 1, page_max_items = 100000)

Gets a page of connected node pairs

Gets a page of connected node pairs

### Example
```R
library(openapi)

# Gets a page of connected node pairs
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_relationship_type <- "relationship_type_example" # character | Type of relationship in a schema, such as requiresDependency
var_page_number <- 1 # integer | The page number to get for a paginated query (Optional)
var_page_max_items <- 100000 # integer | The maximum number of items per page (up to 100,000) for paginated endpoints (Optional)

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetConnectedNodePairPage(var_schema_url, var_relationship_type, page_number = var_page_number, page_max_items = var_page_max_itemsdata_file = "result.txt")
result <- api_instance$GetConnectedNodePairPage(var_schema_url, var_relationship_type, page_number = var_page_number, page_max_items = var_page_max_items)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **relationship_type** | **character**| Type of relationship in a schema, such as requiresDependency | 
 **page_number** | **integer**| The page number to get for a paginated query | [optional] [default to 1]
 **page_max_items** | **integer**| The maximum number of items per page (up to 100,000) for paginated endpoints | [optional] [default to 100000]

### Return type

[**ConnectedNodePairPage**](ConnectedNodePairPage.md)

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

# **GetNodeDependencyArray**
> NodeArray GetNodeDependencyArray(node_label, schema_url, return_display_names = TRUE, return_ordered_by_schema = TRUE)

Gets the immediate dependencies that are related to the given source node

Gets the immediate dependencies that are related to the given source node

### Example
```R
library(openapi)

# Gets the immediate dependencies that are related to the given source node
#
# prepare function argument(s)
var_node_label <- "node_label_example" # character | The label of the source node in a schema to get the dependencies of
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_return_display_names <- TRUE # character | Whether or not to return the display names of the component, otherwise the label (Optional)
var_return_ordered_by_schema <- TRUE # character | Whether or not to order the components by their order in the schema, otherwise random (Optional)

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetNodeDependencyArray(var_node_label, var_schema_url, return_display_names = var_return_display_names, return_ordered_by_schema = var_return_ordered_by_schemadata_file = "result.txt")
result <- api_instance$GetNodeDependencyArray(var_node_label, var_schema_url, return_display_names = var_return_display_names, return_ordered_by_schema = var_return_ordered_by_schema)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **node_label** | **character**| The label of the source node in a schema to get the dependencies of | 
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **return_display_names** | **character**| Whether or not to return the display names of the component, otherwise the label | [optional] [default to TRUE]
 **return_ordered_by_schema** | **character**| Whether or not to order the components by their order in the schema, otherwise random | [optional] [default to TRUE]

### Return type

[**NodeArray**](NodeArray.md)

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

# **GetNodeDependencyPage**
> NodePage GetNodeDependencyPage(node_label, schema_url, return_display_names = TRUE, return_ordered_by_schema = TRUE, page_number = 1, page_max_items = 100000)

Gets the immediate dependencies that are related to the given source node

Gets the immediate dependencies that are related to the given source node

### Example
```R
library(openapi)

# Gets the immediate dependencies that are related to the given source node
#
# prepare function argument(s)
var_node_label <- "node_label_example" # character | The label of the source node in a schema to get the dependencies of
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_return_display_names <- TRUE # character | Whether or not to return the display names of the component, otherwise the label (Optional)
var_return_ordered_by_schema <- TRUE # character | Whether or not to order the components by their order in the schema, otherwise random (Optional)
var_page_number <- 1 # integer | The page number to get for a paginated query (Optional)
var_page_max_items <- 100000 # integer | The maximum number of items per page (up to 100,000) for paginated endpoints (Optional)

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetNodeDependencyPage(var_node_label, var_schema_url, return_display_names = var_return_display_names, return_ordered_by_schema = var_return_ordered_by_schema, page_number = var_page_number, page_max_items = var_page_max_itemsdata_file = "result.txt")
result <- api_instance$GetNodeDependencyPage(var_node_label, var_schema_url, return_display_names = var_return_display_names, return_ordered_by_schema = var_return_ordered_by_schema, page_number = var_page_number, page_max_items = var_page_max_items)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **node_label** | **character**| The label of the source node in a schema to get the dependencies of | 
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **return_display_names** | **character**| Whether or not to return the display names of the component, otherwise the label | [optional] [default to TRUE]
 **return_ordered_by_schema** | **character**| Whether or not to order the components by their order in the schema, otherwise random | [optional] [default to TRUE]
 **page_number** | **integer**| The page number to get for a paginated query | [optional] [default to 1]
 **page_max_items** | **integer**| The maximum number of items per page (up to 100,000) for paginated endpoints | [optional] [default to 100000]

### Return type

[**NodePage**](NodePage.md)

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

# **GetNodeIsRequired**
> character GetNodeIsRequired(node_display, schema_url)

Gets whether or not the node is required in the schema

Gets whether or not the node is required in the schema

### Example
```R
library(openapi)

# Gets whether or not the node is required in the schema
#
# prepare function argument(s)
var_node_display <- "node_display_example" # character | The display name of the node in a schema
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetNodeIsRequired(var_node_display, var_schema_urldata_file = "result.txt")
result <- api_instance$GetNodeIsRequired(var_node_display, var_schema_url)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **node_display** | **character**| The display name of the node in a schema | 
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 

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

# **GetNodeProperties**
> NodePropertyArray GetNodeProperties(node_label, schema_url)

Gets properties associated with a given node

Gets properties associated with a given node

### Example
```R
library(openapi)

# Gets properties associated with a given node
#
# prepare function argument(s)
var_node_label <- "node_label_example" # character | The label of the source node in a schema to get the dependencies of
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetNodeProperties(var_node_label, var_schema_urldata_file = "result.txt")
result <- api_instance$GetNodeProperties(var_node_label, var_schema_url)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **node_label** | **character**| The label of the source node in a schema to get the dependencies of | 
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 

### Return type

[**NodePropertyArray**](NodePropertyArray.md)

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

# **GetNodeValidationRules**
> ValidationRuleArray GetNodeValidationRules(node_display, schema_url)

Gets the validation rules, along with the arguments for each given rule associated with a given node

Gets the validation rules, along with the arguments for each given rule associated with a given node

### Example
```R
library(openapi)

# Gets the validation rules, along with the arguments for each given rule associated with a given node
#
# prepare function argument(s)
var_node_display <- "node_display_example" # character | The display name of the node in a schema
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetNodeValidationRules(var_node_display, var_schema_urldata_file = "result.txt")
result <- api_instance$GetNodeValidationRules(var_node_display, var_schema_url)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **node_display** | **character**| The display name of the node in a schema | 
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 

### Return type

[**ValidationRuleArray**](ValidationRuleArray.md)

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

# **GetPropertyLabel**
> character GetPropertyLabel(node_display, schema_url, use_strict_camel_case = TRUE)

Gets the property label of the node

Gets the property label of the node

### Example
```R
library(openapi)

# Gets the property label of the node
#
# prepare function argument(s)
var_node_display <- "node_display_example" # character | The display name of the node in a schema
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_use_strict_camel_case <- TRUE # character | Whether or not to use the more strict way of converting to camel case (Optional)

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetPropertyLabel(var_node_display, var_schema_url, use_strict_camel_case = var_use_strict_camel_casedata_file = "result.txt")
result <- api_instance$GetPropertyLabel(var_node_display, var_schema_url, use_strict_camel_case = var_use_strict_camel_case)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **node_display** | **character**| The display name of the node in a schema | 
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **use_strict_camel_case** | **character**| Whether or not to use the more strict way of converting to camel case | [optional] [default to TRUE]

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

# **GetSchemaAttributes**
> character GetSchemaAttributes(schema_url)

Get all the attributes associated with a data model formatted as a dataframe (stored as a JSON String).

Get all the attributes associated with a data model formatted as a dataframe (stored as a JSON String).

### Example
```R
library(openapi)

# Get all the attributes associated with a data model formatted as a dataframe (stored as a JSON String).
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form

api_instance <- SchemaApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetSchemaAttributes(var_schema_urldata_file = "result.txt")
result <- api_instance$GetSchemaAttributes(var_schema_url)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 

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

