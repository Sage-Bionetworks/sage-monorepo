# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from bixarena_ai_service.models.basic_error import BasicError
from bixarena_ai_service.models.model_chat_completion_chunk import (
    ModelChatCompletionChunk,
)
from bixarena_ai_service.models.model_chat_request import ModelChatRequest
from bixarena_ai_service.models.rate_limit_error import RateLimitError


class BaseChatApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseChatApi.subclasses = BaseChatApi.subclasses + (cls,)

    async def create_chat_completion(
        self,
        model_chat_request: ModelChatRequest,
    ) -> ModelChatCompletionChunk:
        """Sends messages to an LLM model via OpenRouter and returns a streaming HTTP response (text/event-stream)"""
        ...
