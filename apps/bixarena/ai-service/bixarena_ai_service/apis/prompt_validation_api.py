# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil

from bixarena_ai_service.apis.prompt_validation_api_base import BasePromptValidationApi
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
from bixarena_ai_service.models.prompt_validation import PromptValidation
from bixarena_ai_service.models.prompt_validation_request import PromptValidationRequest


router = APIRouter()

ns_pkg = bixarena_ai_service.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.post(
    "/validate-prompt",
    responses={
        200: {"model": PromptValidation, "description": "Success"},
        400: {"model": BasicError, "description": "Invalid request"},
        401: {"model": BasicError, "description": "Unauthorized"},
        500: {
            "model": BasicError,
            "description": "The request cannot be fulfilled due to an unexpected server error",
        },
    },
    tags=["Prompt Validation"],
    summary="Validate biomedical prompt",
    response_model_by_alias=True,
)
async def validate_prompt(
    prompt_validation_request: PromptValidationRequest = Body(None, description=""),
) -> PromptValidation:
    """Validates whether a prompt is biomedically related and returns a confidence score (requires authentication)"""
    if not BasePromptValidationApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BasePromptValidationApi.subclasses[0]().validate_prompt(
        prompt_validation_request
    )
