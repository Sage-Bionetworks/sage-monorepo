"""Runtime implementation for health check endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/health_check_api.py``). Any subclass of
``BaseHealthCheckApi`` discovered at import time is used to service incoming
requests. By placing our implementation here we avoid modifying generated
code that would be overwritten on regeneration.
"""

from __future__ import annotations

import logging

from bixarena_ai_service.apis.health_check_api_base import BaseHealthCheckApi
from bixarena_ai_service.models.health_check import HealthCheck

logger = logging.getLogger(__name__)


class HealthCheckApiImpl(BaseHealthCheckApi):
    """Concrete health check implementation.

    Currently always reports a passing status. In the future this method can
    aggregate dependencies (DB connections, external services, etc.) and
    downgrade the status to ``warn`` or ``fail`` accordingly while still
    returning HTTP 200 for observability compatibility.
    """

    async def get_health_check(self) -> HealthCheck:  # type: ignore[override]
        logger.debug("Health check invoked; returning static pass status")
        return HealthCheck(status="pass")
