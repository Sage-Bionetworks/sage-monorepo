# UsersPage

A page of users

## Properties

| Name               | Type                      | Description                                 | Notes |
| ------------------ | ------------------------- | ------------------------------------------- | ----- |
| **number**         | **int**                   | The page number.                            |
| **size**           | **int**                   | The number of items in a single page.       |
| **total_elements** | **int**                   | Total number of elements in the result set. |
| **total_pages**    | **int**                   | Total number of pages in the result set.    |
| **has_next**       | **bool**                  | Returns if there is a next page.            |
| **has_previous**   | **bool**                  | Returns if there is a previous page.        |
| **users**          | [**List[User]**](User.md) | A list of users                             |

## Example

```python
from openchallenges.models.users_page import UsersPage

# TODO update the JSON string below
json = "{}"
# create an instance of UsersPage from a JSON string
users_page_instance = UsersPage.from_json(json)
# print the JSON string representation of the object
print(UsersPage.to_json())

# convert the object into a dict
users_page_dict = users_page_instance.to_dict()
# create an instance of UsersPage from a dict
users_page_from_dict = UsersPage.from_dict(users_page_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
