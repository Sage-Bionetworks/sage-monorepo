# BasicError

Problem details (tools.ietf.org/html/rfc7807)

## Properties

| Name                | Type                                                             | Description                                                             | Notes      |
| ------------------- | ---------------------------------------------------------------- | ----------------------------------------------------------------------- | ---------- |
| **title**           | **str**                                                          | A human readable documentation for the problem type                     |
| **status**          | **int**                                                          | The HTTP status code                                                    |
| **detail**          | **str**                                                          | A human readable explanation specific to this occurrence of the problem | [optional] |
| **type**            | **str**                                                          | An absolute URI that identifies the problem type                        | [optional] |
| **any string name** | **bool, date, datetime, dict, float, int, list, str, none_type** | any string name can be used but the value must be the correct type      | [optional] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
