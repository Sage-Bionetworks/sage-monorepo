import connexion
import six

from openapi_server.models.array_of_topics import ArrayOfTopics  # noqa: E501
from openapi_server.models.challenge import Challenge  # noqa: E501
from openapi_server.models.challenge_create_request import ChallengeCreateRequest  # noqa: E501
from openapi_server.models.challenge_create_response import ChallengeCreateResponse  # noqa: E501
from openapi_server.models.challenge_difficulty import ChallengeDifficulty  # noqa: E501
from openapi_server.models.challenge_incentive_type import ChallengeIncentiveType  # noqa: E501
from openapi_server.models.challenge_organizer_create_request import ChallengeOrganizerCreateRequest  # noqa: E501
from openapi_server.models.challenge_organizer_create_response import ChallengeOrganizerCreateResponse  # noqa: E501
from openapi_server.models.challenge_organizer_list import ChallengeOrganizerList  # noqa: E501
from openapi_server.models.challenge_readme import ChallengeReadme  # noqa: E501
from openapi_server.models.challenge_readme_update_request import ChallengeReadmeUpdateRequest  # noqa: E501
from openapi_server.models.challenge_sponsor_create_request import ChallengeSponsorCreateRequest  # noqa: E501
from openapi_server.models.challenge_sponsor_create_response import ChallengeSponsorCreateResponse  # noqa: E501
from openapi_server.models.challenge_sponsor_list import ChallengeSponsorList  # noqa: E501
from openapi_server.models.challenge_status import ChallengeStatus  # noqa: E501
from openapi_server.models.challenge_submission_type import ChallengeSubmissionType  # noqa: E501
from openapi_server.models.date_range import DateRange  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.page_of_challenges import PageOfChallenges  # noqa: E501
from openapi_server.models.page_of_users import PageOfUsers  # noqa: E501
from openapi_server import util


def create_challenge(account_name, challenge_create_request):  # noqa: E501
    """Add a challenge

    Adds a challenge # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_create_request: 
    :type challenge_create_request: dict | bytes

    :rtype: ChallengeCreateResponse
    """
    if connexion.request.is_json:
        challenge_create_request = ChallengeCreateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def create_challenge_organizer(account_name, challenge_name, challenge_organizer_create_request):  # noqa: E501
    """Create a challenge organizer

    Create a challenge organizer # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str
    :param challenge_organizer_create_request: 
    :type challenge_organizer_create_request: dict | bytes

    :rtype: ChallengeOrganizerCreateResponse
    """
    if connexion.request.is_json:
        challenge_organizer_create_request = ChallengeOrganizerCreateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def create_challenge_sponsor(account_name, challenge_name, challenge_sponsor_create_request):  # noqa: E501
    """Create a challenge sponsor

    Create a challenge sponsor # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str
    :param challenge_sponsor_create_request: 
    :type challenge_sponsor_create_request: dict | bytes

    :rtype: ChallengeSponsorCreateResponse
    """
    if connexion.request.is_json:
        challenge_sponsor_create_request = ChallengeSponsorCreateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def delete_all_challenges():  # noqa: E501
    """Delete all challenges

    Delete all challenges # noqa: E501


    :rtype: object
    """
    return 'do some magic!'


def delete_challenge(account_name, challenge_name):  # noqa: E501
    """Delete a challenge

    Deletes the challenge specified # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: object
    """
    return 'do some magic!'


def delete_challenge_organizer(account_name, challenge_name, organizer_id):  # noqa: E501
    """Delete a challenge organizer

    Deletes the challenge organizer specified # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str
    :param organizer_id: The identifier of the challenge organizer
    :type organizer_id: str

    :rtype: object
    """
    return 'do some magic!'


def delete_challenge_sponsor(account_name, challenge_name, sponsor_id):  # noqa: E501
    """Delete a challenge sponsor

    Deletes the challenge sponsor specified # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str
    :param sponsor_id: The identifier of the challenge sponsor
    :type sponsor_id: str

    :rtype: object
    """
    return 'do some magic!'


def get_challenge(account_name, challenge_name):  # noqa: E501
    """Get a challenge

    Returns the challenge specified # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: Challenge
    """
    return 'do some magic!'


def get_challenge_readme(account_name, challenge_name):  # noqa: E501
    """Get a challenge README

    Returns the challenge README specified # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: ChallengeReadme
    """
    return 'do some magic!'


def list_account_challenges(account_name, limit=None, offset=None, search_terms=None):  # noqa: E501
    """List all the challenges owned by the specified account

    List all the challenges owned by the specified account # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int
    :param search_terms: A string of search terms used to filter the results
    :type search_terms: str

    :rtype: PageOfChallenges
    """
    return 'do some magic!'


def list_challenge_organizers(account_name, challenge_name):  # noqa: E501
    """List organizers

    Lists the organizers of the challenge. # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: ChallengeOrganizerList
    """
    return 'do some magic!'


def list_challenge_sponsors(account_name, challenge_name):  # noqa: E501
    """List sponsors

    Lists the sponsors of the challenge. # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: ChallengeSponsorList
    """
    return 'do some magic!'


def list_challenge_stargazers(account_name, challenge_name, limit=None, offset=None):  # noqa: E501
    """List stargazers

    Lists the people that have starred the challenge. # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str
    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int

    :rtype: PageOfUsers
    """
    return 'do some magic!'


def list_challenge_topics(account_name, challenge_name):  # noqa: E501
    """List stargazers

    Lists the challenge topics. # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str

    :rtype: ArrayOfTopics
    """
    return 'do some magic!'


def list_challenges(limit=None, offset=None, sort=None, direction=None, search_terms=None, topics=None, input_data_types=None, status=None, platform_ids=None, difficulty=None, submission_types=None, incentive_types=None, start_date_range=None, org_ids=None, organizer_ids=None, sponsor_ids=None):  # noqa: E501
    """List all the challenges

    Returns all the challenges # noqa: E501

    :param limit: Maximum number of results returned
    :type limit: int
    :param offset: Index of the first result that must be returned
    :type offset: int
    :param sort: Properties used to sort the results that must be returned:   * featured - featured challenge, from featured to non-featured.   * startDate - start date of a challenge, from latest to oldest.   * participantCount - number of participants of a challenge, from most to least.   * viewCount - number of views of a challenge, from most to least.   * starredCount - number of stargazers of a challenge, from most to least.   * name - name of a challenge, from A to Z.   * createdAt - when a challenge is created, from latest to oldest.   * updatedAt - when a challenge is updated, from latest to oldest. 
    :type sort: str
    :param direction: Can be either &#x60;asc&#x60; or &#x60;desc&#x60;. Ignored without &#x60;sort&#x60; parameter.
    :type direction: str
    :param search_terms: A string of search terms used to filter the results
    :type search_terms: str
    :param topics: Array of topics used to filter the results
    :type topics: List[str]
    :param input_data_types: Array of input data types used to filter the results
    :type input_data_types: List[str]
    :param status: Array of challenge status used to filter the results
    :type status: list | bytes
    :param platform_ids: Array of challenge platform ids used to filter the results
    :type platform_ids: List[str]
    :param difficulty: Array of challenge difficulty levels used to filter the results
    :type difficulty: list | bytes
    :param submission_types: Array of challenge submission types used to filter the results
    :type submission_types: list | bytes
    :param incentive_types: Array of challenge incentive types used to filter the results
    :type incentive_types: list | bytes
    :param start_date_range: Return challenges that start during the date range specified
    :type start_date_range: dict | bytes
    :param org_ids: Array of organization ids used to filter the results
    :type org_ids: List[str]
    :param organizer_ids: Array of organizer identifiers used to filter the results
    :type organizer_ids: List[str]
    :param sponsor_ids: Array of sponsor org identifiers used to filter the results
    :type sponsor_ids: List[str]

    :rtype: PageOfChallenges
    """
    if connexion.request.is_json:
        status = [ChallengeStatus.from_dict(d) for d in connexion.request.get_json()]  # noqa: E501
    if connexion.request.is_json:
        difficulty = [ChallengeDifficulty.from_dict(d) for d in connexion.request.get_json()]  # noqa: E501
    if connexion.request.is_json:
        submission_types = [ChallengeSubmissionType.from_dict(d) for d in connexion.request.get_json()]  # noqa: E501
    if connexion.request.is_json:
        incentive_types = [ChallengeIncentiveType.from_dict(d) for d in connexion.request.get_json()]  # noqa: E501
    if connexion.request.is_json:
        start_date_range =  DateRange.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'


def update_challenge_readme(account_name, challenge_name, challenge_readme_update_request):  # noqa: E501
    """Update a challenge README

    Update a challenge README # noqa: E501

    :param account_name: The name of the account that owns the challenge
    :type account_name: str
    :param challenge_name: The name of the challenge
    :type challenge_name: str
    :param challenge_readme_update_request: 
    :type challenge_readme_update_request: dict | bytes

    :rtype: object
    """
    if connexion.request.is_json:
        challenge_readme_update_request = ChallengeReadmeUpdateRequest.from_dict(connexion.request.get_json())  # noqa: E501
    return 'do some magic!'
