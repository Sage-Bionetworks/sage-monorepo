"""
Biomedical Prompt Examples Manager for BixArena
"""

import re
import random
from typing import List, Dict, Any


class BiomedicalPrompts:
    """Manager for biomedical prompt examples"""

    PROMPTS = [
        "Explain the mechanism of action of pembrolizumab and its clinical applications in treating melanoma and lung cancer.",
        "What are the key considerations when designing PROTAC molecules for targeted protein degradation in cancer therapy?",
        "Compare the sensitivity and specificity of liquid biopsy versus tissue biopsy for detecting EGFR mutations in NSCLC patients.",
        "How do mRNA vaccines work at the molecular level, and what are the advantages over traditional vaccine approaches?",
        "Describe the pathophysiology of Alzheimer's disease, focusing on amyloid plaques and tau protein tangles.",
        "What are the clinical manifestations and treatment options for cytokine release syndrome in CAR-T cell therapy?",
        "Explain the concept of allosteric drug binding and provide examples of successful allosteric inhibitors in cancer treatment.",
        "What are the advantages and limitations of using artificial intelligence in medical imaging for early cancer detection?",
        "What role do exosomes play in cancer metastasis and how can they be targeted therapeutically?",
        "Compare the efficacy and safety profiles of different checkpoint inhibitor combinations in treating advanced melanoma.",
    ]

    def get_random_prompt(self) -> str:
        """Get a random prompt text"""
        return random.choice(self.PROMPTS)

    def search_prompts(self, query: str) -> List[Dict[str, Any]]:
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

    def get_all_prompts(self) -> List[str]:
        """Get all prompts"""
        return self.PROMPTS


# Global instance
biomedical_prompts = BiomedicalPrompts()


def get_prompt_manager() -> BiomedicalPrompts:
    """Get the global prompt manager instance"""
    return biomedical_prompts
