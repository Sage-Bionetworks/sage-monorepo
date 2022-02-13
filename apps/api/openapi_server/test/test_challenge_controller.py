# coding: utf-8

from __future__ import absolute_import
import unittest

from flask import json
from six import BytesIO

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
from openapi_server.test import BaseTestCase


class TestChallengeController(BaseTestCase):
    """ChallengeController integration test stubs"""

    def test_create_challenge(self):
        """Test case for create_challenge

        Add a challenge
        """
        challenge_create_request = {
  "name" : "awesome-challenge",
  "displayName" : "Awesome Challenge",
  "description" : "This challenge is awesome.",
  "websiteUrl" : "https://synapse.org/awesome-challenge",
  "status" : "active",
  "startDate" : "2020-11-10",
  "endDate" : "2020-12-31",
  "platformId" : "507f1f77bcf86cd799439011",
  "submissionTypes" : [ "PredictionFile", "DockerImage" ],
  "incentiveTypes" : [ "Monetary", "Publication" ],
  "topics" : [ "breast-cancer", "covid" ],
  "dataTypes" : [ "genomic", "ehr" ],
  "doi" : "http://doi.org/10.5281/zenodo.3714971",
  "participantCount" : 100
}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}'.format(account_name='account_name_example'),
            method='POST',
            headers=headers,
            data=json.dumps(challenge_create_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_create_challenge_organizer(self):
        """Test case for create_challenge_organizer

        Create a challenge organizer
        """
        challenge_organizer_create_request = {
  "name" : "John Smith",
  "login" : "jsmith",
  "roles" : [ "ChallengeLead", "InfrastructureLead" ]
}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/organizers'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='POST',
            headers=headers,
            data=json.dumps(challenge_organizer_create_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_create_challenge_sponsor(self):
        """Test case for create_challenge_sponsor

        Create a challenge sponsor
        """
        challenge_sponsor_create_request = {
  "name" : "IBM",
  "login" : "ibm",
  "roles" : [ "ComputeProvider", "DataProvider" ]
}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/sponsors'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='POST',
            headers=headers,
            data=json.dumps(challenge_sponsor_create_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_all_challenges(self):
        """Test case for delete_all_challenges

        Delete all challenges
        """
        headers = { 
            'Accept': 'application/json',
            'ApiKeyAuth': 'special-key',
        }
        response = self.client.open(
            '/api/v1/challenges',
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_challenge(self):
        """Test case for delete_challenge

        Delete a challenge
        """
        headers = { 
            'Accept': 'application/json',
            'ApiKeyAuth': 'special-key',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_challenge_organizer(self):
        """Test case for delete_challenge_organizer

        Delete a challenge organizer
        """
        headers = { 
            'Accept': 'application/json',
            'ApiKeyAuth': 'special-key',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/organizers/{organizer_id}'.format(account_name='account_name_example', challenge_name='challenge_name_example', organizer_id='organizer_id_example'),
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_delete_challenge_sponsor(self):
        """Test case for delete_challenge_sponsor

        Delete a challenge sponsor
        """
        headers = { 
            'Accept': 'application/json',
            'ApiKeyAuth': 'special-key',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/sponsors/{sponsor_id}'.format(account_name='account_name_example', challenge_name='challenge_name_example', sponsor_id='sponsor_id_example'),
            method='DELETE',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_challenge(self):
        """Test case for get_challenge

        Get a challenge
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_get_challenge_readme(self):
        """Test case for get_challenge_readme

        Get a challenge README
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/readme'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_account_challenges(self):
        """Test case for list_account_challenges

        List all the challenges owned by the specified account
        """
        query_string = [('limit', 10),
                        ('offset', 0),
                        ('searchTerms', 'search_terms_example')]
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}'.format(account_name='account_name_example'),
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_challenge_organizers(self):
        """Test case for list_challenge_organizers

        List organizers
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/organizers'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_challenge_sponsors(self):
        """Test case for list_challenge_sponsors

        List sponsors
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/sponsors'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_challenge_stargazers(self):
        """Test case for list_challenge_stargazers

        List stargazers
        """
        query_string = [('limit', 10),
                        ('offset', 0)]
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/stargazers'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_challenge_topics(self):
        """Test case for list_challenge_topics

        List stargazers
        """
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/topics'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='GET',
            headers=headers)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_list_challenges(self):
        """Test case for list_challenges

        List all the challenges
        """
        query_string = [('limit', 10),
                        ('offset', 0),
                        ('sort', 'sort_example'),
                        ('direction', 'direction_example'),
                        ('searchTerms', 'search_terms_example'),
                        ('topics', ['topics_example']),
                        ('inputDataTypes', ['input_data_types_example']),
                        ('status', [openapi_server.ChallengeStatus()]),
                        ('platformIds', ['platform_ids_example']),
                        ('difficulty', [openapi_server.ChallengeDifficulty()]),
                        ('submissionTypes', [openapi_server.ChallengeSubmissionType()]),
                        ('incentiveTypes', [openapi_server.ChallengeIncentiveType()]),
                        ('startDateRange', openapi_server.DateRange()),
                        ('orgIds', ['org_ids_example']),
                        ('organizerIds', ['organizer_ids_example']),
                        ('sponsorIds', ['sponsor_ids_example'])]
        headers = { 
            'Accept': 'application/json',
        }
        response = self.client.open(
            '/api/v1/challenges',
            method='GET',
            headers=headers,
            query_string=query_string)
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))

    def test_update_challenge_readme(self):
        """Test case for update_challenge_readme

        Update a challenge README
        """
        challenge_readme_update_request = {
  "text" : "A great README text"
}
        headers = { 
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': 'Bearer special-key',
        }
        response = self.client.open(
            '/api/v1/challenges/{account_name}/{challenge_name}/readme'.format(account_name='account_name_example', challenge_name='challenge_name_example'),
            method='PATCH',
            headers=headers,
            data=json.dumps(challenge_readme_update_request),
            content_type='application/json')
        self.assert200(response,
                       'Response body is : ' + response.data.decode('utf-8'))


if __name__ == '__main__':
    unittest.main()
