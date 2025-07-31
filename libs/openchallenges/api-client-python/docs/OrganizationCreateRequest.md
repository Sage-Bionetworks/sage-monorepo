# OrganizationCreateRequest

The information required to create an org account

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**login** | **str** | The unique login of an organization. | 
**name** | **str** |  | 
**description** | **str** |  | [optional] 
**avatar_key** | **str** |  | [optional] 
**website_url** | **str** | A URL to the website or image. | 
**acronym** | **str** | An acronym of the organization. | [optional] 

## Example

```python
from openchallenges_api_client_python.models.organization_create_request import OrganizationCreateRequest

# TODO update the JSON string below
json = "{}"
# create an instance of OrganizationCreateRequest from a JSON string
organization_create_request_instance = OrganizationCreateRequest.from_json(json)
# print the JSON string representation of the object
print(OrganizationCreateRequest.to_json())

# convert the object into a dict
organization_create_request_dict = organization_create_request_instance.to_dict()
# create an instance of OrganizationCreateRequest from a dict
organization_create_request_from_dict = OrganizationCreateRequest.from_dict(organization_create_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


