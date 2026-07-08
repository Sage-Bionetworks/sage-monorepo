# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil

from bixarena_ai_service.apis.health_check_api_base import BaseHealthCheckApi
import bixarena_ai_service.impl

from fastapi import (  # noqa: F401
    APIRouter,
    Body,
    Cookie,
    Depends,
    Form,
    Header,
    HTTPException,
    Path,
    Query,
    Response,
    Security,
    status,
)

from bixarena_ai_service.models.extra_models import TokenModel  # noqa: F401
from bixarena_ai_service.models.basic_error import BasicError
from bixarena_ai_service.models.health_check import HealthCheck


router = APIRouter()

ns_pkg = bixarena_ai_service.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.get(
    "/health-check",
    responses={
        200: {"model": HealthCheck, "description": "Success"},
        404: {"model": BasicError, "description": "The specified resource was not found"},
        500: {"model": BasicError, "description": "The request cannot be fulfilled due to an unexpected server error"},
    },
    tags=["Health Check"],
    summary="Get health check information",
    response_model_by_alias=True,
)
async def get_health_check(
) -> HealthCheck:
    """Get information about the health of the service"""
    if not BaseHealthCheckApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseHealthCheckApi.subclasses[0]().get_health_check()
