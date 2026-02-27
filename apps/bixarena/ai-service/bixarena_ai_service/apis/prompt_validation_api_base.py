# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from bixarena_ai_service.models.basic_error import BasicError
from bixarena_ai_service.models.prompt_validation import PromptValidation
from bixarena_ai_service.models.prompt_validation_request import PromptValidationRequest


class BasePromptValidationApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BasePromptValidationApi.subclasses = BasePromptValidationApi.subclasses + (cls,)

    async def validate_prompt(
        self,
        prompt_validation_request: PromptValidationRequest,
    ) -> PromptValidation:
        """Validates whether a prompt is biomedically related and returns a confidence score (requires authentication)"""
        ...
