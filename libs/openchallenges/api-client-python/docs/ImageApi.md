# openchallenges_client.ImageApi

All URIs are relative to *http://localhost/v1*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_image**](ImageApi.md#get_image) | **GET** /images | Get an image


# **get_image**
> Image get_image(image_query=image_query)

Get an image

Returns the image specified.

### Example


```python
import openchallenges_client
from openchallenges_client.models.image import Image
from openchallenges_client.models.image_query import ImageQuery
from openchallenges_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_client.Configuration(
    host = "http://localhost/v1"
)


# Enter a context with an instance of the API client
with openchallenges_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_client.ImageApi(api_client)
    image_query = openchallenges_client.ImageQuery() # ImageQuery | The query used to get an image. (optional)

    try:
        # Get an image
        api_response = api_instance.get_image(image_query=image_query)
        print("The response of ImageApi->get_image:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ImageApi->get_image: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **image_query** | [**ImageQuery**](.md)| The query used to get an image. | [optional] 

### Return type

[**Image**](Image.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json, application/problem+json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | An image |  -  |
**404** | The specified resource was not found |  -  |
**500** | The request cannot be fulfilled due to an unexpected server error |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

