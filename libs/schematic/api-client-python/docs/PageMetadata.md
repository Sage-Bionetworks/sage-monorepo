# PageMetadata

The metadata of a page.

## Properties

| Name                | Type                                                             | Description                                                        | Notes      |
| ------------------- | ---------------------------------------------------------------- | ------------------------------------------------------------------ | ---------- |
| **number**          | **int**                                                          | The page number.                                                   |
| **size**            | **int**                                                          | The number of items in a single page.                              |
| **total_elements**  | **int**                                                          | Total number of elements in the result set.                        |
| **total_pages**     | **int**                                                          | Total number of pages in the result set.                           |
| **has_next**        | **bool**                                                         | Returns if there is a next page.                                   |
| **has_previous**    | **bool**                                                         | Returns if there is a previous page.                               |
| **any string name** | **bool, date, datetime, dict, float, int, list, str, none_type** | any string name can be used but the value must be the correct type | [optional] |

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
