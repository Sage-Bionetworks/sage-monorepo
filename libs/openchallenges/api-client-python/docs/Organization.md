# Organization

An organization

## Properties

| Name                | Type         | Description                              | Notes      |
| ------------------- | ------------ | ---------------------------------------- | ---------- |
| **id**              | **int**      | The unique identifier of an organization |
| **name**            | **str**      |                                          |
| **email**           | **str**      | An email address.                        |
| **login**           | **str**      | The login of an organization             |
| **description**     | **str**      |                                          |
| **avatar_key**      | **str**      |                                          | [optional] |
| **website_url**     | **str**      |                                          |
| **challenge_count** | **int**      |                                          | [optional] |
| **created_at**      | **datetime** |                                          |
| **updated_at**      | **datetime** |                                          |
| **acronym**         | **str**      |                                          | [optional] |

## Example

```python
from openchallenges_client.models.organization import Organization

# TODO update the JSON string below
json = "{}"
# create an instance of Organization from a JSON string
organization_instance = Organization.from_json(json)
# print the JSON string representation of the object
print Organization.to_json()

# convert the object into a dict
organization_dict = organization_instance.to_dict()
# create an instance of Organization from a dict
organization_form_dict = organization.from_dict(organization_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
