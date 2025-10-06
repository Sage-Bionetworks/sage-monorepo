"""
Example Prompts Manager for BixArena
"""

import logging
import random

from bixarena_api_client import ApiClient, Configuration, ExamplePromptApi
from bixarena_api_client.exceptions import ApiException
from bixarena_api_client.models.example_prompt_search_query import (
    ExamplePromptSearchQuery,
)

logger = logging.getLogger(__name__)


def fetch_example_prompts():
    """Fetch example prompts from the BixArena API"""
    try:
        # Configure the API client
        configuration = Configuration(host="http://bixarena-api:8112/v1")

        # Create API client and example prompt API instance
        with ApiClient(configuration) as api_client:
            api_instance = ExamplePromptApi(api_client)

            # Create search query to get active prompts only
            search_query = ExamplePromptSearchQuery(
                page_number=0,
                page_size=10,  # Get more than we need so we can randomly sample
                active=True,
            )

            # Fetch example prompts
            response = api_instance.list_example_prompts(
                example_prompt_search_query=search_query
            )
            return [prompt.question for prompt in response.example_prompts]

    except ApiException as e:
        logger.error(f"API Exception when fetching example prompts: {e}")
        # Fallback to dummy prompts if API fails
        return _get_dummy_prompts()
    except Exception as e:
        logger.error(f"Exception when fetching example prompts: {e}")
        # Fallback to dummy prompts if API fails
        return _get_dummy_prompts()


def _get_dummy_prompts():
    """Get fallback hardcoded prompts when API is unavailable"""
    return [
        (
            "Do mitochondria play a role in remodelling lace plant leaves "
            "during programmed cell death?"
        ),
        (
            "Do mutations causing low HDL-C promote increased carotid "
            "intima-media thickness?"
        ),
        "Can tailored interventions increase mammography use among HMO women?",
        (
            "30-Day and 1-year mortality in emergency general surgery "
            "laparotomies: an area of concern and need for improvement?"
        ),
        (
            "Differentiation of nonalcoholic from alcoholic steatohepatitis: "
            "are routine laboratory markers useful?"
        ),
        (
            "Are the long-term results of the transanal pull-through equal to "
            "those of the transabdominal pull-through?"
        ),
        "Is there still a need for living-related liver transplantation in children?",
        "Therapeutic anticoagulation in the trauma patient: is it safe?",
        (
            "Acute respiratory distress syndrome in children with malignancy--"
            "can we predict outcome?"
        ),
        (
            "Double balloon enteroscopy: is it efficacious and safe in a "
            "community setting?"
        ),
    ]


def get_display_example_prompts(num_prompts: int = 3):
    """Get prompts for display.

    We no longer truncate in Python; CSS clamps to 3 lines in the UI. Each tuple is
    (display_text, full_prompt) where both are identical.
    """
    all_prompts = fetch_example_prompts()
    sample_prompts = random.sample(all_prompts, min(num_prompts, len(all_prompts)))
    return [(prompt, prompt) for prompt in sample_prompts]
