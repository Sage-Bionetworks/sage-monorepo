# ImageApi

All URIs are relative to _http://localhost/v1_

| Method                               | HTTP request    | Description  |
| ------------------------------------ | --------------- | ------------ |
| [**GetImage**](ImageApi.md#GetImage) | **GET** /images | Get an image |

# **GetImage**

> Image GetImage(image_query = var.image_query)

Get an image

Returns the image specified.

### Example

```R
library(openapi)

# Get an image
#
# prepare function argument(s)
var_image_query <- ImageQuery$new("objectKey_example", ImageHeight$new(), ImageAspectRatio$new()) # ImageQuery | The query used to get an image. (Optional)

api_instance <- ImageApi$new()
# to save the result into a file, simply add the optional `data_file` parameter, e.g.
# result <- api_instance$GetImage(image_query = var_image_querydata_file = "result.txt")
result <- api_instance$GetImage(image_query = var_image_query)
dput(result)
```

### Parameters

| Name            | Type                  | Description                     | Notes      |
| --------------- | --------------------- | ------------------------------- | ---------- |
| **image_query** | [**ImageQuery**](.md) | The query used to get an image. | [optional] |

### Return type

[**Image**](Image.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description                                                       | Response headers |
| ----------- | ----------------------------------------------------------------- | ---------------- |
| **200**     | An image                                                          | -                |
| **404**     | The specified resource was not found                              | -                |
| **500**     | The request cannot be fulfilled due to an unexpected server error | -                |
