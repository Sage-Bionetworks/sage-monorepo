# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from bixarena_ai_service.models.basic_error import BasicError
from bixarena_ai_service.models.battle_validation import BattleValidation
from bixarena_ai_service.models.battle_validation_request import BattleValidationRequest


class BaseBattleValidationApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BaseBattleValidationApi.subclasses = BaseBattleValidationApi.subclasses + (cls,)

    async def validate_battle(
        self,
        battle_validation_request: BattleValidationRequest,
    ) -> BattleValidation:
        """Validates whether a battle&#39;s conversation (all user prompts) is biomedically related. Returns a confidence score. Requires authentication."""
        ...
