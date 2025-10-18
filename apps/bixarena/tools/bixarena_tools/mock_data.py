"""
Mock data generation for Bradley-Terry evaluation testing.
"""

import random
from dataclasses import dataclass


@dataclass
class SimConfig:
    """Configuration for battle simulation."""

    num_models: int = 20
    num_votes: int = 1000
    tie_probability: float = 0.05  # Base probability of tie outcomes
    random_seed: int = 123


def simulate_battles(config: SimConfig) -> list[dict]:
    """
    Simulate battle outcomes with simple random preferences.

    Args:
        config: Simulation configuration

    Returns:
        List of vote dictionaries in BixArena format
    """
    random.seed(config.random_seed)

    # Generate model names
    models = [f"model_{i:02d}" for i in range(1, config.num_models + 1)]

    votes = []

    for _ in range(config.num_votes):
        # Select two different models randomly
        model_a, model_b = random.sample(models, 2)

        # Simple random outcome determination
        rand_outcome = random.random()

        if rand_outcome < config.tie_probability:
            preference = "tie"
        elif rand_outcome < 0.5 + (config.tie_probability / 2):
            preference = "model_a"
        else:
            preference = "model_b"

        vote = {"model_a": model_a, "model_b": model_b, "preference": preference}
        votes.append(vote)

    return votes


def print_simulation_summary(votes: list[dict], config: SimConfig):
    """Print a summary of the simulated votes."""

    print("\n=== Simulation Summary ===")
    print(f"Models: {config.num_models}")
    print(f"Total votes: {len(votes)}")

    # Count outcomes
    outcome_counts = {}
    model_involvement = {}

    for vote in votes:
        preference = vote["preference"]
        outcome_counts[preference] = outcome_counts.get(preference, 0) + 1

        for model in [vote["model_a"], vote["model_b"]]:
            model_involvement[model] = model_involvement.get(model, 0) + 1

    print("votes per model:")
    for model, count in sorted(model_involvement.items()):
        print(f"  {model}: {count}")


if __name__ == "__main__":
    config = SimConfig(num_models=20, num_votes=1000)
    votes = simulate_battles(config)
    print_simulation_summary(votes, config)
