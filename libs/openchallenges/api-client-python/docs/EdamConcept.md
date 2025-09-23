# EdamConcept

The EDAM concept.

## Properties

| Name                | Type    | Description                                | Notes |
| ------------------- | ------- | ------------------------------------------ | ----- |
| **id**              | **int** | The unique identifier of the EDAM concept. |
| **class_id**        | **str** |                                            |
| **preferred_label** | **str** |                                            |

## Example

```python
from openchallenges_api_client.models.edam_concept import EdamConcept

# TODO update the JSON string below
json = "{}"
# create an instance of EdamConcept from a JSON string
edam_concept_instance = EdamConcept.from_json(json)
# print the JSON string representation of the object
print(EdamConcept.to_json())

# convert the object into a dict
edam_concept_dict = edam_concept_instance.to_dict()
# create an instance of EdamConcept from a dict
edam_concept_from_dict = EdamConcept.from_dict(edam_concept_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
