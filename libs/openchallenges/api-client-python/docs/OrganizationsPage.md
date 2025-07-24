# OrganizationsPage

A page of organizations

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**number** | **int** | The page number. | 
**size** | **int** | The number of items in a single page. | 
**total_elements** | **int** | Total number of elements in the result set. | 
**total_pages** | **int** | Total number of pages in the result set. | 
**has_next** | **bool** | Returns if there is a next page. | 
**has_previous** | **bool** | Returns if there is a previous page. | 
**organizations** | [**List[Organization]**](Organization.md) | A list of organizations | 

## Example

```python
from openchallenges_api_client_python.models.organizations_page import OrganizationsPage

# TODO update the JSON string below
json = "{}"
# create an instance of OrganizationsPage from a JSON string
organizations_page_instance = OrganizationsPage.from_json(json)
# print the JSON string representation of the object
print(OrganizationsPage.to_json())

# convert the object into a dict
organizations_page_dict = organizations_page_instance.to_dict()
# create an instance of OrganizationsPage from a dict
organizations_page_from_dict = OrganizationsPage.from_dict(organizations_page_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


