import connexion
import six

from openapi_server.models.account import Account  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501
from openapi_server import util


def get_account(login):  # noqa: E501
    """Get an account

    Returns the user or org account # noqa: E501

    :param login: The login of an account
    :type login: str

    :rtype: Account
    """
    return 'do some magic!'
