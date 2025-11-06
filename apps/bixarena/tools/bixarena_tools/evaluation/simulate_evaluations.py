"""
Generate simulated battle evaluations based on model information.

This script fetches model information from the database and generates synthetic
battles with realistic evaluations based on model performance characteristics
that align with the Bradley-Terry ranking metrics used in rank_battle.py.
"""

import argparse
import os
import random
import sys
import uuid
from datetime import UTC, datetime
from typing import Any

from rich.console import Console
from rich.table import Table

from bixarena_tools.evaluation.db_helper import (
    DatabaseConfig,
    fetch_active_models,
    get_db_connection,
    insert_battle_evaluations_batch,
    insert_battles_batch,
)

console = Console()


def calculate_model_strength(model_name: str, seed: int = 42) -> float:
    """
    Calculate a consistent strength score for a model based on its name.

    This creates a reproducible but varied strength distribution across models.
    Uses the model name hash to ensure consistency across runs.

    Args:
        model_name: Name of the model
        seed: Random seed for reproducibility

    Returns:
        Strength score between 0.3 and 0.95
    """
    # Use hash of model name for consistent but varied strengths
    # Create a separate Random instance to avoid affecting global state
    rng = random.Random(seed + hash(model_name))
    base_strength = rng.uniform(0.3, 0.95)
    return base_strength


def simulate_battle_outcome(
    model1_name: str,
    model2_name: str,
    tie_probability: float = 0.05,
    seed: int = 42,
) -> str:
    """
    Simulate a battle outcome based on model strengths.

    The stronger model is more likely to win, but there's randomness involved.
    This produces realistic data for Bradley-Terry ranking.

    Args:
        model1_name: Name of first model
        model2_name: Name of second model
        tie_probability: Base probability of a tie (default 0.05)
        seed: Random seed for reproducibility

    Returns:
        Outcome string: 'model1', 'model2', or 'tie'
    """
    # Get consistent model strengths
    strength1 = calculate_model_strength(model1_name, seed)
    strength2 = calculate_model_strength(model2_name, seed)

    # Normalize probabilities
    total_strength = strength1 + strength2
    prob_model1 = strength1 / total_strength

    # Generate outcome with tie probability
    rand_value = random.random()

    if rand_value < tie_probability:
        return "tie"
    elif rand_value < tie_probability + prob_model1 * (1 - tie_probability):
        return "model1"
    else:
        return "model2"


def simulate_battles_and_evaluations(
    models: list[dict[str, Any]],
    num_evaluations: int,
    user_id: str,
    tie_probability: float = 0.05,
    seed: int = 42,
) -> tuple[list[dict[str, Any]], list[dict[str, str]]]:
    """
    Simulate synthetic battles with evaluations.

    Args:
        models: List of model dictionaries from database
        num_evaluations: Number of battles/evaluations to generate
        user_id: User ID for the battles
        tie_probability: Probability of tie outcomes
        seed: Random seed for reproducibility

    Returns:
        Tuple of (battles, evaluations) where battles contain full battle data
        and evaluations contain battle_id and outcome
    """
    if not models or len(models) < 2:
        console.print("[red]Error: Need at least 2 models to generate battles[/red]")
        return [], []

    # Set random seed for reproducibility
    random.seed(seed)

    battles = []
    evaluations = []
    current_time = datetime.now(UTC)

    for i in range(num_evaluations):
        # Randomly select two different models
        model1, model2 = random.sample(models, 2)

        # Generate battle
        battle_id = str(uuid.uuid4())
        battle = {
            "id": battle_id,
            "title": f"Simulated Battle {i + 1}",
            "user_id": user_id,
            "model1_id": str(model1["id"]),
            "model2_id": str(model2["id"]),
            "model1_name": model1["name"],
            "model2_name": model2["name"],
            "created_at": current_time,
            "ended_at": current_time,
        }
        battles.append(battle)

        # Generate evaluation outcome
        outcome = simulate_battle_outcome(
            model1["name"],
            model2["name"],
            tie_probability=tie_probability,
            seed=seed,
        )

        evaluations.append(
            {
                "battle_id": battle_id,
                "outcome": outcome,
                "model1_name": model1["name"],
                "model2_name": model2["name"],
            }
        )

    return battles, evaluations


def display_evaluation_summary(evaluations: list[dict[str, str]]) -> None:
    """
    Display a summary table of generated evaluations.

    Args:
        evaluations: List of evaluation dictionaries
    """
    if not evaluations:
        console.print("[yellow]No evaluations to display[/yellow]")
        return

    # Count outcomes
    outcome_counts = {"model1": 0, "model2": 0, "tie": 0}
    for eval_data in evaluations:
        outcome_counts[eval_data["outcome"]] += 1

    # Create summary table
    table = Table(title="Evaluation Summary")
    table.add_column("Outcome", style="cyan")
    table.add_column("Count", justify="right", style="green")
    table.add_column("Percentage", justify="right", style="yellow")

    total = len(evaluations)
    for outcome, count in outcome_counts.items():
        percentage = (count / total) * 100
        table.add_row(outcome, str(count), f"{percentage:.1f}%")

    table.add_row("Total", str(total), "100.0%", style="bold")

    console.print(table)


def main():
    """Main entry point for simulation script."""
    parser = argparse.ArgumentParser(
        description="Generate simulated battle evaluations for BixArena"
    )
    parser.add_argument(
        "-n",
        "--num-evaluations",
        type=int,
        default=100,
        help="Number of evaluations to generate (default: 100)",
    )
    parser.add_argument(
        "--tie-probability",
        type=float,
        default=0.05,
        help="Probability of tie outcomes (default: 0.05)",
    )
    parser.add_argument(
        "--seed", type=int, default=42, help="Random seed for reproducibility"
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Simulate without writing to database",
    )

    args = parser.parse_args()

    # Use dummy user ID for simulations
    user_id = "00000000-0000-0000-0000-000000000000"

    # Create database config from env vars
    db_config = DatabaseConfig(
        host=os.getenv("POSTGRES_HOST"),
        port=int(os.getenv("POSTGRES_PORT", "5432")),
        database=os.getenv("POSTGRES_DB"),
        user=os.getenv("POSTGRES_USER"),
        password=os.getenv("POSTGRES_PASSWORD"),
    )

    try:
        console.print("[bold blue]BixArena Evaluation Simulator[/bold blue]")
        console.print("=" * 60)

        with get_db_connection(db_config) as conn:
            # Fetch active models
            console.print("[cyan]Fetching active models from database...[/cyan]")
            models = fetch_active_models(conn)
            console.print(f"[green]Found {len(models)} active models[/green]")

            if len(models) < 2:
                console.print(
                    "[red]Error: Need at least 2 active models to generate "
                    "battles.[/red]"
                )
                sys.exit(1)

            # Simulate battles and evaluations
            console.print(
                f"\n[cyan]Simulating {args.num_evaluations} battles with "
                f"evaluations...[/cyan]"
            )
            battles, evaluations = simulate_battles_and_evaluations(
                models,
                args.num_evaluations,
                user_id=user_id,
                tie_probability=args.tie_probability,
                seed=args.seed,
            )

            # Display summary
            console.print()
            display_evaluation_summary(evaluations)

            # Insert into database or display sample
            if args.dry_run:
                console.print(
                    "\n[yellow]DRY RUN: Data not written to database[/yellow]"
                )

                # Display sample of generated data
                console.print("\n[cyan]Sample of generated evaluations:[/cyan]")
                sample_table = Table(title="First 20 Evaluations")
                sample_table.add_column("#", justify="right", style="dim")
                sample_table.add_column("Model 1", style="cyan")
                sample_table.add_column("vs", justify="center", style="dim")
                sample_table.add_column("Model 2", style="cyan")
                sample_table.add_column("Outcome", style="yellow")

                for i, eval_data in enumerate(evaluations[:20], 1):
                    outcome_style = (
                        "green"
                        if eval_data["outcome"] == "model1"
                        else "red"
                        if eval_data["outcome"] == "model2"
                        else "blue"
                    )
                    sample_table.add_row(
                        str(i),
                        eval_data["model1_name"],
                        "vs",
                        eval_data["model2_name"],
                        f"[{outcome_style}]{eval_data['outcome']}[/{outcome_style}]",
                    )

                console.print(sample_table)
            else:
                console.print(
                    f"\n[cyan]Inserting {len(battles)} battles and "
                    f"{len(evaluations)} evaluations into database...[/cyan]"
                )
                battle_count = insert_battles_batch(conn, battles)
                console.print(f"[green]Inserted {battle_count} battles[/green]")

                eval_count = insert_battle_evaluations_batch(conn, evaluations)
                console.print(f"[green]Inserted {eval_count} evaluations[/green]")

        console.print("\n[bold green]Simulation completed successfully![/bold green]")

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        import traceback

        console.print(traceback.format_exc())
        sys.exit(1)


if __name__ == "__main__":
    main()
