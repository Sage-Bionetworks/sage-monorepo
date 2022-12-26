from mongoengine.errors import DoesNotExist

from openapi_server.dbmodels.account import Account as DbAccount
from openapi_server.models.account import Account  # noqa: E501
from openapi_server.models.error import Error  # noqa: E501


def get_account(login):  # noqa: E501
    """Get an account

    Returns the user or org account # noqa: E501

    :param login: The login of an account
    :type login: str

    :rtype: Account
    """
    try:
        db_account = DbAccount.objects.get(login=login)
        res = Account.from_dict(db_account.to_dict())
        status = 200
    except DoesNotExist:
        status = 404
        res = Error("The specified resource was not found", status)
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status
