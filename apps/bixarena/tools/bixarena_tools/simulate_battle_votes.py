"""
Mock data generation for Bradley-Terry evaluation testing.
"""

import random
import uuid
from dataclasses import dataclass


@dataclass
class SimConfig:
    """Configuration for battle simulation."""

    num_models: int = 20
    num_votes: int = 1000
    tie_probability: float = 0.05  # Base probability of tie outcomes
    random_seed: int = 123


def simulate_models(num_models: int, seed: int) -> dict[str, dict]:
    """
    Simulate models with minimal props.

    Args:
        num_models: Number of models to generate
        seed: Random seed for reproducible UUIDs

    Returns:
        Dict mapping model_name -> model_info with id, name, etc.
    """
    random.seed(seed)
    models = {}

    for i in range(1, num_models + 1):
        model_name = f"model_{i:02d}"
        # Generate deterministic UUID based on seed and model name
        random.seed(seed + i)  # Make UUID deterministic but unique per model
        model_id = str(uuid.UUID(int=random.getrandbits(128)))

        models[model_name] = {
            "id": model_id,
            "name": model_name,
            "displayName": f"Test Model {i:02d}",
            "license": "Unknown",
        }

    return models


def simulate_votes(config: SimConfig) -> tuple[list[dict], dict[str, dict]]:
    """
    Simulate votes with models having different base win rates.

    Models are assigned base win rates (40%-90%), with higher numbered
    models having higher rates. When two models compete, the one with
    the higher base rate is more likely to win (normalized probability).

    Args:
        config: Simulation configuration

    Returns:
        Tuple of (votes, models) where:
        - votes: List of vote dictionaries in BixArena format
        - models: Dict of model_name -> model_info
    """
    random.seed(config.random_seed)

    # Generate models with UUIDs
    models = simulate_models(config.num_models, config.random_seed)
    model_names = list(models.keys())

    # Assign base win rates to models (40%-90% range)
    # Higher numbered models have higher base win rates
    model_base_rates = {}
    for i, model_name in enumerate(sorted(model_names)):
        # Create gradient: early models ~40% win rate, later models ~90%
        base_rate = 0.40 + (i / (config.num_models - 1)) * 0.50
        # Add small random noise (±5%)
        noise = random.gauss(0, 0.05)
        model_base_rates[model_name] = max(0.1, min(0.95, base_rate + noise))

    votes = []

    for _ in range(config.num_votes):
        # Select two different models randomly
        model_a, model_b = random.sample(model_names, 2)

        # Simple approach: model with higher base rate is more likely to win
        # Normalize so probabilities sum to 1
        rate_a = model_base_rates[model_a]
        rate_b = model_base_rates[model_b]
        total_rate = rate_a + rate_b
        prob_a_wins = rate_a / total_rate

        # Determine outcome
        rand_outcome = random.random()

        if rand_outcome < config.tie_probability:
            preference = "tie"
        elif rand_outcome < (
            config.tie_probability + prob_a_wins * (1 - config.tie_probability)
        ):
            preference = "model_a"
        else:
            preference = "model_b"

        vote = {"model_a": model_a, "model_b": model_b, "preference": preference}
        votes.append(vote)

    return votes, models


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
    votes, models = simulate_votes(config)
    print_simulation_summary(votes, config)
