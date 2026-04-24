# coding: utf-8

from fastapi.testclient import TestClient


from bixarena_ai_service.models.basic_error import BasicError  # noqa: F401
from bixarena_ai_service.models.prompt_categorization import PromptCategorization  # noqa: F401
from bixarena_ai_service.models.prompt_categorization_request import (
    PromptCategorizationRequest,
)  # noqa: F401


def test_categorize_prompt(client: TestClient):
    """Test case for categorize_prompt

    Categorize a prompt
    """
    prompt_categorization_request = {"prompt": "prompt"}

    headers = {}
    # uncomment below to make a request
    # response = client.request(
    #    "POST",
    #    "/categorize-prompt",
    #    headers=headers,
    #    json=prompt_categorization_request,
    # )

    # uncomment below to assert the status code of the HTTP response
    # assert response.status_code == 200
