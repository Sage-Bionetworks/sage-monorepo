"""Runtime implementation for POST /chat/completions.

Proxies chat messages to an OpenAI-compatible LLM provider
and returns the response as an SSE stream of ModelChatCompletionChunk events.

Key behaviours ported from the Gradio frontend:
- System prompt injection (identity guard)
- System-role guardrail: reject caller-supplied system messages
- System message retry for models that don't support the system role
- Truncation detection via completion_tokens
- Empty response detection
- Error message sanitization
"""

from __future__ import annotations

import logging
from collections.abc import AsyncGenerator
from pathlib import Path

from fastapi import HTTPException
from fastapi.sse import EventSourceResponse
from openai import APIError, AsyncOpenAI

from bixarena_ai_service.apis.chat_api_base import BaseChatApi
from bixarena_ai_service.config import Settings, get_settings
from bixarena_ai_service.models.message_role import MessageRole
from bixarena_ai_service.models.model_chat_completion_chunk import (
    ModelChatCompletionChunk,
)
from bixarena_ai_service.models.model_chat_request import ModelChatRequest
from bixarena_ai_service.models.model_chat_usage import ModelChatUsage

logger = logging.getLogger(__name__)

# Load system prompt once at module level
_SYSTEM_PROMPT_PATH = (
    Path(__file__).resolve().parent.parent / "prompts" / "llm_identity_guard.md"
)
_SYSTEM_PROMPT: str = _SYSTEM_PROMPT_PATH.read_text(encoding="utf-8").strip()


def _build_openai_messages(
    request: ModelChatRequest,
) -> list[dict[str, str]]:
    """Build OpenAI-format messages with system prompt prepended."""
    messages: list[dict[str, str]] = [
        {"role": "system", "content": _SYSTEM_PROMPT},
    ]
    for msg in request.messages:
        messages.append({"role": msg.role.value, "content": msg.content})
    return messages


def _merge_system_into_user(
    messages: list[dict[str, str]],
) -> list[dict[str, str]]:
    """Merge system message into first user message.

    For models that don't support the system role.
    """
    if not messages or messages[0].get("role") != "system":
        return messages
    system_content = messages[0]["content"]
    retry_messages = messages[1:]
    if retry_messages and retry_messages[0].get("role") == "user":
        retry_messages[0] = {
            "role": "user",
            "content": f"{system_content}\n\n{retry_messages[0]['content']}",
        }
    return retry_messages


def _sanitize_error_message(error: Exception) -> str:
    """Return a safe error message without leaking provider details."""
    status_code = getattr(error, "status_code", None)
    if status_code == 401:
        return "The service is currently unavailable (code: 401)."
    if status_code == 403:
        return "The service is currently unavailable (code: 403)."
    if status_code == 404:
        return "The service is currently unavailable (code: 404)."
    if status_code == 429:
        return "The model provider rate limit has been exceeded (code: 429)."
    if status_code == 500:
        return "An internal server error occurred (code: 500)."
    if status_code == 400:
        return "The request could not be processed (code: 400)."
    if status_code:
        return f"An error occurred (code: {status_code})."
    return "An unexpected error occurred."


async def _stream_completion(
    client: AsyncOpenAI,
    model_name: str,
    messages: list[dict[str, str]],
    settings: Settings,
) -> AsyncGenerator[ModelChatCompletionChunk]:
    """Stream chat completion chunks from the LLM provider.

    On system-role errors (400), retries with system message merged into
    the first user message.
    """
    try:
        async for chunk in _do_stream(client, model_name, messages, settings):
            yield chunk
    except APIError as e:
        # Retry without system message if provider doesn't support it
        error_str = str(e).lower()
        is_system_error = any(
            p in error_str
            for p in ["developer instruction", "system role", "system message"]
        )
        if e.status_code == 400 and is_system_error:
            logger.warning("Retrying %s without system message", model_name)
            retry_messages = _merge_system_into_user(messages)
            try:
                async for chunk in _do_stream(
                    client, model_name, retry_messages, settings
                ):
                    yield chunk
                return
            except Exception as retry_e:
                logger.error("Retry also failed: %s", retry_e, exc_info=True)
                e = retry_e

        logger.error("LLM API error: %s", e, exc_info=True)
        yield ModelChatCompletionChunk(
            status="error",
            error_message=_sanitize_error_message(e),
        )


async def _do_stream(
    client: AsyncOpenAI,
    model_name: str,
    messages: list[dict[str, str]],
    settings: Settings,
) -> AsyncGenerator[ModelChatCompletionChunk]:
    """Execute the OpenAI streaming call and yield chunks."""
    text = ""
    finish_reason = None
    usage_data = None

    response = await client.chat.completions.create(
        model=model_name,
        messages=messages,
        temperature=settings.chat_default_temperature,
        top_p=settings.chat_default_top_p,
        max_tokens=settings.chat_max_response_tokens,
        stream=True,
        stream_options={"include_usage": True},
    )

    async for chunk in response:
        if not chunk.choices:
            # Final chunk with usage only
            if chunk.usage:
                usage_data = chunk.usage
            continue

        choice = chunk.choices[0]
        delta_content = choice.delta.content or ""
        text += delta_content

        if choice.finish_reason is not None:
            finish_reason = choice.finish_reason

        if delta_content:
            yield ModelChatCompletionChunk(
                content=delta_content,
                status="streaming",
            )

    # Handle error finish reason
    if finish_reason == "error":
        error_details = (
            getattr(chunk.choices[0], "error", None) if chunk.choices else None
        )
        error_msg = (
            getattr(error_details, "message", "Unknown error")
            if error_details
            else "Unknown error"
        )
        logger.error("Model %s finish_reason='error': %s", model_name, error_msg)
        yield ModelChatCompletionChunk(
            status="error",
            error_message="An error occurred while generating the response.",
        )
        return

    # Empty response detection
    if not text.strip():
        logger.error("Empty response from model: %s", model_name)
        yield ModelChatCompletionChunk(
            status="error",
            error_message="The model returned an empty response.",
        )
        return

    # Truncation detection via completion_tokens
    if finish_reason in (None, "stop") and usage_data:
        completion_tokens = getattr(usage_data, "completion_tokens", 0) or 0
        max_tokens = getattr(usage_data, "max_tokens", None)
        if max_tokens and completion_tokens >= max_tokens:
            logger.warning(
                "%s: max tokens reached (%d/%d)",
                model_name,
                completion_tokens,
                max_tokens,
            )
            finish_reason = "length"

    # Build usage for final chunk
    usage = None
    if usage_data:
        usage = ModelChatUsage(
            model=model_name,
            prompt_tokens=getattr(usage_data, "prompt_tokens", 0) or 0,
            completion_tokens=getattr(usage_data, "completion_tokens", 0) or 0,
            total_tokens=getattr(usage_data, "total_tokens", 0) or 0,
        )

    # Final chunk
    yield ModelChatCompletionChunk(
        status="complete",
        finish_reason=finish_reason or "stop",
        usage=usage,
    )


class ChatApiImpl(BaseChatApi):
    """SSE streaming chat completion via OpenAI-compatible providers."""

    async def create_chat_completion(
        self,
        model_chat_request: ModelChatRequest,
    ) -> EventSourceResponse:
        # Guardrail: reject caller-supplied system messages
        for msg in model_chat_request.messages:
            if msg.role == MessageRole.SYSTEM:
                raise HTTPException(
                    status_code=400,
                    detail="System role messages are not allowed in chat requests.",
                )

        settings = get_settings()

        client = AsyncOpenAI(
            api_key=settings.openrouter_api_key,
            base_url=model_chat_request.api_base,
            timeout=settings.chat_timeout,
            max_retries=settings.chat_max_retries,
        )

        messages = _build_openai_messages(model_chat_request)

        logger.info(
            "Chat completion requested: model=%s, messages=%d",
            model_chat_request.api_model_name,
            len(model_chat_request.messages),
        )

        async def event_generator():
            async for chunk in _stream_completion(
                client=client,
                model_name=model_chat_request.api_model_name,
                messages=messages,
                settings=settings,
            ):
                yield chunk.to_dict()

        return EventSourceResponse(content=event_generator())
