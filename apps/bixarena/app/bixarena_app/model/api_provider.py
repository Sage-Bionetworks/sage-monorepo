"""Stream LLM responses from the backend SSE endpoint."""

import json
import logging

from bixarena_api_client import BattleApi

from bixarena_app.api.api_client_helper import create_authenticated_api_client
from bixarena_app.config.constants import MAX_RESPONSE_TOKENS
from bixarena_app.model.error_handler import StreamError

logger = logging.getLogger(__name__)


def get_api_provider_stream_iter(
    model_id,
    battle_session,
    cookies: dict[str, str] | None = None,
    max_new_tokens: int = MAX_RESPONSE_TOKENS,
):
    """Stream LLM response from the backend via SSE.

    Calls POST /battles/{battleId}/rounds/{roundId}/stream?modelId={modelId}
    through the generated Python client.  Yields dicts:
        {"text": str, "finish_reason": str | None}

    Raises StreamError on backend errors or empty responses.
    """

    logger.info(
        "Streaming: battle=%s, round=%s, model=%s",
        battle_session.battle_id,
        battle_session.round_id,
        model_id,
    )

    with create_authenticated_api_client(cookies) as api_client:
        battle_api = BattleApi(api_client)
        response = battle_api.create_battle_round_completion_without_preload_content(
            battle_id=battle_session.battle_id,
            round_id=battle_session.round_id,
            model_id=model_id,
        )

        try:
            text = ""
            finish_reason = None
            buf = ""

            for chunk_bytes in response.stream(amt=4096):
                buf += chunk_bytes.decode("utf-8")
                # SSE lines are separated by \n; process complete lines
                while "\n" in buf:
                    line, buf = buf.split("\n", 1)
                    line = line.strip()
                    if not line.startswith("data: "):
                        continue

                    data = json.loads(line[6:])
                    status = data.get("status")

                    if status == "error":
                        error_msg = data.get("errorMessage", "Unknown error")
                        logger.error(
                            "Stream error for model %s: %s", model_id, error_msg
                        )
                        raise StreamError(error_msg)

                    if status == "streaming":
                        text += data.get("content", "")
                        yield {"text": text, "finish_reason": None}

                    elif status == "complete":
                        finish_reason = data.get("finishReason", "stop")
                        usage = data.get("usage")

                        # Fallback truncation detection
                        if finish_reason == "stop" and usage:
                            completion_tokens = usage.get("completionTokens", 0)
                            if completion_tokens >= max_new_tokens:
                                logger.warning(
                                    "Truncation detected: %d/%d tokens",
                                    completion_tokens,
                                    max_new_tokens,
                                )
                                finish_reason = "length"

                        yield {"text": text, "finish_reason": finish_reason}

            # Empty response detection
            if not text.strip():
                logger.error("Empty response from model %s", model_id)
                raise StreamError("Empty response")

        finally:
            response.release_conn()
