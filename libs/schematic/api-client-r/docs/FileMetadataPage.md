# openapi::FileMetadataPage

A page of file metadata.

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**number** | **integer** | The page number. | 
**size** | **integer** | The number of items in a single page. | 
**totalElements** | **integer** | Total number of elements in the result set. | 
**totalPages** | **integer** | Total number of pages in the result set. | 
**hasNext** | **character** | Returns if there is a next page. | 
**hasPrevious** | **character** | Returns if there is a previous page. | 
**files** | [**array[FileMetadata]**](FileMetadata.md) | A list of file metadata. | 


