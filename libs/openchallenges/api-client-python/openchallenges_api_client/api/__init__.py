# flake8: noqa

if __import__("typing").TYPE_CHECKING:
    # import apis into api package
    from openchallenges_api_client.api.api_key_api import APIKeyApi
    from openchallenges_api_client.api.authentication_api import AuthenticationApi
    from openchallenges_api_client.api.challenge_api import ChallengeApi
    from openchallenges_api_client.api.challenge_analytics_api import (
        ChallengeAnalyticsApi,
    )
    from openchallenges_api_client.api.challenge_contribution_api import (
        ChallengeContributionApi,
    )
    from openchallenges_api_client.api.challenge_platform_api import (
        ChallengePlatformApi,
    )
    from openchallenges_api_client.api.edam_concept_api import EdamConceptApi
    from openchallenges_api_client.api.image_api import ImageApi
    from openchallenges_api_client.api.organization_api import OrganizationApi

else:
    from lazy_imports import LazyModule, as_package, load

    load(
        LazyModule(
            *as_package(__file__),
            """# import apis into api package
from openchallenges_api_client.api.api_key_api import APIKeyApi
from openchallenges_api_client.api.authentication_api import AuthenticationApi
from openchallenges_api_client.api.challenge_api import ChallengeApi
from openchallenges_api_client.api.challenge_analytics_api import ChallengeAnalyticsApi
from openchallenges_api_client.api.challenge_contribution_api import ChallengeContributionApi
from openchallenges_api_client.api.challenge_platform_api import ChallengePlatformApi
from openchallenges_api_client.api.edam_concept_api import EdamConceptApi
from openchallenges_api_client.api.image_api import ImageApi
from openchallenges_api_client.api.organization_api import OrganizationApi

""",
            name=__name__,
            doc=__doc__,
        )
    )
