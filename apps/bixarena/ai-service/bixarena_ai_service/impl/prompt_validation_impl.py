"""Runtime implementation for prompt validation endpoint.

This file lives under the ``impl`` package which is scanned dynamically by the
generated router (see ``apis/prompt_validation_api.py``). Any subclass of
``BasePromptValidationApi`` discovered at import time is used to service incoming
requests. By placing our implementation here we avoid modifying generated
code that would be overwritten on regeneration.
"""

from __future__ import annotations

import logging

from bixarena_ai_service.apis.prompt_validation_api_base import BasePromptValidationApi
from bixarena_ai_service.config import get_settings
from bixarena_ai_service.models.prompt_validation import PromptValidation

logger = logging.getLogger(__name__)


class PromptValidationApiImpl(BasePromptValidationApi):
    """Concrete prompt validation implementation.

    This implementation provides a placeholder that returns a static confidence
    value. In the future, this will be replaced with actual ML-based validation
    using biomedical NLP models.
    """

    async def validate_prompt(
        self,
        prompt: str,
    ) -> PromptValidation:
        """Validate whether a prompt is biomedically related.

        Args:
            prompt: The prompt text to validate

        Returns:
            PromptValidation: Validation result with confidence score
        """
        # Get settings
        settings = get_settings()

        logger.info("Prompt validation requested")
        logger.debug(f"Prompt text (length={len(prompt)}): {prompt[:100]}...")

        # TODO: Replace with actual ML-based validation
        # For now, return a static confidence value
        static_confidence = 0.75

        validation_result = PromptValidation(
            prompt=prompt,
            confidence=static_confidence,
            is_biomedical=static_confidence
            >= settings.prompt_validation_confidence_threshold,
        )

        logger.info(
            f"Validation result: confidence={validation_result.confidence}, "
            f"isBiomedical={validation_result.is_biomedical}"
        )

        return validation_result
