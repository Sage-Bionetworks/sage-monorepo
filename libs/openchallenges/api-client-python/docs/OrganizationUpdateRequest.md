# OrganizationUpdateRequest

The information required to update an org account

## Properties

| Name            | Type    | Description                                                                                                                                  | Notes |
| --------------- | ------- | -------------------------------------------------------------------------------------------------------------------------------------------- | ----- |
| **name**        | **str** |                                                                                                                                              |
| **description** | **str** |                                                                                                                                              |
| **avatar_key**  | **str** |                                                                                                                                              |
| **website_url** | **str** | A URL to the website or image.                                                                                                               |
| **short_name**  | **str** | The abbreviation, which may be an acronym, initialism, or other short form (e.g., \&quot;AI\&quot;, \&quot;WashU\&quot;, \&quot;etc.\&quot;) |

## Example

```python
from openchallenges_api_client.models.organization_update_request import OrganizationUpdateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of OrganizationUpdateRequest from a JSON string
organization_update_request_instance = OrganizationUpdateRequest.from_json(json)
# print the JSON string representation of the object
print(OrganizationUpdateRequest.to_json())

# convert the object into a dict
organization_update_request_dict = organization_update_request_instance.to_dict()
# create an instance of OrganizationUpdateRequest from a dict
organization_update_request_from_dict = OrganizationUpdateRequest.from_dict(organization_update_request_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
