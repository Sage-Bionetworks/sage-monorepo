# coding: utf-8

from typing import Dict, List  # noqa: F401
import importlib
import pkgutil

from bixarena_ai_service.apis.chat_api_base import BaseChatApi
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
from bixarena_ai_service.models.model_chat_completion_chunk import (
    ModelChatCompletionChunk,
)
from bixarena_ai_service.models.model_chat_request import ModelChatRequest
from bixarena_ai_service.models.rate_limit_error import RateLimitError


router = APIRouter()

ns_pkg = bixarena_ai_service.impl
for _, name, _ in pkgutil.iter_modules(ns_pkg.__path__, ns_pkg.__name__ + "."):
    importlib.import_module(name)


@router.post(
    "/chat/completions",
    responses={
        200: {
            "model": ModelChatCompletionChunk,
            "description": "Streaming HTTP response with chat completion chunks",
        },
        400: {"model": BasicError, "description": "Invalid request"},
        401: {"model": BasicError, "description": "Unauthorized"},
        429: {
            "model": RateLimitError,
            "description": "Too many requests. Rate limit exceeded. The client should wait before making additional requests.",
        },
        500: {
            "model": BasicError,
            "description": "The request cannot be fulfilled due to an unexpected server error",
        },
    },
    tags=["Chat"],
    summary="Create a chat completion",
    response_model_by_alias=True,
)
async def create_chat_completion(
    model_chat_request: ModelChatRequest = Body(None, description=""),
) -> ModelChatCompletionChunk:
    """Sends messages to an LLM model via OpenRouter and returns a streaming HTTP response (text/event-stream)"""
    if not BaseChatApi.subclasses:
        raise HTTPException(status_code=500, detail="Not implemented")
    return await BaseChatApi.subclasses[0]().create_chat_completion(model_chat_request)
