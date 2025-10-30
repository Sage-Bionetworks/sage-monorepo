# RateLimitError

## Properties

| Name                    | Type    | Description                                                             | Notes      |
| ----------------------- | ------- | ----------------------------------------------------------------------- | ---------- |
| **title**               | **str** | A human readable documentation for the problem type                     |
| **status**              | **int** | The HTTP status code                                                    |
| **detail**              | **str** | A human readable explanation specific to this occurrence of the problem | [optional] |
| **type**                | **str** | An absolute URI that identifies the problem type                        | [optional] |
| **instance**            | **str** | An absolute URI that identifies the specific occurrence of the problem  | [optional] |
| **limit**               | **int** | Maximum requests allowed per window                                     |
| **window**              | **str** | Time window for rate limiting                                           |
| **retry_after_seconds** | **int** | Seconds to wait before retrying                                         |

## Example

```python
from bixarena_api_client.models.rate_limit_error import RateLimitError

# TODO update the JSON string below
json = "{}"
# create an instance of RateLimitError from a JSON string
rate_limit_error_instance = RateLimitError.from_json(json)
# print the JSON string representation of the object
print(RateLimitError.to_json())

# convert the object into a dict
rate_limit_error_dict = rate_limit_error_instance.to_dict()
# create an instance of RateLimitError from a dict
rate_limit_error_from_dict = RateLimitError.from_dict(rate_limit_error_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
