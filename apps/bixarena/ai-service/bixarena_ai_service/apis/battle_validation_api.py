# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil

from bixarena_ai_service.apis.battle_validation_api_base import BaseBattleValidationApi
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
from bixarena_ai_service.models.battle_validation import BattleValidation
from bixarena_ai_service.models.battle_validation_request import BattleValidationRequest


router = APIRouter()

ns_pkg = bixarena_ai_service.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.post(
    "/validate-battle",
    responses={
        200: {"model": BattleValidation, "description": "Success"},
        400: {"model": BasicError, "description": "Invalid request"},
        401: {"model": BasicError, "description": "Unauthorized"},
        500: {
            "model": BasicError,
            "description": "The request cannot be fulfilled due to an unexpected server error",
        },
    },
    tags=["Battle Validation"],
    summary="Validate biomedical battle",
    response_model_by_alias=True,
)
async def validate_battle(
    battle_validation_request: BattleValidationRequest = Body(None, description=""),
) -> BattleValidation:
    """Validates whether a battle&#39;s conversation (all user prompts) is biomedically related. Returns a confidence score. Requires authentication."""
    if not BaseBattleValidationApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseBattleValidationApi.subclasses[0]().validate_battle(
        battle_validation_request
    )
