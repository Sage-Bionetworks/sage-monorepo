# coding: utf-8

from fastapi.testclient import TestClient


from bixarena_ai_service.models.basic_error import BasicError  # noqa: F401
from bixarena_ai_service.models.model_chat_completion_chunk import (
    ModelChatCompletionChunk,
)  # noqa: F401
from bixarena_ai_service.models.model_chat_request import ModelChatRequest  # noqa: F401
from bixarena_ai_service.models.rate_limit_error import RateLimitError  # noqa: F401


def test_create_chat_completion(client: TestClient):
    """Test case for create_chat_completion

    Create a chat completion
    """
    model_chat_request = {
        "model_id": "1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d",
        "messages": [
            {"role": "user", "content": "What is the capital of France?"},
            {"role": "user", "content": "What is the capital of France?"},
        ],
    }

    headers = {}
    # uncomment below to make a request
    # response = client.request(
    #    "POST",
    #    "/chat/completions",
    #    headers=headers,
    #    json=model_chat_request,
    # )

    # uncomment below to assert the status code of the HTTP response
    # assert response.status_code == 200
