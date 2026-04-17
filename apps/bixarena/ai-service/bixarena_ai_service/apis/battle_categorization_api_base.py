# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from bixarena_ai_service.models.basic_error import BasicError
from bixarena_ai_service.models.battle_categorization import BattleCategorization
from bixarena_ai_service.models.battle_categorization_request import (
    BattleCategorizationRequest,
)


class BaseBattleCategorizationApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseBattleCategorizationApi.subclasses = (
            BaseBattleCategorizationApi.subclasses + (cls,)
        )

    async def categorize_battle(
        self,
        battle_categorization_request: BattleCategorizationRequest,
    ) -> BattleCategorization:
        """Classifies the prompts of a battle conversation into up to 3 biomedical subject categories using an LLM. The categories array may be empty when the classifier did not assign any category. Requires authentication."""
        ...
