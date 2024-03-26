# ManifestGenerationApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GenerateGoogleSheetManifests**](ManifestGenerationApi.md#GenerateGoogleSheetManifests) | **GET** /generateGoogleSheetManifests | Generates a list of google sheet links


# **GenerateGoogleSheetManifests**
> GoogleSheetLinks GenerateGoogleSheetManifests(schema_url, add_annotations = FALSE, dataset_id_array = var.dataset_id_array, manifest_title = var.manifest_title, data_type_array = var.data_type_array, asset_view_id = var.asset_view_id, use_strict_validation = TRUE, generate_all_manifests = FALSE)

Generates a list of google sheet links

Generates a list of google sheet links

### Example
```R
library(openapi)

# Generates a list of google sheet links
#
# prepare function argument(s)
var_schema_url <- "schema_url_example" # character | The URL of a schema in jsonld or csv form
var_add_annotations <- FALSE # character | If true, annotations are added to the manifest (Optional)
var_dataset_id_array <- c("inner_example") # array[character] | An array of dataset ids (Optional)
var_manifest_title <- "manifest_title_example" # character | If making one manifest, the title of the manifest. If making multiple manifests, the prefix of the title of the manifests. (Optional)
var_data_type_array <- c("inner_example") # array[character] | An array of data types (Optional)
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project (Optional)
var_use_strict_validation <- TRUE # character | If true, users are blocked from entering incorrect values. If false, users will get a warning when using incorrect values. (Optional)
var_generate_all_manifests <- FALSE # character | If true, a manifest for all components will be generated, datasetIds will be ignored. If false, manifests for each id in datasetIds will be generated. (Optional)

api_instance <- ManifestGenerationApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GenerateGoogleSheetManifests(var_schema_url, add_annotations = var_add_annotations, dataset_id_array = var_dataset_id_array, manifest_title = var_manifest_title, data_type_array = var_data_type_array, asset_view_id = var_asset_view_id, use_strict_validation = var_use_strict_validation, generate_all_manifests = var_generate_all_manifestsdata_file = "result.txt")
result <- api_instance$GenerateGoogleSheetManifests(var_schema_url, add_annotations = var_add_annotations, dataset_id_array = var_dataset_id_array, manifest_title = var_manifest_title, data_type_array = var_data_type_array, asset_view_id = var_asset_view_id, use_strict_validation = var_use_strict_validation, generate_all_manifests = var_generate_all_manifests)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **schema_url** | **character**| The URL of a schema in jsonld or csv form | 
 **add_annotations** | **character**| If true, annotations are added to the manifest | [optional] [default to FALSE]
 **dataset_id_array** | list( **character** )| An array of dataset ids | [optional] 
 **manifest_title** | **character**| If making one manifest, the title of the manifest. If making multiple manifests, the prefix of the title of the manifests. | [optional] 
 **data_type_array** | list( **character** )| An array of data types | [optional] 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | [optional] 
 **use_strict_validation** | **character**| If true, users are blocked from entering incorrect values. If false, users will get a warning when using incorrect values. | [optional] [default to TRUE]
 **generate_all_manifests** | **character**| If true, a manifest for all components will be generated, datasetIds will be ignored. If false, manifests for each id in datasetIds will be generated. | [optional] [default to FALSE]

### Return type

[**GoogleSheetLinks**](GoogleSheetLinks.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

