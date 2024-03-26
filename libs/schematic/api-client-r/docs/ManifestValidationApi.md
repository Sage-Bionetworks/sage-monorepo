# ManifestValidationApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**SubmitManifestCsv**](ManifestValidationApi.md#SubmitManifestCsv) | **POST** /submitManifestCsv | Validates manifest in csv form, then submits it
[**SubmitManifestJson**](ManifestValidationApi.md#SubmitManifestJson) | **POST** /submitManifestJson | Validates a manifest in json form, then submits it
[**ValidateManifestCsv**](ManifestValidationApi.md#ValidateManifestCsv) | **POST** /validateManifestCsv | Validates a manifest in csv form
[**ValidateManifestJson**](ManifestValidationApi.md#ValidateManifestJson) | **POST** /validateManifestJson | Validates a manifest in json form


# **SubmitManifestCsv**
> character SubmitManifestCsv(schema_url, component, dataset_id, asset_view_id, body, restrict_rules = FALSE, storage_method = "table_file_and_entities", hide_blanks = FALSE, table_manipulation_method = "replace", use_schema_label = TRUE)

Validates manifest in csv form, then submits it

Validates manifest in csv form, then submits it

### Example
```R
library(openapi)

# Validates manifest in csv form, then submits it
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_component <- "component_example" # character | A component in a schema, either the dsplay label or schema label
var_dataset_id <- "dataset_id_example" # character | The ID of a dataset.
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_body <- File.new('/path/to/file') # data.frame | .csv file
var_restrict_rules <- FALSE # character | If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available. (Optional)
var_storage_method <- "table_file_and_entities" # character | file_and_entities will store the manifest as a csv and create Synapse files for each row in the manifest. table_and_file will store the manifest as a table and a csv on Synapse. file_only will store the manifest as a csv only on Synapse. table_file_and_entities will perform the options file_with_entites and table in combination. (Optional)
var_hide_blanks <- FALSE # character | If true, annotations with blank values will be hidden from a dataset's annotation list in Synaspe. If false, annotations with blank values will be displayed. (Optional)
var_table_manipulation_method <- "replace" # character | replace will remove the rows and columns from the existing table and store the new rows and columns, preserving the name and synID. upsert will add the new rows to the table and preserve the exisitng rows and columns in the existing table. (Optional)
var_use_schema_label <- TRUE # character | If true, store attributes using the schema label If false, store attributes using the display label (Optional)

api_instance <- ManifestValidationApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$SubmitManifestCsv(var_schema_url, var_component, var_dataset_id, var_asset_view_id, var_body, restrict_rules = var_restrict_rules, storage_method = var_storage_method, hide_blanks = var_hide_blanks, table_manipulation_method = var_table_manipulation_method, use_schema_label = var_use_schema_labeldata_file = "result.txt")
result <- api_instance$SubmitManifestCsv(var_schema_url, var_component, var_dataset_id, var_asset_view_id, var_body, restrict_rules = var_restrict_rules, storage_method = var_storage_method, hide_blanks = var_hide_blanks, table_manipulation_method = var_table_manipulation_method, use_schema_label = var_use_schema_label)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **component** | **character**| A component in a schema, either the dsplay label or schema label | 
 **dataset_id** | **character**| The ID of a dataset. | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **body** | **data.frame**| .csv file | 
 **restrict_rules** | **character**| If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available. | [optional] [default to FALSE]
 **storage_method** | Enum [file_and_entities, table_and_file, file_only, table_file_and_entities] | file_and_entities will store the manifest as a csv and create Synapse files for each row in the manifest. table_and_file will store the manifest as a table and a csv on Synapse. file_only will store the manifest as a csv only on Synapse. table_file_and_entities will perform the options file_with_entites and table in combination. | [optional] [default to &quot;table_file_and_entities&quot;]
 **hide_blanks** | **character**| If true, annotations with blank values will be hidden from a dataset&#39;s annotation list in Synaspe. If false, annotations with blank values will be displayed. | [optional] [default to FALSE]
 **table_manipulation_method** | Enum [replace, upsert] | replace will remove the rows and columns from the existing table and store the new rows and columns, preserving the name and synID. upsert will add the new rows to the table and preserve the exisitng rows and columns in the existing table. | [optional] [default to &quot;replace&quot;]
 **use_schema_label** | **character**| If true, store attributes using the schema label If false, store attributes using the display label | [optional] [default to TRUE]

### Return type

**character**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/csv
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **SubmitManifestJson**
> character SubmitManifestJson(schema_url, component, dataset_id, asset_view_id, restrict_rules = FALSE, storage_method = "table_file_and_entities", hide_blanks = FALSE, table_manipulation_method = "replace", use_schema_label = TRUE, body = var.body)

Validates a manifest in json form, then submits it

Validates a manifest in json form, then submits it in csv form

### Example
```R
library(openapi)

# Validates a manifest in json form, then submits it
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_component <- "component_example" # character | A component in a schema, either the dsplay label or schema label
var_dataset_id <- "dataset_id_example" # character | The ID of a dataset.
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_restrict_rules <- FALSE # character | If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available. (Optional)
var_storage_method <- "table_file_and_entities" # character | file_and_entities will store the manifest as a csv and create Synapse files for each row in the manifest. table_and_file will store the manifest as a table and a csv on Synapse. file_only will store the manifest as a csv only on Synapse. table_file_and_entities will perform the options file_with_entites and table in combination. (Optional)
var_hide_blanks <- FALSE # character | If true, annotations with blank values will be hidden from a dataset's annotation list in Synaspe. If false, annotations with blank values will be displayed. (Optional)
var_table_manipulation_method <- "replace" # character | replace will remove the rows and columns from the existing table and store the new rows and columns, preserving the name and synID. upsert will add the new rows to the table and preserve the exisitng rows and columns in the existing table. (Optional)
var_use_schema_label <- TRUE # character | If true, store attributes using the schema label If false, store attributes using the display label (Optional)
var_body <- "body_example" # character | A manifest in json form (Optional)

api_instance <- ManifestValidationApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$SubmitManifestJson(var_schema_url, var_component, var_dataset_id, var_asset_view_id, restrict_rules = var_restrict_rules, storage_method = var_storage_method, hide_blanks = var_hide_blanks, table_manipulation_method = var_table_manipulation_method, use_schema_label = var_use_schema_label, body = var_bodydata_file = "result.txt")
result <- api_instance$SubmitManifestJson(var_schema_url, var_component, var_dataset_id, var_asset_view_id, restrict_rules = var_restrict_rules, storage_method = var_storage_method, hide_blanks = var_hide_blanks, table_manipulation_method = var_table_manipulation_method, use_schema_label = var_use_schema_label, body = var_body)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **component** | **character**| A component in a schema, either the dsplay label or schema label | 
 **dataset_id** | **character**| The ID of a dataset. | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **restrict_rules** | **character**| If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available. | [optional] [default to FALSE]
 **storage_method** | Enum [file_and_entities, table_and_file, file_only, table_file_and_entities] | file_and_entities will store the manifest as a csv and create Synapse files for each row in the manifest. table_and_file will store the manifest as a table and a csv on Synapse. file_only will store the manifest as a csv only on Synapse. table_file_and_entities will perform the options file_with_entites and table in combination. | [optional] [default to &quot;table_file_and_entities&quot;]
 **hide_blanks** | **character**| If true, annotations with blank values will be hidden from a dataset&#39;s annotation list in Synaspe. If false, annotations with blank values will be displayed. | [optional] [default to FALSE]
 **table_manipulation_method** | Enum [replace, upsert] | replace will remove the rows and columns from the existing table and store the new rows and columns, preserving the name and synID. upsert will add the new rows to the table and preserve the exisitng rows and columns in the existing table. | [optional] [default to &quot;replace&quot;]
 **use_schema_label** | **character**| If true, store attributes using the schema label If false, store attributes using the display label | [optional] [default to TRUE]
 **body** | **character**| A manifest in json form | [optional] 

### Return type

**character**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: text/plain
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **ValidateManifestCsv**
> ManifestValidationResult ValidateManifestCsv(schema_url, component_label, body, restrict_rules = FALSE)

Validates a manifest in csv form

Validates a manifest in csv form

### Example
```R
library(openapi)

# Validates a manifest in csv form
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_component_label <- "component_label_example" # character | The label of a component in a schema
var_body <- File.new('/path/to/file') # data.frame | .csv file
var_restrict_rules <- FALSE # character | If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available. (Optional)

api_instance <- ManifestValidationApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$ValidateManifestCsv(var_schema_url, var_component_label, var_body, restrict_rules = var_restrict_rulesdata_file = "result.txt")
result <- api_instance$ValidateManifestCsv(var_schema_url, var_component_label, var_body, restrict_rules = var_restrict_rules)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **component_label** | **character**| The label of a component in a schema | 
 **body** | **data.frame**| .csv file | 
 **restrict_rules** | **character**| If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available. | [optional] [default to FALSE]

### Return type

[**ManifestValidationResult**](ManifestValidationResult.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/csv
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **ValidateManifestJson**
> ManifestValidationResult ValidateManifestJson(schema_url, component_label, restrict_rules = FALSE, body = var.body)

Validates a manifest in json form

Validates a manifest in json form

### Example
```R
library(openapi)

# Validates a manifest in json form
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_component_label <- "component_label_example" # character | The label of a component in a schema
var_restrict_rules <- FALSE # character | If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available. (Optional)
var_body <- "body_example" # character | A manifest in json form (Optional)

api_instance <- ManifestValidationApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$ValidateManifestJson(var_schema_url, var_component_label, restrict_rules = var_restrict_rules, body = var_bodydata_file = "result.txt")
result <- api_instance$ValidateManifestJson(var_schema_url, var_component_label, restrict_rules = var_restrict_rules, body = var_body)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **component_label** | **character**| The label of a component in a schema | 
 **restrict_rules** | **character**| If True, validation suite will only run with in-house validation rule. If False, the Great Expectations suite will be utilized and all rules will be available. | [optional] [default to FALSE]
 **body** | **character**| A manifest in json form | [optional] 

### Return type

[**ManifestValidationResult**](ManifestValidationResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: text/plain
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

