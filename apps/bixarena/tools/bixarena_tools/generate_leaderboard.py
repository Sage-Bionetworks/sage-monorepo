"""
Generate BixArena leaderboard data using Bradley-Terry evaluation with mock vote data.
"""

import argparse
import json
import logging
import sys
import time

from bixarena_tools.mock_data import (
    SimConfig,
    print_simulation_summary,
    simulate_battles,
)
from bixarena_tools.ranking_metric import compute_leaderboard_bt

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


def main():
    """Main CLI entry point."""
    parser = argparse.ArgumentParser(
        description="Generate BixArena leaderboard data using Bradley-Terry evaluation"
    )

    parser.add_argument(
        "--num-models",
        type=int,
        default=10,
        help="Number of models to simulate (default: 10)",
    )
    parser.add_argument(
        "--num-votes",
        type=int,
        default=500,
        help="Number of votes to generate (default: 500)",
    )
    parser.add_argument(
        "--num-bootstrap",
        type=int,
        default=5000,
        help="Number of bootstrap iterations for resampling (default: 5000)",
    )
    parser.add_argument(
        "--tie-probability",
        type=float,
        default=0.05,
        help="Probability of tie outcomes (default: 0.05)",
    )
    parser.add_argument(
        "--random-seed",
        type=int,
        default=42,
        help="Random seed for reproducibility (default: 42)",
    )
    parser.add_argument("--verbose", action="store_true", help="Enable verbose logging")

    args = parser.parse_args()

    if args.verbose:
        logging.basicConfig(level=logging.DEBUG)

    print("🎯 BixArena Model Evaluation")
    print("=" * 40)

    # Start timing
    total_start_time = time.time()

    # Create simulation configuration
    config = SimConfig(
        num_models=args.num_models,
        num_votes=args.num_votes,
        tie_probability=args.tie_probability,
        random_seed=args.random_seed,
    )

    # Generate mock votes
    print("📊 Generating mock vote data...")
    votes, models = simulate_battles(config)
    print_simulation_summary(votes, config, models)

    # Run evaluation
    print("\n🧮 Computing ranking scores and bootstrapping...")
    eval_start = time.time()
    leaderboard_entries = compute_leaderboard_bt(
        votes, models, num_bootstrap=args.num_bootstrap
    )
    eval_time = time.time() - eval_start

    if not leaderboard_entries:
        print("❌ Failed to compute ranking scores")
        sys.exit(1)

    # Display results
    print("\n🏆 Model Ranking Leaderboard:")
    print("-" * 80)
    print(f"{'Rank':<6} {'Model':<12} {'Score':<10} {'95% CI':<20} {'Votes':<8}")
    print("-" * 80)

    for entry in leaderboard_entries:
        rank = entry["rank"]
        model = entry["modelName"]
        score = entry["btScore"]
        ci_95 = f"[{entry['bootstrapQ025']:.1f}, {entry['bootstrapQ975']:.1f}]"
        vote_count = entry["voteCount"]

        print(f"{rank:<6} {model:<12} {score:<10.3f} {ci_95:<20} {vote_count:<8}")

    # Calculate total runtime
    total_time = time.time() - total_start_time

    # Format and print data for API integration
    print("\n📤 Formatted LeaderboardEntry data for API:")
    print("-" * 50)

    # Remove bootstrap fields for API output (they're not in the schema)
    api_entries = []
    for entry in leaderboard_entries:
        api_entry = {
            k: v
            for k, v in entry.items()
            if k not in ["bootstrapQ025", "bootstrapQ975"]
        }
        api_entries.append(api_entry)

    print(json.dumps(api_entries, indent=2))

    print("\n✅ Evaluation completed successfully!")
    print(f"⏱️  Total runtime: {total_time:.3f} seconds")
    print(f"   • Ranking evaluation including bootstrapping: {eval_time:.3f}s")

    top_model = leaderboard_entries[0]
    print(f"   Top model: {top_model['modelName']} (Score: {top_model['btScore']:.3f})")


if __name__ == "__main__":
    main()
