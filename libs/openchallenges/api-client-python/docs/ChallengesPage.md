# ChallengesPage

A page of challenges.

## Properties

| Name               | Type                                | Description                                 | Notes |
| ------------------ | ----------------------------------- | ------------------------------------------- | ----- |
| **number**         | **int**                             | The page number.                            |
| **size**           | **int**                             | The number of items in a single page.       |
| **total_elements** | **int**                             | Total number of elements in the result set. |
| **total_pages**    | **int**                             | Total number of pages in the result set.    |
| **has_next**       | **bool**                            | Returns if there is a next page.            |
| **has_previous**   | **bool**                            | Returns if there is a previous page.        |
| **challenges**     | [**List[Challenge]**](Challenge.md) | A list of challenges.                       |

## Example

```python
from openchallenges_api_client.models.challenges_page import ChallengesPage

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengesPage from a JSON string
challenges_page_instance = ChallengesPage.from_json(json)
# print the JSON string representation of the object
print(ChallengesPage.to_json())

# convert the object into a dict
challenges_page_dict = challenges_page_instance.to_dict()
# create an instance of ChallengesPage from a dict
challenges_page_from_dict = ChallengesPage.from_dict(challenges_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
