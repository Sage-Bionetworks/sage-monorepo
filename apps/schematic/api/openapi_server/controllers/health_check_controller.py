from openapi_server.models.error import Error  # noqa: E501
from openapi_server.models.health_check import HealthCheck


def get_health_check():
    """Get health check information

    Get information about the health of the service

    :rtype: HealthCheck
    """
    try:
        res = HealthCheck(status="pass")
        status = 200
    except Exception as error:
        status = 500
        res = Error("Internal error", status, str(error))
    return res, status
