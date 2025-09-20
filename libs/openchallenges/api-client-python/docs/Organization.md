# Organization

An organization

## Properties

| Name                | Type         | Description                                                                                                                                  | Notes          |
| ------------------- | ------------ | -------------------------------------------------------------------------------------------------------------------------------------------- | -------------- |
| **id**              | **int**      | The unique identifier of an organization                                                                                                     |
| **name**            | **str**      | The name of the organization.                                                                                                                |
| **login**           | **str**      | The unique login of an organization.                                                                                                         |
| **description**     | **str**      | A description of the organization.                                                                                                           | [optional]     |
| **avatar_key**      | **str**      |                                                                                                                                              | [optional]     |
| **website_url**     | **str**      | A URL to the website or image.                                                                                                               |
| **challenge_count** | **int**      | The number of challenges involving this organization.                                                                                        | [default to 0] |
| **short_name**      | **str**      | The abbreviation, which may be an acronym, initialism, or other short form (e.g., \&quot;AI\&quot;, \&quot;WashU\&quot;, \&quot;etc.\&quot;) | [optional]     |
| **created_at**      | **datetime** | Datetime when the object was added to the database.                                                                                          |
| **updated_at**      | **datetime** | Datetime when the object was last modified in the database.                                                                                  |

## Example

```python
from openchallenges_api_client_python.models.organization import Organization

# TODO update the JSON string below
json = "{}"
# create an instance of Organization from a JSON string
organization_instance = Organization.from_json(json)
# print the JSON string representation of the object
print(Organization.to_json())

# convert the object into a dict
organization_dict = organization_instance.to_dict()
# create an instance of Organization from a dict
organization_from_dict = Organization.from_dict(organization_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
