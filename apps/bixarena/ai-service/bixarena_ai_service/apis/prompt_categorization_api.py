# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil

from bixarena_ai_service.apis.prompt_categorization_api_base import (
    BasePromptCategorizationApi,
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
from bixarena_ai_service.models.prompt_categorization import PromptCategorization
from bixarena_ai_service.models.prompt_categorization_request import (
    PromptCategorizationRequest,
)


router = APIRouter()

ns_pkg = bixarena_ai_service.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.post(
    "/categorize-prompt",
    responses={
        200: {"model": PromptCategorization, "description": "Success"},
        400: {"model": BasicError, "description": "Invalid request"},
        401: {"model": BasicError, "description": "Unauthorized"},
        500: {
            "model": BasicError,
            "description": "The request cannot be fulfilled due to an unexpected server error",
        },
    },
    tags=["Prompt Categorization"],
    summary="Categorize a prompt",
    response_model_by_alias=True,
)
async def categorize_prompt(
    prompt_categorization_request: PromptCategorizationRequest = Body(
        None, description=""
    ),
) -> PromptCategorization:
    """Classifies a single prompt into one or more biomedical subject categories using an LLM. Requires authentication."""
    if not BasePromptCategorizationApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BasePromptCategorizationApi.subclasses[0]().categorize_prompt(
        prompt_categorization_request
    )
