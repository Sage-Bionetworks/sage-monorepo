# coding: utf-8

from fastapi.testclient import TestClient


from bixarena_ai_service.models.basic_error import BasicError  # noqa: F401
from bixarena_ai_service.models.battle_validation import BattleValidation  # noqa: F401
from bixarena_ai_service.models.battle_validation_request import BattleValidationRequest  # noqa: F401


def test_validate_battle(client: TestClient):
    """Test case for validate_battle

    Validate biomedical battle
    """
    battle_validation_request = {
        "prompts": ["prompts", "prompts", "prompts", "prompts", "prompts"]
    }

    headers = {}
    # uncomment below to make a request
    # response = client.request(
    #    "POST",
    #    "/validate-battle",
    #    headers=headers,
    #    json=battle_validation_request,
    # )

    # uncomment below to assert the status code of the HTTP response
    # assert response.status_code == 200
