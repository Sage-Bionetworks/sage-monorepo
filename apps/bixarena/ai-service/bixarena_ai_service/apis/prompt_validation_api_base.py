# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from pydantic import Field
from typing_extensions import Annotated
from bixarena_ai_service.models.basic_error import BasicError
from bixarena_ai_service.models.prompt_validation import PromptValidation


class BasePromptValidationApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BasePromptValidationApi.subclasses = BasePromptValidationApi.subclasses + (cls,)

    async def validate_prompt(
        self,
        prompt: Annotated[
            str,
            Field(
                min_length=1,
                strict=True,
                max_length=10000,
                description="The prompt text to validate",
            ),
        ],
    ) -> PromptValidation:
        """Validates whether a prompt is biomedically related and returns a confidence score (requires authentication)"""
        ...
