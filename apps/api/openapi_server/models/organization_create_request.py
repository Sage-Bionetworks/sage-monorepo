# coding: utf-8

from __future__ import absolute_import
from datetime import date, datetime  # noqa: F401

from typing import List, Dict  # noqa: F401

from openapi_server.models.base_model_ import Model
from openapi_server import util


class OrganizationCreateRequest(Model):
    """NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).

    Do not edit the class manually.
    """

    def __init__(self, login=None, email=None, name=None, avatar_url=None, website_url=None, description=None):  # noqa: E501
        """OrganizationCreateRequest - a model defined in OpenAPI

        :param login: The login of this OrganizationCreateRequest.  # noqa: E501
        :type login: str
        :param email: The email of this OrganizationCreateRequest.  # noqa: E501
        :type email: str
        :param name: The name of this OrganizationCreateRequest.  # noqa: E501
        :type name: str
        :param avatar_url: The avatar_url of this OrganizationCreateRequest.  # noqa: E501
        :type avatar_url: str
        :param website_url: The website_url of this OrganizationCreateRequest.  # noqa: E501
        :type website_url: str
        :param description: The description of this OrganizationCreateRequest.  # noqa: E501
        :type description: str
        """
        self.openapi_types = {
            'login': str,
            'email': str,
            'name': str,
            'avatar_url': str,
            'website_url': str,
            'description': str
        }

        self.attribute_map = {
            'login': 'login',
            'email': 'email',
            'name': 'name',
            'avatar_url': 'avatarUrl',
            'website_url': 'websiteUrl',
            'description': 'description'
        }

        self._login = login
        self._email = email
        self._name = name
        self._avatar_url = avatar_url
        self._website_url = website_url
        self._description = description

    @classmethod
    def from_dict(cls, dikt) -> 'OrganizationCreateRequest':
        """Returns the dict as a model

        :param dikt: A dict.
        :type: dict
        :return: The OrganizationCreateRequest of this OrganizationCreateRequest.  # noqa: E501
        :rtype: OrganizationCreateRequest
        """
        return util.deserialize_model(dikt, cls)

    @property
    def login(self):
        """Gets the login of this OrganizationCreateRequest.


        :return: The login of this OrganizationCreateRequest.
        :rtype: str
        """
        return self._login

    @login.setter
    def login(self, login):
        """Sets the login of this OrganizationCreateRequest.


        :param login: The login of this OrganizationCreateRequest.
        :type login: str
        """
        if login is None:
            raise ValueError("Invalid value for `login`, must not be `None`")  # noqa: E501

        self._login = login

    @property
    def email(self):
        """Gets the email of this OrganizationCreateRequest.

        An email address  # noqa: E501

        :return: The email of this OrganizationCreateRequest.
        :rtype: str
        """
        return self._email

    @email.setter
    def email(self, email):
        """Sets the email of this OrganizationCreateRequest.

        An email address  # noqa: E501

        :param email: The email of this OrganizationCreateRequest.
        :type email: str
        """
        if email is None:
            raise ValueError("Invalid value for `email`, must not be `None`")  # noqa: E501

        self._email = email

    @property
    def name(self):
        """Gets the name of this OrganizationCreateRequest.


        :return: The name of this OrganizationCreateRequest.
        :rtype: str
        """
        return self._name

    @name.setter
    def name(self, name):
        """Sets the name of this OrganizationCreateRequest.


        :param name: The name of this OrganizationCreateRequest.
        :type name: str
        """

        self._name = name

    @property
    def avatar_url(self):
        """Gets the avatar_url of this OrganizationCreateRequest.


        :return: The avatar_url of this OrganizationCreateRequest.
        :rtype: str
        """
        return self._avatar_url

    @avatar_url.setter
    def avatar_url(self, avatar_url):
        """Sets the avatar_url of this OrganizationCreateRequest.


        :param avatar_url: The avatar_url of this OrganizationCreateRequest.
        :type avatar_url: str
        """

        self._avatar_url = avatar_url

    @property
    def website_url(self):
        """Gets the website_url of this OrganizationCreateRequest.


        :return: The website_url of this OrganizationCreateRequest.
        :rtype: str
        """
        return self._website_url

    @website_url.setter
    def website_url(self, website_url):
        """Sets the website_url of this OrganizationCreateRequest.


        :param website_url: The website_url of this OrganizationCreateRequest.
        :type website_url: str
        """

        self._website_url = website_url

    @property
    def description(self):
        """Gets the description of this OrganizationCreateRequest.


        :return: The description of this OrganizationCreateRequest.
        :rtype: str
        """
        return self._description

    @description.setter
    def description(self, description):
        """Sets the description of this OrganizationCreateRequest.


        :param description: The description of this OrganizationCreateRequest.
        :type description: str
        """

        self._description = description
