# ChallengeContributionsPage

A page of challenge challenge contributions.

## Properties

| Name                        | Type                                                        | Description                                 | Notes |
| --------------------------- | ----------------------------------------------------------- | ------------------------------------------- | ----- |
| **number**                  | **int**                                                     | The page number.                            |
| **size**                    | **int**                                                     | The number of items in a single page.       |
| **total_elements**          | **int**                                                     | Total number of elements in the result set. |
| **total_pages**             | **int**                                                     | Total number of pages in the result set.    |
| **has_next**                | **bool**                                                    | Returns if there is a next page.            |
| **has_previous**            | **bool**                                                    | Returns if there is a previous page.        |
| **challenge_contributions** | [**List[ChallengeContribution]**](ChallengeContribution.md) | A list of challenge contributions.          |

## Example

```python
from openchallenges_api_client_python.models.challenge_contributions_page import ChallengeContributionsPage

# TODO update the JSON string below
json = "{}"
# create an instance of ChallengeContributionsPage from a JSON string
challenge_contributions_page_instance = ChallengeContributionsPage.from_json(json)
# print the JSON string representation of the object
print(ChallengeContributionsPage.to_json())

# convert the object into a dict
challenge_contributions_page_dict = challenge_contributions_page_instance.to_dict()
# create an instance of ChallengeContributionsPage from a dict
challenge_contributions_page_from_dict = ChallengeContributionsPage.from_dict(challenge_contributions_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
