# coding: utf-8

from fastapi.testclient import TestClient


from bixarena_ai_service.models.basic_error import BasicError  # noqa: F401
from bixarena_ai_service.models.prompt_validation import PromptValidation  # noqa: F401
from bixarena_ai_service.models.prompt_validation_request import PromptValidationRequest  # noqa: F401


def test_validate_prompt(client: TestClient):
    """Test case for validate_prompt

    Validate biomedical prompt
    """
    prompt_validation_request = {"prompt": "prompt"}

    headers = {}
    # uncomment below to make a request
    # response = client.request(
    #    "POST",
    #    "/validate-prompt",
    #    headers=headers,
    #    json=prompt_validation_request,
    # )

    # uncomment below to assert the status code of the HTTP response
    # assert response.status_code == 200
