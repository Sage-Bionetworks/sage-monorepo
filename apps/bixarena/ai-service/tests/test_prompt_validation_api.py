# coding: utf-8

from fastapi.testclient import TestClient


from pydantic import Field  # noqa: F401
from typing_extensions import Annotated  # noqa: F401
from bixarena_ai_service.models.basic_error import BasicError  # noqa: F401
from bixarena_ai_service.models.prompt_validation import PromptValidation  # noqa: F401


def test_validate_prompt(client: TestClient):
    """Test case for validate_prompt

    Validate biomedical prompt
    """
    params = [("prompt", "prompt_example")]
    headers = {}
    # uncomment below to make a request
    # response = client.request(
    #    "GET",
    #    "/validate-prompt",
    #    headers=headers,
    #    params=params,
    # )

    # uncomment below to assert the status code of the HTTP response
    # assert response.status_code == 200
