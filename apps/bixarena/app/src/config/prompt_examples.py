"""
Biomedical Prompt Examples Manager for BixArena
"""

import re
import random
from typing import Any


class BiomedicalPrompts:
    """Manager for biomedical prompt examples"""

    PROMPTS = [
        "30-Day and 1-year mortality in emergency general surgery laparotomies: an area of concern and need for improvement?",
        "Do mitochondria play a role in remodelling lace plant leaves during programmed cell death?",
        "AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAAAAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAAAAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAAAAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA AAAA",
    ]

    def get_random_prompt(self) -> str:
        """Get a random prompt text"""
        return random.choice(self.PROMPTS)

    def search_prompts(self, query: str) -> list[dict[str, Any]]:
        """Search prompts by text content with input validation"""
        if not query or not query.strip():
            return []

        # Input validation
        query = query.strip()
        if len(query) > 200:  # Reasonable limit for search queries
            query = query[:200]

        # Basic sanitization - remove potentially harmful characters
        query = re.sub(r'[<>"\']', "", query)

        query_lower = query.lower()
        results = []

        for prompt in self.PROMPTS:
            if query_lower in prompt.lower():
                results.append({"text": prompt})

        return results

    def get_all_prompts(self) -> list[str]:
        """Get all prompts"""
        return self.PROMPTS


# Global instance
biomedical_prompts = BiomedicalPrompts()


def get_prompt_manager() -> BiomedicalPrompts:
    """Get the global prompt manager instance"""
    return biomedical_prompts
