"""
Modern CLI for BixArena leaderboard generation with multiple output formats.
"""

import json
import time
from typing import Annotated

import typer
from rich.console import Console
from rich.table import Table

from bixarena_tools.rank_battle import compute_leaderboard_bt
from bixarena_tools.simulate_battle_votes import (
    SimConfig,
    simulate_votes,
)

app = typer.Typer(help="BixArena Bradley-Terry evaluation CLI")
console = Console()


def format_table_output(leaderboard: list[dict]) -> None:
    """Format leaderboard data as a rich table."""
    if not leaderboard:
        console.print("(no results)")
        return

    table = Table(title="🏆 Model Ranking Leaderboard")
    table.add_column("Rank", justify="center", style="bold")
    table.add_column("Model", style="cyan")
    table.add_column("Score", justify="right", style="green")
    table.add_column("95% CI", justify="center", style="yellow")
    table.add_column("Votes", justify="center", style="blue")

    for entry in leaderboard:
        ci_lower = entry.get("bootstrapQ025", 0)
        ci_upper = entry.get("bootstrapQ975", 0)
        ci_str = f"[{ci_lower:.1f}, {ci_upper:.1f}]"

        table.add_row(
            str(entry["rank"]),
            entry["modelName"],
            f"{entry['btScore']:.3f}",
            ci_str,
            str(entry["voteCount"]),
        )

    console.print(table)


def format_json_output(leaderboard: list[dict]) -> None:
    """Format leaderboard data as JSON."""
    print(json.dumps(leaderboard, indent=2))


@app.callback(invoke_without_command=True)
def main(
    num_models: Annotated[int, typer.Option(help="Number of models to simulate")] = 10,
    num_votes: Annotated[int, typer.Option(help="Number of votes to generate")] = 500,
    num_bootstrap: Annotated[
        int, typer.Option(help="Number of bootstrap iterations")
    ] = 5000,
    tie_probability: Annotated[
        float, typer.Option(help="Probability of tie outcomes")
    ] = 0.05,
    random_seed: Annotated[
        int, typer.Option(help="Random seed for reproducibility")
    ] = 42,
    output: Annotated[str, typer.Option(help="Output format: table|json")] = "table",
    verbose: Annotated[bool, typer.Option(help="Enable verbose logging")] = False,
):
    """Generate BixArena leaderboard using Bradley-Terry evaluation."""

    # Validate output format
    if output not in {"table", "json"}:
        console.print(f"❌ Invalid output format: {output}", style="red")
        console.print("Valid formats: table, json", style="yellow")
        raise typer.Exit(1)

    start_time = time.time()

    if output == "table":
        console.print("🎯 [bold]BixArena Model Evaluation[/bold]")
        console.print("=" * 40)
        console.print("📊 Generating mock vote data...")

    # Generate mock data
    config = SimConfig(
        num_models=num_models,
        num_votes=num_votes,
        tie_probability=tie_probability,
        random_seed=random_seed,
    )

    votes, models = simulate_votes(config)

    if output == "table":
        console.print("🧮 Computing ranking scores and bootstrapping...")

    # Compute leaderboard
    leaderboard = compute_leaderboard_bt(
        votes=votes,
        models=models,
        num_bootstrap=num_bootstrap,
    )

    # Calculate runtime for all formats
    total_time = time.time() - start_time

    # Output results based on format
    if output == "table":
        format_table_output(leaderboard)
    elif output == "json":
        format_json_output(leaderboard)

    # Always show the same performance metrics for all formats
    console.print("\n✅ Evaluation completed successfully!")
    console.print(f"⏱️  Total runtime: {total_time:.3f} seconds")
    console.print(f"   • Models evaluated: {len(leaderboard)}")
    total_votes = sum(entry["voteCount"] for entry in leaderboard)
    console.print(f"   • Total votes processed: {total_votes}")
    console.print(f"   • Bootstrap iterations: {num_bootstrap}")
    if leaderboard:
        top_model = leaderboard[0]
        console.print(
            f"   • Top model: {top_model['modelName']} "
            f"(Score: {top_model['btScore']:.3f})"
        )


if __name__ == "__main__":
    app()
