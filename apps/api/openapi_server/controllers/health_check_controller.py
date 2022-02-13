import connexion
import six

from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.health_check import HealthCheck  # noqa: E501
from openapi_server import util


def get_health_check():  # noqa: E501
    """Get health check information

    Get information about the health of the service # noqa: E501


    :rtype: HealthCheck
    """
    return 'do some magic!'
