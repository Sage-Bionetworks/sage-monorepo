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
from datetime import UTC, datetime
from typing import Any

from rich.console import Console
from rich.table import Table

from bixarena_tools.evaluation.db_helper import (
    DatabaseConfig,
    fetch_active_models,
    fetch_battle_evaluations,
    fetch_leaderboard_ids,
    fetch_leaderboards,
    get_db_connection,
    insert_leaderboard_entries,
    insert_leaderboard_snapshot,
)
from bixarena_tools.evaluation.rank_battle import compute_leaderboard_bt

console = Console()


def display_leaderboard_summary(
    leaderboard_name: str, entries: list[dict[str, Any]], evaluation_count: int
) -> None:
    """
    Display leaderboard results in a formatted table.

    Args:
        leaderboard_name: Name of the leaderboard
        entries: List of leaderboard entries
        evaluation_count: Total number of evaluations used
    """
    console.print(f"\n[bold cyan]═══ {leaderboard_name} ═══[/bold cyan]")
    console.print(f"Total evaluations: {evaluation_count}")
    console.print(f"Models ranked: {len(entries)}\n")

    if not entries:
        console.print("[yellow]No entries to display[/yellow]")
        return

    # Sort by rank first, then by BT score descending
    sorted_entries = sorted(entries, key=lambda x: (x["rank"], -x["btScore"]))

    table = Table(title=f"{leaderboard_name} Rankings")
    table.add_column("Rank", justify="center", style="bold")
    table.add_column("Model", style="cyan")
    table.add_column("BT Score", justify="right", style="green")
    table.add_column("95% CI", justify="center", style="yellow")
    table.add_column("Evals", justify="center", style="blue")
    table.add_column("License", style="magenta")

    for entry in sorted_entries[:20]:  # Show top 20
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
        "--id",
        type=str,
        default="overall",
        help="Leaderboard ID to update (default: 'overall')",
    )
    parser.add_argument(
        "--dry-run",
        action="store_true",
        help="Compute and display results without writing to database",
    )
    parser.add_argument(
        "--min",
        type=int,
        default=0,
        help=(
            "Minimum evaluations per model to include in leaderboard "
            "(default: 0/disabled, recommended: 20)"
        ),
    )

    args = parser.parse_args()

    # Create database config from environment variables
    db_config = DatabaseConfig(
        host=os.getenv("POSTGRES_HOST"),
        port=int(p) if (p := os.getenv("POSTGRES_PORT")) else None,
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

            # Validate leaderboard ID
            available_ids = fetch_leaderboard_ids(conn)
            if args.id not in available_ids:
                console.print(
                    f"[bold red]Error: Leaderboard '{args.id}' not found.[/bold red]"
                )
                console.print(f"Available leaderboards: {', '.join(available_ids)}")
                sys.exit(1)

            all_models = fetch_active_models(conn)
            console.print(f"  ✓ Found {len(all_models)} active models")

            all_evaluations = fetch_battle_evaluations(conn)
            console.print(f"  ✓ Found {len(all_evaluations)} battle evaluations")

            # Create model lookup by name
            models_by_name = {model["name"]: model for model in all_models}

            # Process the specified leaderboard
            leaderboard_found = False
            for leaderboard in leaderboards:
                leaderboard_slug = leaderboard["slug"]

                # Only process the specified leaderboard
                if leaderboard_slug != args.id:
                    continue

                leaderboard_found = True
                console.print(
                    f"\n[bold]Processing leaderboard: {leaderboard['name']}[/bold]"
                )

                # Use all evaluations for the leaderboard
                console.print(f"  • Using {len(all_evaluations)} total evaluations")

                if len(all_evaluations) == 0:
                    console.print(
                        "[bold red]Error: No evaluations found in database.[/bold red]"
                    )
                    sys.exit(1)

                # Compute rankings
                console.print(
                    f"  • Computing rankings with {args.num_bootstrap} "
                    "bootstrap iterations..."
                )
                leaderboard_entries = compute_leaderboard_bt(
                    evaluations=all_evaluations,
                    models=models_by_name,
                    num_bootstrap=args.num_bootstrap,
                )

                # Filter by minimum evaluations if threshold is set
                if args.min > 0:
                    skipped_models = []

                    filtered_entries = []
                    for entry in leaderboard_entries:
                        if entry["voteCount"] >= args.min:
                            filtered_entries.append(entry)
                        else:
                            skipped_models.append(
                                (entry["modelName"], entry["voteCount"])
                            )

                    leaderboard_entries = filtered_entries

                    if skipped_models:
                        console.print(
                            f"\n[yellow]⚠ Warning: Skipped "
                            f"{len(skipped_models)} model(s) "
                            f"with < {args.min} evaluations:[/yellow]"
                        )
                        for model_name, count in skipped_models[:5]:  # Show first 5
                            console.print(f"  • {model_name}: {count} evaluations")
                        if len(skipped_models) > 5:
                            console.print(f"  • ... and {len(skipped_models) - 5} more")

                # Display results
                display_leaderboard_summary(
                    leaderboard["name"], leaderboard_entries, len(all_evaluations)
                )

                console.print(
                    "\n[bold green]✓ Leaderboard computation completed![/bold green]"
                )

                # Insert into database if not dry run
                if not args.dry_run:
                    # Create snapshot identifier with timestamp
                    timestamp = datetime.now(UTC).strftime("%Y%m%d_%H%M%S")
                    snapshot_identifier = f"snapshot_{timestamp}"
                    description = (
                        f"Leaderboard snapshot with {len(all_evaluations)} "
                        f"evaluations and {len(leaderboard_entries)} ranked models"
                    )

                    # Insert snapshot
                    snapshot_id = insert_leaderboard_snapshot(
                        conn,
                        leaderboard["id"],
                        snapshot_identifier,
                        description,
                    )

                    # Insert entries
                    entry_count = insert_leaderboard_entries(
                        conn, leaderboard["id"], snapshot_id, leaderboard_entries
                    )

                    console.print(
                        "\n[bold green]✓ Database updated successfully![/bold green]"
                    )
                    console.print(f"  • Snapshot ID: {snapshot_id}")
                    console.print(f"  • Entries inserted: {entry_count}")

            if not leaderboard_found:
                console.print(
                    f"[bold red]Error: Leaderboard '{args.id}' exists "
                    f"but was not retrieved.[/bold red]"
                )
                sys.exit(1)

            # Show dry run message if applicable
            if args.dry_run:
                console.print(
                    "\n[yellow]DRY RUN: Results displayed only. "
                    "Database not updated.[/yellow]"
                )

    except Exception as e:
        console.print(f"[bold red]Error: {e}[/bold red]")
        import traceback

        console.print(traceback.format_exc())
        sys.exit(1)


if __name__ == "__main__":
    main()
