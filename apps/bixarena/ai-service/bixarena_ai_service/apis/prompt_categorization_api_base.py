# coding: utf-8

from typing import ClassVar, Dict, List, Tuple  # noqa: F401

from bixarena_ai_service.models.basic_error import BasicError
from bixarena_ai_service.models.prompt_categorization import PromptCategorization
from bixarena_ai_service.models.prompt_categorization_request import (
    PromptCategorizationRequest,
)


class BasePromptCategorizationApi:
    subclasses: ClassVar[Tuple] = ()

    def __init_subclass__(cls, **kwargs):
        super().__init_subclass__(**kwargs)
        BasePromptCategorizationApi.subclasses = (
            BasePromptCategorizationApi.subclasses + (cls,)
        )

    async def categorize_prompt(
        self,
        prompt_categorization_request: PromptCategorizationRequest,
    ) -> PromptCategorization:
        """Classifies a single prompt into up to 3 biomedical subject categories using an LLM. The categories array may be empty when the classifier did not assign any category. Requires authentication."""
        ...
