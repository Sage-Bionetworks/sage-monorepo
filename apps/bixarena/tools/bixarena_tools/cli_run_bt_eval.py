"""
Command-line interface for running Bradley-Terry evaluation with mock data.
"""

import argparse
import json
import sys
from pathlib import Path

from .bt_eval import (
    compute_bt_scores_and_bootstrap,
    format_leaderboard_output,
    update_vote_counts_in_leaderboard,
)
from .mock_bt_data import SimConfig, print_simulation_summary, simulate_battles


def main():
    """Main CLI entry point."""
    parser = argparse.ArgumentParser(
        description="Run Bradley-Terry evaluation with mock vote data"
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

    parser.add_argument(
        "--output-file", type=str, help="Optional output file for results (JSON format)"
    )

    parser.add_argument("--verbose", action="store_true", help="Enable verbose logging")

    args = parser.parse_args()

    # Configure logging level
    if args.verbose:
        import logging

        logging.basicConfig(level=logging.DEBUG)

    print("🎯 BixArena Bradley-Terry Evaluation")
    print("=" * 40)

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

    # Run BT evaluation
    print("\n🧮 Computing Bradley-Terry scores...")
    bt_results, confidence_intervals = compute_bt_scores_and_bootstrap(
        votes, num_bootstrap=args.num_bootstrap
    )

    if bt_results.empty:
        print("❌ Failed to compute BT scores")
        sys.exit(1)

    # Format for leaderboard
    print("📋 Formatting leaderboard results...")
    leaderboard = format_leaderboard_output(bt_results, confidence_intervals)
    leaderboard = update_vote_counts_in_leaderboard(leaderboard, votes)

    # Display results
    print("\n🏆 Bradley-Terry Leaderboard Results:")
    print("-" * 80)
    print(f"{'Rank':<6} {'Model':<12} {'BT Score':<10} {'95% CI':<20} {'Votes':<8}")
    print("-" * 80)

    for _, row in leaderboard.iterrows():
        rank = row["rank"]
        model = row["model_name"]
        bt_score = row["bt_score"]
        ci_95 = row["ci_95"]
        vote_count = row["vote_count"]

        print(f"{rank:<6} {model:<12} {bt_score:<10.3f} {ci_95:<20} {vote_count:<8}")

    # Save to file if requested
    if args.output_file:
        output_data = {
            "config": {
                "num_models": config.num_models,
                "num_votes": config.num_votes,
                "num_bootstrap": args.num_bootstrap,
                "tie_probability": config.tie_probability,
                "random_seed": config.random_seed,
            },
            "leaderboard": leaderboard.to_dict("records"),
            "bt_scores": bt_results.to_dict("records"),
            "summary": {
                "total_votes": len(votes),
                "total_models": len(bt_results),
                "top_model": leaderboard.iloc[0]["model_name"]
                if not leaderboard.empty
                else None,
                "top_score": leaderboard.iloc[0]["bt_score"]
                if not leaderboard.empty
                else None,
            },
        }

        output_path = Path(args.output_file)
        output_path.parent.mkdir(parents=True, exist_ok=True)

        with open(output_path, "w") as f:
            json.dump(output_data, f, indent=2, default=str)

        print(f"\n💾 Results saved to: {output_path}")

    print("\n✅ Evaluation completed successfully!")
    top_model = leaderboard.iloc[0]
    print(
        f"   Top model: {top_model['model_name']} "
        f"(BT Score: {top_model['bt_score']:.3f})"
    )


if __name__ == "__main__":
    main()
