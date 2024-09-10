# openapi::ChallengePlatformSearchQuery

A challenge platform search query.

## Properties

| Name            | Type                                                            | Description                                          | Notes                                |
| --------------- | --------------------------------------------------------------- | ---------------------------------------------------- | ------------------------------------ |
| **pageNumber**  | **integer**                                                     | The page number.                                     | [optional] [default to 0] [Min: 0]   |
| **pageSize**    | **integer**                                                     | The number of items in a single page.                | [optional] [default to 100] [Min: 1] |
| **sort**        | [**ChallengePlatformSort**](ChallengePlatformSort.md)           |                                                      | [optional] [Enum: ]                  |
| **direction**   | [**ChallengePlatformDirection**](ChallengePlatformDirection.md) |                                                      | [optional] [Enum: ]                  |
| **searchTerms** | **character**                                                   | A string of search terms used to filter the results. | [optional]                           |
