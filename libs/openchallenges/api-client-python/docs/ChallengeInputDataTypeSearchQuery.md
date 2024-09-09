# ChallengeInputDataTypeSearchQuery

A challenge input data type search query.

## Properties

| Name             | Type                                                                      | Description                                          | Notes                       |
| ---------------- | ------------------------------------------------------------------------- | ---------------------------------------------------- | --------------------------- |
| **page_number**  | **int**                                                                   | The page number.                                     | [optional] [default to 0]   |
| **page_size**    | **int**                                                                   | The number of items in a single page.                | [optional] [default to 100] |
| **sort**         | [**ChallengeInputDataTypeSort**](ChallengeInputDataTypeSort.md)           |                                                      | [optional]                  |
| **direction**    | [**ChallengeInputDataTypeDirection**](ChallengeInputDataTypeDirection.md) |                                                      | [optional]                  |
| **search_terms** | **str**                                                                   | A string of search terms used to filter the results. | [optional]                  |

## Example

```python
from openchallenges_client.models.challenge_input_data_type_search_query import ChallengeInputDataTypeSearchQuery

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeInputDataTypeSearchQuery from a JSON string
challenge_input_data_type_search_query_instance = ChallengeInputDataTypeSearchQuery.from_json(json)
# print the JSON string representation of the object
print ChallengeInputDataTypeSearchQuery.to_json()

# convert the object into a dict
challenge_input_data_type_search_query_dict = challenge_input_data_type_search_query_instance.to_dict()
# create an instance of ChallengeInputDataTypeSearchQuery from a dict
challenge_input_data_type_search_query_form_dict = challenge_input_data_type_search_query.from_dict(challenge_input_data_type_search_query_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
