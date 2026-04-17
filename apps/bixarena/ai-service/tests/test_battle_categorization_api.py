# coding: utf-8

from fastapi.testclient import TestClient


from bixarena_ai_service.models.basic_error import BasicError  # noqa: F401
from bixarena_ai_service.models.battle_categorization import BattleCategorization  # noqa: F401
from bixarena_ai_service.models.battle_categorization_request import (
    BattleCategorizationRequest,
)  # noqa: F401


def test_categorize_battle(client: TestClient):
    """Test case for categorize_battle

    Categorize a battle
    """
    battle_categorization_request = {
        "prompts": ["prompts", "prompts", "prompts", "prompts", "prompts"]
    }

    headers = {}
    # uncomment below to make a request
    # response = client.request(
    #    "POST",
    #    "/categorize-battle",
    #    headers=headers,
    #    json=battle_categorization_request,
    # )

    # uncomment below to assert the status code of the HTTP response
    # assert response.status_code == 200
