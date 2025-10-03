# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from bixarena_ai_service.models.basic_error import BasicError
from bixarena_ai_service.models.health_check import HealthCheck


class BaseHealthCheckApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseHealthCheckApi.subclasses = BaseHealthCheckApi.subclasses + (cls,)

    async def get_health_check(
        self,
    ) -> HealthCheck:
        """Get information about the health of the service"""
        ...
