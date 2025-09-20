# ImageQuery

An image query that identifies an image either by an object storage key or by a direct URL. Exactly one of `objectKey` or `imageUrl` must be provided.

## Properties

| Name             | Type                                        | Description                                                                      | Notes                                             |
| ---------------- | ------------------------------------------- | -------------------------------------------------------------------------------- | ------------------------------------------------- |
| **object_key**   | **str**                                     | The unique identifier of the image.                                              | [optional]                                        |
| **image_url**    | **str**                                     | The HTTPS URL of the image. Use this as an alternative to &#x60;objectKey&#x60;. | [optional]                                        |
| **height**       | [**ImageHeight**](ImageHeight.md)           |                                                                                  | [optional] [default to ImageHeight.ORIGINAL]      |
| **aspect_ratio** | [**ImageAspectRatio**](ImageAspectRatio.md) |                                                                                  | [optional] [default to ImageAspectRatio.ORIGINAL] |

## Example

```python
from openchallenges_api_client_python.models.image_query import ImageQuery

# TODO update the JSON string below
json = "{}"
# create an instance of ImageQuery from a JSON string
image_query_instance = ImageQuery.from_json(json)
# print the JSON string representation of the object
print(ImageQuery.to_json())

# convert the object into a dict
image_query_dict = image_query_instance.to_dict()
# create an instance of ImageQuery from a dict
image_query_from_dict = ImageQuery.from_dict(image_query_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
