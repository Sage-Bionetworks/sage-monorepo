"""
Biomedical Prompt Examples Manager for BixArena
"""

import random
import time
from typing import List, Dict, Any


class BiomedicalPrompts:
    """Manager for biomedical prompt examples"""

    PROMPTS = [
        {
            "category": "Clinical",
            "text": "Explain the mechanism of action of pembrolizumab and its clinical applications in treating melanoma and lung cancer.",
            "trending": True,
        },
        {
            "category": "Drug Discovery",
            "text": "What are the key considerations when designing PROTAC molecules for targeted protein degradation in cancer therapy?",
            "trending": False,
        },
        {
            "category": "Diagnostics",
            "text": "Compare the sensitivity and specificity of liquid biopsy versus tissue biopsy for detecting EGFR mutations in NSCLC patients.",
            "trending": False,
        },
        {
            "category": "Research",
            "text": "How do mRNA vaccines work at the molecular level, and what are the advantages over traditional vaccine approaches?",
            "trending": True,
        },
        {
            "category": "Pathology",
            "text": "Describe the pathophysiology of Alzheimer's disease, focusing on amyloid plaques and tau protein tangles.",
            "trending": False,
        },
        {
            "category": "Clinical",
            "text": "What are the clinical manifestations and treatment options for cytokine release syndrome in CAR-T cell therapy?",
            "trending": False,
        },
        {
            "category": "Drug Discovery",
            "text": "Explain the concept of allosteric drug binding and provide examples of successful allosteric inhibitors in cancer treatment.",
            "trending": False,
        },
        {
            "category": "Diagnostics",
            "text": "What are the advantages and limitations of using artificial intelligence in medical imaging for early cancer detection?",
            "trending": False,
        },
        {
            "category": "Research",
            "text": "What role do exosomes play in cancer metastasis and how can they be targeted therapeutically?",
            "trending": False,
        },
        {
            "category": "Clinical",
            "text": "Compare the efficacy and safety profiles of different checkpoint inhibitor combinations in treating advanced melanoma.",
            "trending": False,
        },
    ]

    def __init__(self):
        self.current_rolling_index = 0
        self.last_roll_time = time.time()
        self.roll_interval = 4.0  # seconds

    def get_random_prompt(self) -> str:
        """Get a random prompt text"""
        return random.choice(self.PROMPTS)["text"]

    def get_rolling_prompt(self) -> str:
        """Get the current rolling prompt"""
        current_time = time.time()
        if current_time - self.last_roll_time >= self.roll_interval:
            self.current_rolling_index = (self.current_rolling_index + 1) % len(
                self.PROMPTS
            )
            self.last_roll_time = current_time

        return self.PROMPTS[self.current_rolling_index]["text"]

    def search_prompts(self, query: str) -> List[Dict[str, Any]]:
        """Search prompts by text content"""
        if not query.strip():
            return []

        query_lower = query.lower()
        results = []

        for prompt in self.PROMPTS:
            if (
                query_lower in prompt["text"].lower()
                or query_lower in prompt["category"].lower()
            ):
                results.append(prompt)

        return results

    def get_all_prompts(self) -> List[Dict[str, Any]]:
        """Get all prompts"""
        return self.PROMPTS

    def get_categories(self) -> List[str]:
        """Get unique categories"""
        return list(set(prompt["category"] for prompt in self.PROMPTS))


# Global instance
biomedical_prompts = BiomedicalPrompts()


def get_prompt_manager() -> BiomedicalPrompts:
    """Get the global prompt manager instance"""
    return biomedical_prompts
