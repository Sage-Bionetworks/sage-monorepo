# ChallengePlatformsPage

A page of challenge platforms.

## Properties

| Name                    | Type                                                | Description                                 | Notes |
| ----------------------- | --------------------------------------------------- | ------------------------------------------- | ----- |
| **number**              | **int**                                             | The page number.                            |
| **size**                | **int**                                             | The number of items in a single page.       |
| **total_elements**      | **int**                                             | Total number of elements in the result set. |
| **total_pages**         | **int**                                             | Total number of pages in the result set.    |
| **has_next**            | **bool**                                            | Returns if there is a next page.            |
| **has_previous**        | **bool**                                            | Returns if there is a previous page.        |
| **challenge_platforms** | [**List[ChallengePlatform]**](ChallengePlatform.md) | A list of challenge platforms.              |

## Example

```python
from openchallenges_api_client_python.models.challenge_platforms_page import ChallengePlatformsPage

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengePlatformsPage from a JSON string
challenge_platforms_page_instance = ChallengePlatformsPage.from_json(json)
# print the JSON string representation of the object
print(ChallengePlatformsPage.to_json())

# convert the object into a dict
challenge_platforms_page_dict = challenge_platforms_page_instance.to_dict()
# create an instance of ChallengePlatformsPage from a dict
challenge_platforms_page_from_dict = ChallengePlatformsPage.from_dict(challenge_platforms_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
