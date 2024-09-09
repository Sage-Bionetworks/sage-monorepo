# openapi::Challenge

A challenge

## Properties

| Name                | Type                                                                       | Description                                                  | Notes                                                                     |
| ------------------- | -------------------------------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------------------- |
| **id**              | **integer**                                                                | The unique identifier of the challenge.                      |
| **slug**            | **character**                                                              | The slug of the challenge.                                   | [Pattern: ^[a-z0-9]+(?:-[a-z0-9]+)\*$] [Max. length: 60] [Min. length: 3] |
| **name**            | **character**                                                              | The name of the challenge.                                   | [Max. length: 60] [Min. length: 3]                                        |
| **headline**        | **character**                                                              | The headline of the challenge.                               | [optional] [Max. length: 80] [Min. length: 0]                             |
| **description**     | **character**                                                              | The description of the challenge.                            | [Max. length: 280] [Min. length: 0]                                       |
| **doi**             | **character**                                                              |                                                              | [optional]                                                                |
| **status**          | [**ChallengeStatus**](ChallengeStatus.md)                                  |                                                              | [Enum: ]                                                                  |
| **difficulty**      | [**ChallengeDifficulty**](ChallengeDifficulty.md)                          |                                                              | [Enum: ]                                                                  |
| **platform**        | [**SimpleChallengePlatform**](SimpleChallengePlatform.md)                  |                                                              |
| **websiteUrl**      | **character**                                                              |                                                              | [optional]                                                                |
| **avatarUrl**       | **character**                                                              |                                                              | [optional]                                                                |
| **incentives**      | [**array[ChallengeIncentive]**](ChallengeIncentive.md)                     |                                                              |
| **submissionTypes** | [**array[ChallengeSubmissionType]**](ChallengeSubmissionType.md)           |                                                              |
| **inputDataTypes**  | [**array[SimpleChallengeInputDataType]**](SimpleChallengeInputDataType.md) |                                                              | [optional]                                                                |
| **startDate**       | **character**                                                              | The start date of the challenge.                             | [optional]                                                                |
| **endDate**         | **character**                                                              | The end date of the challenge.                               | [optional]                                                                |
| **starredCount**    | **integer**                                                                | The number of times the challenge has been starred by users. | [default to 0]                                                            |
| **createdAt**       | **character**                                                              |                                                              |
| **updatedAt**       | **character**                                                              |                                                              |
