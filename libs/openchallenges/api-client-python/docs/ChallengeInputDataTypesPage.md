# ChallengeInputDataTypesPage

A page of challenge input data types.

## Properties

| Name                           | Type                                                          | Description                                 | Notes |
| ------------------------------ | ------------------------------------------------------------- | ------------------------------------------- | ----- |
| **number**                     | **int**                                                       | The page number.                            |
| **size**                       | **int**                                                       | The number of items in a single page.       |
| **total_elements**             | **int**                                                       | Total number of elements in the result set. |
| **total_pages**                | **int**                                                       | Total number of pages in the result set.    |
| **has_next**                   | **bool**                                                      | Returns if there is a next page.            |
| **has_previous**               | **bool**                                                      | Returns if there is a previous page.        |
| **challenge_input_data_types** | [**List[ChallengeInputDataType]**](ChallengeInputDataType.md) | A list of challenge input data types.       |

## Example

```python
from openchallenges_client.models.challenge_input_data_types_page import ChallengeInputDataTypesPage

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeInputDataTypesPage from a JSON string
challenge_input_data_types_page_instance = ChallengeInputDataTypesPage.from_json(json)
# print the JSON string representation of the object
print ChallengeInputDataTypesPage.to_json()

# convert the object into a dict
challenge_input_data_types_page_dict = challenge_input_data_types_page_instance.to_dict()
# create an instance of ChallengeInputDataTypesPage from a dict
challenge_input_data_types_page_form_dict = challenge_input_data_types_page.from_dict(challenge_input_data_types_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
