"""
Update BixArena leaderboards with latest battle evaluation data.

This script:
1. Fetches battle evaluations from the database
2. Computes Bradley-Terry rankings for each leaderboard
3. Displays results for review (currently does not write to DB)
"""

import argparse
import os
import sys
from typing import Any

from rich.console import Console
from rich.table import Table

from bixarena_tools.evaluation.db_helper import (
    DatabaseConfig,
    fetch_active_models,
    fetch_battle_evaluations,
    fetch_leaderboards,
    get_db_connection,
)
from bixarena_tools.evaluation.rank_battle import compute_leaderboard_bt

console = Console()


def filter_votes_by_license(
    votes: list[dict[str, Any]], models_by_name: dict[str, dict], target_license: str
) -> list[dict[str, Any]]:
    """
    Filter votes to only include models with specified license.

    Args:
        votes: List of vote dictionaries
        models_by_name: Dict mapping model name to model info
        target_license: License to filter for (e.g., 'open-source', 'commercial')

    Returns:
        Filtered list of votes
    """
    filtered_votes = []
    for vote in votes:
        model1 = models_by_name.get(vote["model1_name"])
        model2 = models_by_name.get(vote["model2_name"])

        # Only include vote if both models have the target license
        if (
            model1
            and model2
            and model1.get("license") == target_license
            and model2.get("license") == target_license
        ):
            filtered_votes.append(vote)

    return filtered_votes


def display_leaderboard_summary(
    leaderboard_name: str, entries: list[dict[str, Any]], vote_count: int
) -> None:
    """
    Display leaderboard results in a formatted table.

    Args:
        leaderboard_name: Name of the leaderboard
        entries: List of leaderboard entries
        vote_count: Total number of votes used
    """
    console.print(f"\n[bold cyan]═══ {leaderboard_name} ═══[/bold cyan]")
    console.print(f"Total votes: {vote_count}")
    console.print(f"Models ranked: {len(entries)}\n")

    if not entries:
        console.print("[yellow]No entries to display[/yellow]")
        return

    table = Table(title=f"{leaderboard_name} Rankings")
    table.add_column("Rank", justify="center", style="bold")
    table.add_column("Model", style="cyan")
    table.add_column("BT Score", justify="right", style="green")
    table.add_column("95% CI", justify="center", style="yellow")
    table.add_column("Votes", justify="center", style="blue")
    table.add_column("License", style="magenta")

    for entry in entries[:20]:  # Show top 20
        ci_str = f"[{entry['bootstrapQ025']:.1f}, {entry['bootstrapQ975']:.1f}]"
        table.add_row(
            str(entry["rank"]),
            entry["modelName"],
            f"{entry['btScore']:.2f}",
            ci_str,
            str(entry["voteCount"]),
            entry["license"],
        )

    console.print(table)


def main():
    """Main entry point for leaderboard update script."""
    parser = argparse.ArgumentParser(
        description="Update BixArena leaderboards from battle evaluations"
    )
    parser.add_argument(
        "--num-bootstrap",
        type=int,
        default=100,
        help="Number of bootstrap iterations for confidence intervals (default: 100)",
    )
    parser.add_argument(
        "--leaderboard",
        type=str,
        help="Only update specific leaderboard by slug (e.g., 'open-source')",
    )

    args = parser.parse_args()

    # Create database config from environment variables
    db_config = DatabaseConfig(
        host=os.getenv("POSTGRES_HOST"),
        port=int(os.getenv("POSTGRES_PORT", "5432")),
        database=os.getenv("POSTGRES_DB"),
        user=os.getenv("POSTGRES_USER"),
        password=os.getenv("POSTGRES_PASSWORD"),
    )

    try:
        console.print("[bold blue]BixArena Leaderboard Update[/bold blue]")
        console.print("=" * 60)

        with get_db_connection(db_config) as conn:
            # Fetch all data needed
            console.print("\n[cyan]Fetching data from database...[/cyan]")

            leaderboards = fetch_leaderboards(conn)
            console.print(f"  ✓ Found {len(leaderboards)} leaderboards")

            all_models = fetch_active_models(conn)
            console.print(f"  ✓ Found {len(all_models)} active models")

            all_votes = fetch_battle_evaluations(conn)
            console.print(f"  ✓ Found {len(all_votes)} battle evaluations")

            # Create model lookup by name
            models_by_name = {model["name"]: model for model in all_models}

            # Process each leaderboard
            for leaderboard in leaderboards:
                leaderboard_slug = leaderboard["slug"]

                # Skip if filtering by leaderboard
                if args.leaderboard and leaderboard_slug != args.leaderboard:
                    continue

                console.print(
                    f"\n[bold]Processing leaderboard: {leaderboard['name']}[/bold]"
                )

                # Determine license filter based on slug
                # Assuming slug is 'open-source' or 'commercial'
                if "open" in leaderboard_slug.lower():
                    target_license = "open-source"
                elif "commercial" in leaderboard_slug.lower():
                    target_license = "commercial"
                else:
                    console.print(
                        f"[yellow]  ⚠ Unknown leaderboard slug: {leaderboard_slug}, "
                        "skipping[/yellow]"
                    )
                    continue

                # Filter votes by license
                filtered_votes = filter_votes_by_license(
                    all_votes, models_by_name, target_license
                )
                console.print(
                    f"  • Filtered to {len(filtered_votes)} votes for "
                    f"{target_license} models"
                )

                if len(filtered_votes) == 0:
                    console.print("[yellow]  ⚠ No votes found, skipping[/yellow]")
                    continue

                # Compute rankings
                console.print(
                    f"  • Computing rankings with {args.num_bootstrap} "
                    "bootstrap iterations..."
                )
                leaderboard_entries = compute_leaderboard_bt(
                    votes=filtered_votes,
                    models=models_by_name,
                    num_bootstrap=args.num_bootstrap,
                )

                # Display results
                display_leaderboard_summary(
                    leaderboard["name"], leaderboard_entries, len(filtered_votes)
                )

        console.print("\n[bold green]✓ Leaderboard computation completed![/bold green]")
        console.print(
            "[yellow]Note: Results displayed only. Database not updated.[/yellow]"
        )

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        import traceback

        console.print(traceback.format_exc())
        sys.exit(1)


if __name__ == "__main__":
    main()
