"""
Command-line interface for running model evaluation with mock data.
"""

import argparse
import sys
import time

from bixarena_tools.evaluation import (
    compute_scores_and_bootstrap,
    format_leaderboard_output,
    update_vote_counts_in_leaderboard,
)
from bixarena_tools.mock_data import (
    SimConfig,
    print_simulation_summary,
    simulate_battles,
)


def main():
    """Main CLI entry point."""
    parser = argparse.ArgumentParser(
        description="Run model evaluation with mock vote data"
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
        default=1000,
        help="Number of bootstrap samples (default: 1000)",
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

    # Configure logging level
    if args.verbose:
        import logging

        logging.basicConfig(level=logging.DEBUG)

    print("🎯 BixArena Model Evaluation")
    print("=" * 40)

    # Start total timing
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
    votes = simulate_battles(config)
    print_simulation_summary(votes, config)

    # Run evaluation
    print("\n🧮 Computing ranking scores and bootstrapping...")
    eval_start = time.time()
    results, confidence_intervals = compute_scores_and_bootstrap(
        votes, num_bootstrap=args.num_bootstrap
    )
    eval_time = time.time() - eval_start

    if results.empty:
        print("❌ Failed to compute ranking scores")
        sys.exit(1)

    # Format for leaderboard
    print("📋 Formatting leaderboard results...")
    leaderboard = format_leaderboard_output(results, confidence_intervals)
    leaderboard = update_vote_counts_in_leaderboard(leaderboard, votes)

    # Display results
    print("\n🏆 Model Ranking Leaderboard:")
    print("-" * 80)
    print(f"{'Rank':<6} {'Model':<12} {'Score':<10} {'95% CI':<20} {'Votes':<8}")
    print("-" * 80)

    for _, row in leaderboard.iterrows():
        rank = row["rank"]
        model = row["model_name"]
        score = row["bt_score"]  # Keep internal name for now
        ci_95 = row["ci_95"]
        vote_count = row["vote_count"]

        print(f"{rank:<6} {model:<12} {score:<10.3f} {ci_95:<20} {vote_count:<8}")

    # Calculate total runtime
    total_time = time.time() - total_start_time

    print("\n✅ Evaluation completed successfully!")
    print(f"⏱️  Total runtime: {total_time:.3f} seconds")
    print(f"   • Ranking evaluation including bootstrapping: {eval_time:.3f}s")

    top_model = leaderboard.iloc[0]
    print(
        f"   Top model: {top_model['model_name']} (Score: {top_model['bt_score']:.3f})"
    )


if __name__ == "__main__":
    main()
