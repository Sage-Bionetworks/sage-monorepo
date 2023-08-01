# openapi::ChallengeSearchQuery

A challenge search query.

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**pageNumber** | **integer** | The page number. | [optional] [default to 0] [Min: 0] 
**pageSize** | **integer** | The number of items in a single page. | [optional] [default to 100] [Min: 1] 
**sort** | [**ChallengeSort**](ChallengeSort.md) |  | [optional] [Enum: ] 
**direction** | [**ChallengeDirection**](ChallengeDirection.md) |  | [optional] [Enum: ] 
**difficulties** | [**array[ChallengeDifficulty]**](ChallengeDifficulty.md) | An array of challenge difficulty levels used to filter the results. | [optional] 
**incentives** | [**array[ChallengeIncentive]**](ChallengeIncentive.md) | An array of challenge incentive types used to filter the results. | [optional] 
**minStartDate** | **character** | Keep the challenges that start at this date or later. | [optional] 
**maxStartDate** | **character** | Keep the challenges that start at this date or sooner. | [optional] 
**platforms** | **array[character]** | An array of challenge platform ids used to filter the results. | [optional] 
**organizations** | **array[integer]** | An array of organization ids used to filter the results. | [optional] 
**inputDataTypes** | **array[character]** | An array of challenge input data type ids used to filter the results. | [optional] 
**status** | [**array[ChallengeStatus]**](ChallengeStatus.md) | An array of challenge status used to filter the results. | [optional] 
**submissionTypes** | [**array[ChallengeSubmissionType]**](ChallengeSubmissionType.md) | An array of challenge submission types used to filter the results. | [optional] 
**searchTerms** | **character** | A string of search terms used to filter the results. | [optional] 


