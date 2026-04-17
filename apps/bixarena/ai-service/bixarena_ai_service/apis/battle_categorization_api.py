# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil

from bixarena_ai_service.apis.battle_categorization_api_base import (
    BaseBattleCategorizationApi,
)
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
from bixarena_ai_service.models.battle_categorization import BattleCategorization
from bixarena_ai_service.models.battle_categorization_request import (
    BattleCategorizationRequest,
)


router = APIRouter()

ns_pkg = bixarena_ai_service.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.post(
    "/categorize-battle",
    responses={
        200: {"model": BattleCategorization, "description": "Success"},
        400: {"model": BasicError, "description": "Invalid request"},
        401: {"model": BasicError, "description": "Unauthorized"},
        500: {
            "model": BasicError,
            "description": "The request cannot be fulfilled due to an unexpected server error",
        },
    },
    tags=["Battle Categorization"],
    summary="Categorize a battle",
    response_model_by_alias=True,
)
async def categorize_battle(
    battle_categorization_request: BattleCategorizationRequest = Body(
        None, description=""
    ),
) -> BattleCategorization:
    """Classifies the prompts of a battle conversation into one or more biomedical subject categories using an LLM. Requires authentication."""
    if not BaseBattleCategorizationApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseBattleCategorizationApi.subclasses[0]().categorize_battle(
        battle_categorization_request
    )
