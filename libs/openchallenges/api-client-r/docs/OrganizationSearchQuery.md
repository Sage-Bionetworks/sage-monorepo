# openapi::OrganizationSearchQuery

An organization search query.

## Properties

| Name                           | Type                                                                 | Description                                                          | Notes                                |
| ------------------------------ | -------------------------------------------------------------------- | -------------------------------------------------------------------- | ------------------------------------ |
| **pageNumber**                 | **integer**                                                          | The page number.                                                     | [optional] [default to 0] [Min: 0]   |
| **pageSize**                   | **integer**                                                          | The number of items in a single page.                                | [optional] [default to 100] [Min: 1] |
| **categories**                 | [**array[OrganizationCategory]**](OrganizationCategory.md)           | The array of organization categories used to filter the results.     | [optional]                           |
| **challengeContributionRoles** | [**array[ChallengeContributionRole]**](ChallengeContributionRole.md) | An array of challenge contribution roles used to filter the results. | [optional]                           |
| **sort**                       | [**OrganizationSort**](OrganizationSort.md)                          |                                                                      | [optional] [Enum: ]                  |
| **direction**                  | [**OrganizationDirection**](OrganizationDirection.md)                |                                                                      | [optional] [Enum: ]                  |
| **searchTerms**                | **character**                                                        | A string of search terms used to filter the results.                 | [optional]                           |
