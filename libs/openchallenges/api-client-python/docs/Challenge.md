# Challenge

A challenge

## Properties

| Name                 | Type                                                                      | Description                                                  | Notes          |
| -------------------- | ------------------------------------------------------------------------- | ------------------------------------------------------------ | -------------- |
| **id**               | **int**                                                                   | The unique identifier of the challenge.                      |
| **slug**             | **str**                                                                   | The slug of the challenge.                                   |
| **name**             | **str**                                                                   | The name of the challenge.                                   |
| **headline**         | **str**                                                                   | The headline of the challenge.                               | [optional]     |
| **description**      | **str**                                                                   | The description of the challenge.                            |
| **doi**              | **str**                                                                   |                                                              | [optional]     |
| **status**           | [**ChallengeStatus**](ChallengeStatus.md)                                 |                                                              |
| **difficulty**       | [**ChallengeDifficulty**](ChallengeDifficulty.md)                         |                                                              |
| **platform**         | [**SimpleChallengePlatform**](SimpleChallengePlatform.md)                 |                                                              |
| **website_url**      | **str**                                                                   |                                                              | [optional]     |
| **avatar_url**       | **str**                                                                   |                                                              | [optional]     |
| **incentives**       | [**List[ChallengeIncentive]**](ChallengeIncentive.md)                     |                                                              |
| **submission_types** | [**List[ChallengeSubmissionType]**](ChallengeSubmissionType.md)           |                                                              |
| **input_data_types** | [**List[SimpleChallengeInputDataType]**](SimpleChallengeInputDataType.md) |                                                              | [optional]     |
| **start_date**       | **date**                                                                  | The start date of the challenge.                             | [optional]     |
| **end_date**         | **date**                                                                  | The end date of the challenge.                               | [optional]     |
| **starred_count**    | **int**                                                                   | The number of times the challenge has been starred by users. | [default to 0] |
| **created_at**       | **datetime**                                                              |                                                              |
| **updated_at**       | **datetime**                                                              |                                                              |

## Example

```python
from openchallenges_client.models.challenge import Challenge

# TODO update the JSON string below
json = "{}"
# create an instance of Challenge from a JSON string
challenge_instance = Challenge.from_json(json)
# print the JSON string representation of the object
print Challenge.to_json()

# convert the object into a dict
challenge_dict = challenge_instance.to_dict()
# create an instance of Challenge from a dict
challenge_form_dict = challenge.from_dict(challenge_dict)
```

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)
