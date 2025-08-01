# openchallenges-api-client-python
Discover, explore, and contribute to open biomedical challenges.

This Python package is automatically generated by the [OpenAPI Generator](https://openapi-generator.tech) project:

- API version: 1.0.0
- Package version: 1.0.0
- Generator version: 7.14.0
- Build package: org.openapitools.codegen.languages.PythonClientCodegen
For more information, please visit [https://github.com/Sage-Bionetworks/sage-monorepo](https://github.com/Sage-Bionetworks/sage-monorepo)

## Requirements.

Python 3.9+

## Installation & Usage
### pip install

If the python package is hosted on a repository, you can install directly using:

```sh
pip install git+https://github.com/GIT_USER_ID/GIT_REPO_ID.git
```
(you may need to run `pip` with root permission: `sudo pip install git+https://github.com/GIT_USER_ID/GIT_REPO_ID.git`)

Then import the package:
```python
import openchallenges_api_client_python
```

### Setuptools

Install via [Setuptools](http://pypi.python.org/pypi/setuptools).

```sh
python setup.py install --user
```
(or `sudo python setup.py install` to install the package for all users)

Then import the package:
```python
import openchallenges_api_client_python
```

### Tests

Execute `pytest` to run the tests.

## Getting Started

Please follow the [installation procedure](#installation--usage) and then run the following:

```python

import openchallenges_api_client_python
from openchallenges_api_client_python.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to https://openchallenges.io/api/v1
# See configuration.py for a list of all supported configuration parameters.
configuration = openchallenges_api_client_python.Configuration(
    host = "https://openchallenges.io/api/v1"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (api_key): apiBearerAuth
configuration = openchallenges_api_client_python.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)


# Enter a context with an instance of the API client
with openchallenges_api_client_python.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openchallenges_api_client_python.APIKeyApi(api_client)
    create_api_key_request = openchallenges_api_client_python.CreateApiKeyRequest() # CreateApiKeyRequest | 

    try:
        # Create API key
        api_response = api_instance.create_api_key(create_api_key_request)
        print("The response of APIKeyApi->create_api_key:\n")
        pprint(api_response)
    except ApiException as e:
        print("Exception when calling APIKeyApi->create_api_key: %s\n" % e)

```

## Documentation for API Endpoints

All URIs are relative to *https://openchallenges.io/api/v1*

Class | Method | HTTP request | Description
------------ | ------------- | ------------- | -------------
*APIKeyApi* | [**create_api_key**](docs/APIKeyApi.md#create_api_key) | **POST** /auth/api-keys | Create API key
*APIKeyApi* | [**delete_api_key**](docs/APIKeyApi.md#delete_api_key) | **DELETE** /auth/api-keys/{keyId} | Delete API key
*APIKeyApi* | [**list_api_keys**](docs/APIKeyApi.md#list_api_keys) | **GET** /auth/api-keys | List API keys
*AuthenticationApi* | [**login**](docs/AuthenticationApi.md#login) | **POST** /auth/login | User login
*ChallengeApi* | [**create_challenge**](docs/ChallengeApi.md#create_challenge) | **POST** /challenges | Create a challenge
*ChallengeApi* | [**delete_challenge**](docs/ChallengeApi.md#delete_challenge) | **DELETE** /challenges/{challengeId} | Delete a challenge
*ChallengeApi* | [**get_challenge**](docs/ChallengeApi.md#get_challenge) | **GET** /challenges/{challengeId} | Get a challenge
*ChallengeApi* | [**get_challenge_json_ld**](docs/ChallengeApi.md#get_challenge_json_ld) | **GET** /challenges/{challengeId}/json-ld | Get a challenge in JSON-LD format
*ChallengeApi* | [**list_challenges**](docs/ChallengeApi.md#list_challenges) | **GET** /challenges | List challenges
*ChallengeApi* | [**update_challenge**](docs/ChallengeApi.md#update_challenge) | **PUT** /challenges/{challengeId} | Update an existing challenge
*ChallengeAnalyticsApi* | [**get_challenges_per_year**](docs/ChallengeAnalyticsApi.md#get_challenges_per_year) | **GET** /challenge-analytics/challenges-per-year | Get the number of challenges tracked per year
*ChallengeContributionApi* | [**create_challenge_contribution**](docs/ChallengeContributionApi.md#create_challenge_contribution) | **POST** /challenges/{challengeId}/contributions | Create a new contribution for a challenge
*ChallengeContributionApi* | [**delete_challenge_contribution**](docs/ChallengeContributionApi.md#delete_challenge_contribution) | **DELETE** /challenges/{challengeId}/contributions/{organizationId}/role/{role} | Delete a specific challenge contribution
*ChallengeContributionApi* | [**get_challenge_contribution**](docs/ChallengeContributionApi.md#get_challenge_contribution) | **GET** /challenges/{challengeId}/contributions/{organizationId}/role/{role} | Get a specific challenge contribution
*ChallengeContributionApi* | [**list_challenge_contributions**](docs/ChallengeContributionApi.md#list_challenge_contributions) | **GET** /challenges/{challengeId}/contributions | List challenge contributions
*ChallengePlatformApi* | [**create_challenge_platform**](docs/ChallengePlatformApi.md#create_challenge_platform) | **POST** /challenge-platforms | Create a challenge platform
*ChallengePlatformApi* | [**delete_challenge_platform**](docs/ChallengePlatformApi.md#delete_challenge_platform) | **DELETE** /challenge-platforms/{challengePlatformId} | Delete a challenge platform
*ChallengePlatformApi* | [**get_challenge_platform**](docs/ChallengePlatformApi.md#get_challenge_platform) | **GET** /challenge-platforms/{challengePlatformId} | Get a challenge platform
*ChallengePlatformApi* | [**list_challenge_platforms**](docs/ChallengePlatformApi.md#list_challenge_platforms) | **GET** /challenge-platforms | List challenge platforms
*ChallengePlatformApi* | [**update_challenge_platform**](docs/ChallengePlatformApi.md#update_challenge_platform) | **PUT** /challenge-platforms/{challengePlatformId} | Update an existing challenge platform
*EdamConceptApi* | [**list_edam_concepts**](docs/EdamConceptApi.md#list_edam_concepts) | **GET** /edam-concepts | List EDAM concepts
*ImageApi* | [**get_image**](docs/ImageApi.md#get_image) | **GET** /images | Get an image
*OrganizationApi* | [**create_organization**](docs/OrganizationApi.md#create_organization) | **POST** /organizations | Create an organization
*OrganizationApi* | [**delete_organization**](docs/OrganizationApi.md#delete_organization) | **DELETE** /organizations/{org} | Delete an organization
*OrganizationApi* | [**get_organization**](docs/OrganizationApi.md#get_organization) | **GET** /organizations/{org} | Get an organization
*OrganizationApi* | [**list_organizations**](docs/OrganizationApi.md#list_organizations) | **GET** /organizations | List organizations
*OrganizationApi* | [**update_organization**](docs/OrganizationApi.md#update_organization) | **PUT** /organizations/{org} | Update an existing organization


## Documentation For Models

 - [ApiKey](docs/ApiKey.md)
 - [BasicError](docs/BasicError.md)
 - [Challenge](docs/Challenge.md)
 - [ChallengeCategory](docs/ChallengeCategory.md)
 - [ChallengeContribution](docs/ChallengeContribution.md)
 - [ChallengeContributionCreateRequest](docs/ChallengeContributionCreateRequest.md)
 - [ChallengeContributionRole](docs/ChallengeContributionRole.md)
 - [ChallengeContributionsPage](docs/ChallengeContributionsPage.md)
 - [ChallengeCreateRequest](docs/ChallengeCreateRequest.md)
 - [ChallengeDirection](docs/ChallengeDirection.md)
 - [ChallengeIncentive](docs/ChallengeIncentive.md)
 - [ChallengeJsonLd](docs/ChallengeJsonLd.md)
 - [ChallengeParticipationRole](docs/ChallengeParticipationRole.md)
 - [ChallengePlatform](docs/ChallengePlatform.md)
 - [ChallengePlatformCreateRequest](docs/ChallengePlatformCreateRequest.md)
 - [ChallengePlatformDirection](docs/ChallengePlatformDirection.md)
 - [ChallengePlatformSearchQuery](docs/ChallengePlatformSearchQuery.md)
 - [ChallengePlatformSort](docs/ChallengePlatformSort.md)
 - [ChallengePlatformUpdateRequest](docs/ChallengePlatformUpdateRequest.md)
 - [ChallengePlatformsPage](docs/ChallengePlatformsPage.md)
 - [ChallengeSearchQuery](docs/ChallengeSearchQuery.md)
 - [ChallengeSort](docs/ChallengeSort.md)
 - [ChallengeStatus](docs/ChallengeStatus.md)
 - [ChallengeSubmissionType](docs/ChallengeSubmissionType.md)
 - [ChallengeUpdateRequest](docs/ChallengeUpdateRequest.md)
 - [ChallengesPage](docs/ChallengesPage.md)
 - [ChallengesPerYear](docs/ChallengesPerYear.md)
 - [CreateApiKeyRequest](docs/CreateApiKeyRequest.md)
 - [CreateApiKeyResponse](docs/CreateApiKeyResponse.md)
 - [EdamConcept](docs/EdamConcept.md)
 - [EdamConceptDirection](docs/EdamConceptDirection.md)
 - [EdamConceptSearchQuery](docs/EdamConceptSearchQuery.md)
 - [EdamConceptSort](docs/EdamConceptSort.md)
 - [EdamConceptsPage](docs/EdamConceptsPage.md)
 - [EdamSection](docs/EdamSection.md)
 - [Image](docs/Image.md)
 - [ImageAspectRatio](docs/ImageAspectRatio.md)
 - [ImageHeight](docs/ImageHeight.md)
 - [ImageQuery](docs/ImageQuery.md)
 - [LoginRequest](docs/LoginRequest.md)
 - [LoginResponse](docs/LoginResponse.md)
 - [Organization](docs/Organization.md)
 - [OrganizationCategory](docs/OrganizationCategory.md)
 - [OrganizationCreateRequest](docs/OrganizationCreateRequest.md)
 - [OrganizationDirection](docs/OrganizationDirection.md)
 - [OrganizationSearchQuery](docs/OrganizationSearchQuery.md)
 - [OrganizationSort](docs/OrganizationSort.md)
 - [OrganizationUpdateRequest](docs/OrganizationUpdateRequest.md)
 - [OrganizationsPage](docs/OrganizationsPage.md)
 - [PageMetadata](docs/PageMetadata.md)
 - [SimpleChallengePlatform](docs/SimpleChallengePlatform.md)


<a id="documentation-for-authorization"></a>
## Documentation For Authorization


Authentication schemes defined for the API:
<a id="apiBearerAuth"></a>
### apiBearerAuth

- **Type**: Bearer authentication (api_key)


## Author




