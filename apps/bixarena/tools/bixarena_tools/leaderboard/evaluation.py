"""CLI commands for managing battle evaluations."""

import random
import sys
import uuid
from datetime import UTC, datetime
from typing import Any

import typer
from rich.console import Console
from rich.table import Table

from .db_helper import (
    DatabaseConfig,
    fetch_active_models,
    get_db_connection,
    insert_battle_evaluations_batch,
    insert_battles_batch,
)

evaluation_app = typer.Typer(
    help="Manage battle evaluations (simulate, clean, etc.)",
    no_args_is_help=True,
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


@evaluation_app.command("simulate")
def simulate(
    num_evaluations: int = typer.Option(
        100,
        "-n",
        "--num-evaluations",
        help="Number of evaluations to generate",
    ),
    tie_probability: float = typer.Option(
        0.05,
        "--tie-probability",
        help="Probability of tie outcomes",
    ),
    seed: int = typer.Option(
        None,
        "--seed",
        help="Random seed for reproducibility",
    ),
    dry_run: bool = typer.Option(
        False,
        "--dry-run",
        help="Simulate without writing to database",
    ),
):
    """
    Generate simulated battle evaluations for testing.

    This command creates synthetic battles with realistic evaluations based on
    model performance characteristics that align with Bradley-Terry ranking metrics.
    """
    # Generate random seed if not provided
    if seed is None:
        seed = random.randint(0, 2**31 - 1)
        console.print(f"[dim]Using random seed: {seed}[/dim]")

    # Use dummy user ID for simulations
    user_id = "00000000-0000-0000-0000-000000000000"

    try:
        console.print("[bold blue]BixArena Evaluation Simulator[/bold blue]")
        console.print("=" * 60)

        # Create database config from env vars
        db_config = DatabaseConfig.from_env()

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
                f"\n[cyan]Simulating {num_evaluations} battles with "
                f"evaluations...[/cyan]"
            )
            battles, evaluations = simulate_battles_and_evaluations(
                models,
                num_evaluations,
                user_id=user_id,
                tie_probability=tie_probability,
                seed=seed,
            )

            # Display summary
            console.print()
            display_evaluation_summary(evaluations)

            # Insert into database or display sample
            if dry_run:
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

                # Show total evaluations in database after insertion
                with conn.cursor() as cur:
                    cur.execute("SELECT COUNT(*) FROM api.battle_evaluation")
                    result = cur.fetchone()
                    total_evals = result["count"] if result else 0  # type: ignore
                console.print(
                    f"[cyan]Total evaluations in database: {total_evals}[/cyan]"
                )

        console.print("\n[bold green]Simulation completed successfully![/bold green]")

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        import traceback

        console.print(traceback.format_exc())
        sys.exit(1)


def clean_evaluations_db(conn) -> tuple[int, int]:
    """
    Delete all battle evaluations and battles from the database.

    Args:
        conn: Database connection

    Returns:
        Tuple of (evaluation_count, battle_count) deleted
    """
    with conn.cursor() as cur:
        # Delete evaluations first (due to foreign key constraint)
        cur.execute("DELETE FROM api.battle_evaluation")
        eval_count = cur.rowcount

        # Delete battles
        cur.execute("DELETE FROM api.battle")
        battle_count = cur.rowcount

        conn.commit()

        return eval_count, battle_count


@evaluation_app.command("clean")
def clean(
    yes: bool = typer.Option(
        False,
        "-y",
        "--yes",
        help="Skip confirmation prompt",
    ),
):
    """
    Clean all battles and evaluations from the database.

    WARNING: This is a destructive operation that will delete ALL battles and
    their associated evaluations. Use with caution!
    """
    try:
        console.print("[bold red]⚠ Database Cleanup Tool[/bold red]")
        console.print("=" * 60)

        # Create database config from env vars
        db_config = DatabaseConfig.from_env()

        with get_db_connection(db_config) as conn:
            # Get current counts
            with conn.cursor() as cur:
                cur.execute("SELECT COUNT(*) FROM api.battle_evaluation")
                eval_count = cur.fetchone()["count"]

                cur.execute("SELECT COUNT(*) FROM api.battle")
                battle_count = cur.fetchone()["count"]

            console.print("\n[yellow]Current database state:[/yellow]")
            console.print(f"  • Battles: {battle_count}")
            console.print(f"  • Evaluations: {eval_count}")

            if battle_count == 0 and eval_count == 0:
                console.print("\n[green]Database is already clean![/green]")
                return

            # Confirmation prompt
            if not yes:
                console.print(
                    "\n[bold red]WARNING: This will delete ALL battles and "
                    "evaluations![/bold red]"
                )
                response = console.input("[yellow]Are you sure? (yes/no): [/yellow]")
                if response.lower() not in ["yes", "y"]:
                    console.print("[cyan]Operation cancelled.[/cyan]")
                    return

            # Perform cleanup
            deleted_evals, deleted_battles = clean_evaluations_db(conn)

            console.print(f"\n[green]✓ Deleted {deleted_evals} evaluations[/green]")
            console.print(f"[green]✓ Deleted {deleted_battles} battles[/green]")
            console.print("\n[bold green]Database cleaned successfully![/bold green]")

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        import traceback

        console.print(traceback.format_exc())
        sys.exit(1)
