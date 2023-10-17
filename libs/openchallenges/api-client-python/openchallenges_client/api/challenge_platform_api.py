# coding: utf-8

"""
    OpenChallenges REST API

    No description provided (generated by Openapi Generator https://github.com/openapitools/openapi-generator)

    The version of the OpenAPI document: 1.0.0
    Generated by OpenAPI Generator (https://openapi-generator.tech)

    Do not edit the class manually.
"""  # noqa: E501


import re  # noqa: F401
import io
import warnings

from pydantic import validate_arguments, ValidationError
from typing_extensions import Annotated

from pydantic import Field, constr

from typing import Optional

from openchallenges_client.models.challenge_platform import ChallengePlatform
from openchallenges_client.models.challenge_platform_search_query import ChallengePlatformSearchQuery
from openchallenges_client.models.challenge_platforms_page import ChallengePlatformsPage

from openchallenges_client.api_client import ApiClient
from openchallenges_client.api_response import ApiResponse
from openchallenges_client.exceptions import (  # noqa: F401
    ApiTypeError,
    ApiValueError
)


class ChallengePlatformApi(object):
    """NOTE: This class is auto generated by OpenAPI Generator
    Ref: https://openapi-generator.tech

    Do not edit the class manually.
    """

    def __init__(self, api_client=None):
        if api_client is None:
            api_client = ApiClient.get_default()
        self.api_client = api_client

    @validate_arguments
    def get_challenge_platform(self, challenge_platform_name : Annotated[constr(strict=True, max_length=30, min_length=3), Field(..., description="The unique identifier of the challenge platform.")], **kwargs) -> ChallengePlatform:  # noqa: E501
        """Get a challenge platform  # noqa: E501

        Returns the challenge platform specified  # noqa: E501
        This method makes a synchronous HTTP request by default. To make an
        asynchronous HTTP request, please pass async_req=True

        >>> thread = api.get_challenge_platform(challenge_platform_name, async_req=True)
        >>> result = thread.get()

        :param challenge_platform_name: The unique identifier of the challenge platform. (required)
        :type challenge_platform_name: str
        :param async_req: Whether to execute the request asynchronously.
        :type async_req: bool, optional
        :param _request_timeout: timeout setting for this request. If one
                                 number provided, it will be total request
                                 timeout. It can also be a pair (tuple) of
                                 (connection, read) timeouts.
        :return: Returns the result object.
                 If the method is called asynchronously,
                 returns the request thread.
        :rtype: ChallengePlatform
        """
        kwargs['_return_http_data_only'] = True
        if '_preload_content' in kwargs:
            raise ValueError("Error! Please call the get_challenge_platform_with_http_info method with `_preload_content` instead and obtain raw data from ApiResponse.raw_data")
        return self.get_challenge_platform_with_http_info(challenge_platform_name, **kwargs)  # noqa: E501

    @validate_arguments
    def get_challenge_platform_with_http_info(self, challenge_platform_name : Annotated[constr(strict=True, max_length=30, min_length=3), Field(..., description="The unique identifier of the challenge platform.")], **kwargs) -> ApiResponse:  # noqa: E501
        """Get a challenge platform  # noqa: E501

        Returns the challenge platform specified  # noqa: E501
        This method makes a synchronous HTTP request by default. To make an
        asynchronous HTTP request, please pass async_req=True

        >>> thread = api.get_challenge_platform_with_http_info(challenge_platform_name, async_req=True)
        >>> result = thread.get()

        :param challenge_platform_name: The unique identifier of the challenge platform. (required)
        :type challenge_platform_name: str
        :param async_req: Whether to execute the request asynchronously.
        :type async_req: bool, optional
        :param _preload_content: if False, the ApiResponse.data will
                                 be set to none and raw_data will store the 
                                 HTTP response body without reading/decoding.
                                 Default is True.
        :type _preload_content: bool, optional
        :param _return_http_data_only: response data instead of ApiResponse
                                       object with status code, headers, etc
        :type _return_http_data_only: bool, optional
        :param _request_timeout: timeout setting for this request. If one
                                 number provided, it will be total request
                                 timeout. It can also be a pair (tuple) of
                                 (connection, read) timeouts.
        :param _request_auth: set to override the auth_settings for an a single
                              request; this effectively ignores the authentication
                              in the spec for a single request.
        :type _request_auth: dict, optional
        :type _content_type: string, optional: force content-type for the request
        :return: Returns the result object.
                 If the method is called asynchronously,
                 returns the request thread.
        :rtype: tuple(ChallengePlatform, status_code(int), headers(HTTPHeaderDict))
        """

        _params = locals()

        _all_params = [
            'challenge_platform_name'
        ]
        _all_params.extend(
            [
                'async_req',
                '_return_http_data_only',
                '_preload_content',
                '_request_timeout',
                '_request_auth',
                '_content_type',
                '_headers'
            ]
        )

        # validate the arguments
        for _key, _val in _params['kwargs'].items():
            if _key not in _all_params:
                raise ApiTypeError(
                    "Got an unexpected keyword argument '%s'"
                    " to method get_challenge_platform" % _key
                )
            _params[_key] = _val
        del _params['kwargs']

        _collection_formats = {}

        # process the path parameters
        _path_params = {}
        if _params['challenge_platform_name']:
            _path_params['challengePlatformName'] = _params['challenge_platform_name']


        # process the query parameters
        _query_params = []
        # process the header parameters
        _header_params = dict(_params.get('_headers', {}))
        # process the form parameters
        _form_params = []
        _files = {}
        # process the body parameter
        _body_params = None
        # set the HTTP header `Accept`
        _header_params['Accept'] = self.api_client.select_header_accept(
            ['application/json', 'application/problem+json'])  # noqa: E501

        # authentication setting
        _auth_settings = []  # noqa: E501

        _response_types_map = {
            '200': "ChallengePlatform",
            '404': "BasicError",
            '500': "BasicError",
        }

        return self.api_client.call_api(
            '/challengePlatforms/{challengePlatformName}', 'GET',
            _path_params,
            _query_params,
            _header_params,
            body=_body_params,
            post_params=_form_params,
            files=_files,
            response_types_map=_response_types_map,
            auth_settings=_auth_settings,
            async_req=_params.get('async_req'),
            _return_http_data_only=_params.get('_return_http_data_only'),  # noqa: E501
            _preload_content=_params.get('_preload_content', True),
            _request_timeout=_params.get('_request_timeout'),
            collection_formats=_collection_formats,
            _request_auth=_params.get('_request_auth'))

    @validate_arguments
    def list_challenge_platforms(self, challenge_platform_search_query : Annotated[Optional[ChallengePlatformSearchQuery], Field(description="The search query used to find challenge platforms.")] = None, **kwargs) -> ChallengePlatformsPage:  # noqa: E501
        """List challenge platforms  # noqa: E501

        List challenge platforms  # noqa: E501
        This method makes a synchronous HTTP request by default. To make an
        asynchronous HTTP request, please pass async_req=True

        >>> thread = api.list_challenge_platforms(challenge_platform_search_query, async_req=True)
        >>> result = thread.get()

        :param challenge_platform_search_query: The search query used to find challenge platforms.
        :type challenge_platform_search_query: ChallengePlatformSearchQuery
        :param async_req: Whether to execute the request asynchronously.
        :type async_req: bool, optional
        :param _request_timeout: timeout setting for this request. If one
                                 number provided, it will be total request
                                 timeout. It can also be a pair (tuple) of
                                 (connection, read) timeouts.
        :return: Returns the result object.
                 If the method is called asynchronously,
                 returns the request thread.
        :rtype: ChallengePlatformsPage
        """
        kwargs['_return_http_data_only'] = True
        if '_preload_content' in kwargs:
            raise ValueError("Error! Please call the list_challenge_platforms_with_http_info method with `_preload_content` instead and obtain raw data from ApiResponse.raw_data")
        return self.list_challenge_platforms_with_http_info(challenge_platform_search_query, **kwargs)  # noqa: E501

    @validate_arguments
    def list_challenge_platforms_with_http_info(self, challenge_platform_search_query : Annotated[Optional[ChallengePlatformSearchQuery], Field(description="The search query used to find challenge platforms.")] = None, **kwargs) -> ApiResponse:  # noqa: E501
        """List challenge platforms  # noqa: E501

        List challenge platforms  # noqa: E501
        This method makes a synchronous HTTP request by default. To make an
        asynchronous HTTP request, please pass async_req=True

        >>> thread = api.list_challenge_platforms_with_http_info(challenge_platform_search_query, async_req=True)
        >>> result = thread.get()

        :param challenge_platform_search_query: The search query used to find challenge platforms.
        :type challenge_platform_search_query: ChallengePlatformSearchQuery
        :param async_req: Whether to execute the request asynchronously.
        :type async_req: bool, optional
        :param _preload_content: if False, the ApiResponse.data will
                                 be set to none and raw_data will store the 
                                 HTTP response body without reading/decoding.
                                 Default is True.
        :type _preload_content: bool, optional
        :param _return_http_data_only: response data instead of ApiResponse
                                       object with status code, headers, etc
        :type _return_http_data_only: bool, optional
        :param _request_timeout: timeout setting for this request. If one
                                 number provided, it will be total request
                                 timeout. It can also be a pair (tuple) of
                                 (connection, read) timeouts.
        :param _request_auth: set to override the auth_settings for an a single
                              request; this effectively ignores the authentication
                              in the spec for a single request.
        :type _request_auth: dict, optional
        :type _content_type: string, optional: force content-type for the request
        :return: Returns the result object.
                 If the method is called asynchronously,
                 returns the request thread.
        :rtype: tuple(ChallengePlatformsPage, status_code(int), headers(HTTPHeaderDict))
        """

        _params = locals()

        _all_params = [
            'challenge_platform_search_query'
        ]
        _all_params.extend(
            [
                'async_req',
                '_return_http_data_only',
                '_preload_content',
                '_request_timeout',
                '_request_auth',
                '_content_type',
                '_headers'
            ]
        )

        # validate the arguments
        for _key, _val in _params['kwargs'].items():
            if _key not in _all_params:
                raise ApiTypeError(
                    "Got an unexpected keyword argument '%s'"
                    " to method list_challenge_platforms" % _key
                )
            _params[_key] = _val
        del _params['kwargs']

        _collection_formats = {}

        # process the path parameters
        _path_params = {}

        # process the query parameters
        _query_params = []
        if _params.get('challenge_platform_search_query') is not None:  # noqa: E501
            _query_params.append(('challengePlatformSearchQuery', _params['challenge_platform_search_query']))

        # process the header parameters
        _header_params = dict(_params.get('_headers', {}))
        # process the form parameters
        _form_params = []
        _files = {}
        # process the body parameter
        _body_params = None
        # set the HTTP header `Accept`
        _header_params['Accept'] = self.api_client.select_header_accept(
            ['application/json', 'application/problem+json'])  # noqa: E501

        # authentication setting
        _auth_settings = []  # noqa: E501

        _response_types_map = {
            '200': "ChallengePlatformsPage",
            '400': "BasicError",
            '500': "BasicError",
        }

        return self.api_client.call_api(
            '/challengePlatforms', 'GET',
            _path_params,
            _query_params,
            _header_params,
            body=_body_params,
            post_params=_form_params,
            files=_files,
            response_types_map=_response_types_map,
            auth_settings=_auth_settings,
            async_req=_params.get('async_req'),
            _return_http_data_only=_params.get('_return_http_data_only'),  # noqa: E501
            _preload_content=_params.get('_preload_content', True),
            _request_timeout=_params.get('_request_timeout'),
            collection_formats=_collection_formats,
            _request_auth=_params.get('_request_auth'))
