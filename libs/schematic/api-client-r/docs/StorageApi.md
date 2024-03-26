# StorageApi

All URIs are relative to *http://localhost/api/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**GetAssetViewJson**](StorageApi.md#GetAssetViewJson) | **GET** /assetTypes/{assetType}/assetViews/{assetViewId}/json | Gets the asset view table in json form
[**GetDatasetFileMetadataArray**](StorageApi.md#GetDatasetFileMetadataArray) | **GET** /assetTypes/{assetType}/datasets/{datasetId}/fileMetadataArray | Gets all files associated with a dataset.
[**GetDatasetFileMetadataPage**](StorageApi.md#GetDatasetFileMetadataPage) | **GET** /assetTypes/{assetType}/datasets/{datasetId}/fileMetadataPage | Gets all files associated with a dataset.
[**GetDatasetManifestJson**](StorageApi.md#GetDatasetManifestJson) | **GET** /assetTypes/{assetType}/datasets/{datasetId}/manifestJson | Gets the manifest in json form
[**GetManifestJson**](StorageApi.md#GetManifestJson) | **GET** /assetTypes/{assetType}/manifests/{manifestId}/json | Gets the manifest in json form
[**GetProjectDatasetMetadataArray**](StorageApi.md#GetProjectDatasetMetadataArray) | **GET** /assetTypes/{assetType}/projects/{projectId}/datasetMetadataArray | Gets all dataset metadata in folder under a given storage project that the current user has access to.
[**GetProjectDatasetMetadataPage**](StorageApi.md#GetProjectDatasetMetadataPage) | **GET** /assetTypes/{assetType}/projects/{projectId}/datasetMetadataPage | Gets a page of dataset metadata in folder under a given storage project that the current user has access to.
[**GetProjectManifestMetadataArray**](StorageApi.md#GetProjectManifestMetadataArray) | **GET** /assetTypes/{assetType}/projects/{projectId}/manifestMetadataArray | Gets all manifests in a project folder that users have access to
[**GetProjectManifestMetadataPage**](StorageApi.md#GetProjectManifestMetadataPage) | **GET** /assetTypes/{assetType}/projects/{projectId}/manifestMetadataPage | Gets all manifests in a project folder that users have access to
[**GetProjectMetadataArray**](StorageApi.md#GetProjectMetadataArray) | **GET** /assetTypes/{assetType}/assetViews/{assetViewId}/projectMetadataArray | Gets all storage projects the current user has access to.
[**GetProjectMetadataPage**](StorageApi.md#GetProjectMetadataPage) | **GET** /assetTypes/{assetType}/assetViews/{assetViewId}/projectMetadataPage | Gets all storage projects the current user has access to.


# **GetAssetViewJson**
> object GetAssetViewJson(asset_view_id, asset_type)

Gets the asset view table in json form

Gets the asset view table in json form

### Example
```R
library(openapi)

# Gets the asset view table in json form
#
# prepare function argument(s)
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetAssetViewJson(var_asset_view_id, var_asset_typedata_file = "result.txt")
result <- api_instance$GetAssetViewJson(var_asset_view_id, var_asset_type)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 

### Return type

**object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetDatasetFileMetadataArray**
> FileMetadataArray GetDatasetFileMetadataArray(dataset_id, asset_type, asset_view_id, file_names = var.file_names, use_full_file_path = FALSE)

Gets all files associated with a dataset.

Gets all files associated with a dataset.

### Example
```R
library(openapi)

# Gets all files associated with a dataset.
#
# prepare function argument(s)
var_dataset_id <- "dataset_id_example" # character | The ID of a dataset.
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_file_names <- c("inner_example") # array[character] | A list of file names used to filter the output. (Optional)
var_use_full_file_path <- FALSE # character | Whether or not to return the full path of output, or just the basename. (Optional)

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetDatasetFileMetadataArray(var_dataset_id, var_asset_type, var_asset_view_id, file_names = var_file_names, use_full_file_path = var_use_full_file_pathdata_file = "result.txt")
result <- api_instance$GetDatasetFileMetadataArray(var_dataset_id, var_asset_type, var_asset_view_id, file_names = var_file_names, use_full_file_path = var_use_full_file_path)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **dataset_id** | **character**| The ID of a dataset. | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **file_names** | list( **character** )| A list of file names used to filter the output. | [optional] 
 **use_full_file_path** | **character**| Whether or not to return the full path of output, or just the basename. | [optional] [default to FALSE]

### Return type

[**FileMetadataArray**](FileMetadataArray.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetDatasetFileMetadataPage**
> FileMetadataPage GetDatasetFileMetadataPage(dataset_id, asset_type, asset_view_id, file_names = var.file_names, use_full_file_path = FALSE, page_number = 1, page_max_items = 100000)

Gets all files associated with a dataset.

Gets all files associated with a dataset.

### Example
```R
library(openapi)

# Gets all files associated with a dataset.
#
# prepare function argument(s)
var_dataset_id <- "dataset_id_example" # character | The ID of a dataset.
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_file_names <- c("inner_example") # array[character] | A list of file names used to filter the output. (Optional)
var_use_full_file_path <- FALSE # character | Whether or not to return the full path of output, or just the basename. (Optional)
var_page_number <- 1 # integer | The page number to get for a paginated query (Optional)
var_page_max_items <- 100000 # integer | The maximum number of items per page (up to 100,000) for paginated endpoints (Optional)

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetDatasetFileMetadataPage(var_dataset_id, var_asset_type, var_asset_view_id, file_names = var_file_names, use_full_file_path = var_use_full_file_path, page_number = var_page_number, page_max_items = var_page_max_itemsdata_file = "result.txt")
result <- api_instance$GetDatasetFileMetadataPage(var_dataset_id, var_asset_type, var_asset_view_id, file_names = var_file_names, use_full_file_path = var_use_full_file_path, page_number = var_page_number, page_max_items = var_page_max_items)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **dataset_id** | **character**| The ID of a dataset. | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **file_names** | list( **character** )| A list of file names used to filter the output. | [optional] 
 **use_full_file_path** | **character**| Whether or not to return the full path of output, or just the basename. | [optional] [default to FALSE]
 **page_number** | **integer**| The page number to get for a paginated query | [optional] [default to 1]
 **page_max_items** | **integer**| The maximum number of items per page (up to 100,000) for paginated endpoints | [optional] [default to 100000]

### Return type

[**FileMetadataPage**](FileMetadataPage.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetDatasetManifestJson**
> object GetDatasetManifestJson(asset_type, dataset_id, asset_view_id)

Gets the manifest in json form

Gets the manifest in json form

### Example
```R
library(openapi)

# Gets the manifest in json form
#
# prepare function argument(s)
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_dataset_id <- "dataset_id_example" # character | The ID of a dataset.
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetDatasetManifestJson(var_asset_type, var_dataset_id, var_asset_view_iddata_file = "result.txt")
result <- api_instance$GetDatasetManifestJson(var_asset_type, var_dataset_id, var_asset_view_id)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **dataset_id** | **character**| The ID of a dataset. | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 

### Return type

**object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetManifestJson**
> object GetManifestJson(asset_type, manifest_id)

Gets the manifest in json form

Gets the manifest in json form

### Example
```R
library(openapi)

# Gets the manifest in json form
#
# prepare function argument(s)
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_manifest_id <- "manifest_id_example" # character | ID of a manifest

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetManifestJson(var_asset_type, var_manifest_iddata_file = "result.txt")
result <- api_instance$GetManifestJson(var_asset_type, var_manifest_id)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **manifest_id** | **character**| ID of a manifest | 

### Return type

**object**

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetProjectDatasetMetadataArray**
> DatasetMetadataArray GetProjectDatasetMetadataArray(project_id, asset_type, asset_view_id)

Gets all dataset metadata in folder under a given storage project that the current user has access to.

Gets all dataset meatdata in folder under a given storage project that the current user has access to.

### Example
```R
library(openapi)

# Gets all dataset metadata in folder under a given storage project that the current user has access to.
#
# prepare function argument(s)
var_project_id <- "project_id_example" # character | The Synapse ID of a storage project.
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetProjectDatasetMetadataArray(var_project_id, var_asset_type, var_asset_view_iddata_file = "result.txt")
result <- api_instance$GetProjectDatasetMetadataArray(var_project_id, var_asset_type, var_asset_view_id)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **project_id** | **character**| The Synapse ID of a storage project. | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 

### Return type

[**DatasetMetadataArray**](DatasetMetadataArray.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetProjectDatasetMetadataPage**
> DatasetMetadataPage GetProjectDatasetMetadataPage(project_id, asset_type, asset_view_id, page_number = 1, page_max_items = 100000)

Gets a page of dataset metadata in folder under a given storage project that the current user has access to.

Gets a page of dataset meatdata in folder under a given storage project that the current user has access to.

### Example
```R
library(openapi)

# Gets a page of dataset metadata in folder under a given storage project that the current user has access to.
#
# prepare function argument(s)
var_project_id <- "project_id_example" # character | The Synapse ID of a storage project.
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_page_number <- 1 # integer | The page number to get for a paginated query (Optional)
var_page_max_items <- 100000 # integer | The maximum number of items per page (up to 100,000) for paginated endpoints (Optional)

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetProjectDatasetMetadataPage(var_project_id, var_asset_type, var_asset_view_id, page_number = var_page_number, page_max_items = var_page_max_itemsdata_file = "result.txt")
result <- api_instance$GetProjectDatasetMetadataPage(var_project_id, var_asset_type, var_asset_view_id, page_number = var_page_number, page_max_items = var_page_max_items)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **project_id** | **character**| The Synapse ID of a storage project. | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **page_number** | **integer**| The page number to get for a paginated query | [optional] [default to 1]
 **page_max_items** | **integer**| The maximum number of items per page (up to 100,000) for paginated endpoints | [optional] [default to 100000]

### Return type

[**DatasetMetadataPage**](DatasetMetadataPage.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetProjectManifestMetadataArray**
> ManifestMetadataArray GetProjectManifestMetadataArray(project_id, asset_type, asset_view_id)

Gets all manifests in a project folder that users have access to

Gets all manifests in a project folder that the current user has access to.

### Example
```R
library(openapi)

# Gets all manifests in a project folder that users have access to
#
# prepare function argument(s)
var_project_id <- "project_id_example" # character | The Synapse ID of a storage project.
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetProjectManifestMetadataArray(var_project_id, var_asset_type, var_asset_view_iddata_file = "result.txt")
result <- api_instance$GetProjectManifestMetadataArray(var_project_id, var_asset_type, var_asset_view_id)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **project_id** | **character**| The Synapse ID of a storage project. | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 

### Return type

[**ManifestMetadataArray**](ManifestMetadataArray.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetProjectManifestMetadataPage**
> ManifestMetadataPage GetProjectManifestMetadataPage(project_id, asset_type, asset_view_id, page_number = 1, page_max_items = 100000)

Gets all manifests in a project folder that users have access to

Gets all manifests in a project folder that the current user has access to.

### Example
```R
library(openapi)

# Gets all manifests in a project folder that users have access to
#
# prepare function argument(s)
var_project_id <- "project_id_example" # character | The Synapse ID of a storage project.
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_page_number <- 1 # integer | The page number to get for a paginated query (Optional)
var_page_max_items <- 100000 # integer | The maximum number of items per page (up to 100,000) for paginated endpoints (Optional)

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetProjectManifestMetadataPage(var_project_id, var_asset_type, var_asset_view_id, page_number = var_page_number, page_max_items = var_page_max_itemsdata_file = "result.txt")
result <- api_instance$GetProjectManifestMetadataPage(var_project_id, var_asset_type, var_asset_view_id, page_number = var_page_number, page_max_items = var_page_max_items)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **project_id** | **character**| The Synapse ID of a storage project. | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **page_number** | **integer**| The page number to get for a paginated query | [optional] [default to 1]
 **page_max_items** | **integer**| The maximum number of items per page (up to 100,000) for paginated endpoints | [optional] [default to 100000]

### Return type

[**ManifestMetadataPage**](ManifestMetadataPage.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetProjectMetadataArray**
> ProjectMetadataArray GetProjectMetadataArray(asset_view_id, asset_type)

Gets all storage projects the current user has access to.

Gets all storage projects the current user has access to.

### Example
```R
library(openapi)

# Gets all storage projects the current user has access to.
#
# prepare function argument(s)
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetProjectMetadataArray(var_asset_view_id, var_asset_typedata_file = "result.txt")
result <- api_instance$GetProjectMetadataArray(var_asset_view_id, var_asset_type)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 

### Return type

[**ProjectMetadataArray**](ProjectMetadataArray.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

# **GetProjectMetadataPage**
> ProjectMetadataPage GetProjectMetadataPage(asset_view_id, asset_type, page_number = 1, page_max_items = 100000)

Gets all storage projects the current user has access to.

Gets all storage projects the current user has access to.

### Example
```R
library(openapi)

# Gets all storage projects the current user has access to.
#
# prepare function argument(s)
var_asset_view_id <- "asset_view_id_example" # character | ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project
var_asset_type <- AssetType$new() # AssetType | Type of asset, such as Synapse
var_page_number <- 1 # integer | The page number to get for a paginated query (Optional)
var_page_max_items <- 100000 # integer | The maximum number of items per page (up to 100,000) for paginated endpoints (Optional)

api_instance <- StorageApi$new()
# Configure HTTP bearer authorization: bearerAuth
api_instance$api_client$bearer_token <- Sys.getenv("BEARER_TOKEN")
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetProjectMetadataPage(var_asset_view_id, var_asset_type, page_number = var_page_number, page_max_items = var_page_max_itemsdata_file = "result.txt")
result <- api_instance$GetProjectMetadataPage(var_asset_view_id, var_asset_type, page_number = var_page_number, page_max_items = var_page_max_items)
dput(result)
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **asset_view_id** | **character**| ID of view listing all project data assets. E.g. for Synapse this would be the Synapse ID of the fileview listing all data assets for a given project | 
 **asset_type** | [**AssetType**](.md)| Type of asset, such as Synapse | 
 **page_number** | **integer**| The page number to get for a paginated query | [optional] [default to 1]
 **page_max_items** | **integer**| The maximum number of items per page (up to 100,000) for paginated endpoints | [optional] [default to 100000]

### Return type

[**ProjectMetadataPage**](ProjectMetadataPage.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Success |  -  |
| **400** | Invalid request |  -  |
| **401** | Unauthorized |  -  |
| **403** | Unauthorized |  -  |
| **404** | The specified resource was not found |  -  |
| **500** | The request cannot be fulfilled due to an unexpected server error |  -  |

