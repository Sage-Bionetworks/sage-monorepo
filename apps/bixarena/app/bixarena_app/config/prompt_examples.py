"""
Biomedical Prompt Examples Manager for BixArena
"""

import random


class BiomedicalPrompts:
    """Manager for biomedical prompt examples"""

    PROMPTS = [
        "Do mitochondria play a role in remodelling lace plant leaves during programmed cell death?",
        "Do mutations causing low HDL-C promote increased carotid intima-media thickness?",
        "30-Day and 1-year mortality in emergency general surgery laparotomies: an area of concern and need for improvement?",
        "Differentiation of nonalcoholic from alcoholic steatohepatitis: are routine laboratory markers useful?",
        "Can tailored interventions increase mammography use among HMO women?",
        "Are the long-term results of the transanal pull-through equal to those of the transabdominal pull-through?",
        "Is there still a need for living-related liver transplantation in children?",
        "Therapeutic anticoagulation in the trauma patient: is it safe?",
        "Acute respiratory distress syndrome in children with malignancy--can we predict outcome?",
        "Double balloon enteroscopy: is it efficacious and safe in a community setting?",
    ]

    def get_sample_prompts(self, num_prompts: int = 3) -> list[str]:
        """Get a random sample of prompts"""
        num_to_sample = min(num_prompts, len(self.PROMPTS))
        return random.sample(self.PROMPTS, num_to_sample)

    def truncate_prompt(self, prompt: str, max_chars: int = 240) -> str:
        """Truncate a prompt for display purposes"""
        if len(prompt) <= max_chars:
            return prompt
        return prompt[:max_chars].rsplit(" ", 1)[0] + "..."

    def get_display_prompts(
        self, num_prompts: int = 3, max_chars: int = 240
    ) -> list[tuple[str, str]]:
        """Get prompts ready for display with truncation

        Returns:
            List of tuples: (display_text, full_prompt)
        """
        sample_prompts = self.get_sample_prompts(num_prompts)
        return [
            (self.truncate_prompt(prompt, max_chars), prompt)
            for prompt in sample_prompts
        ]


# Global instance
biomedical_prompts = BiomedicalPrompts()


def get_prompt_manager() -> BiomedicalPrompts:
    """Get the global prompt manager instance"""
    return biomedical_prompts
